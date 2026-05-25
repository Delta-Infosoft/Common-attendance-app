package com.i.common.attendance.ui.home.ledgerreport.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.LedgerReportFragmentBinding
import com.i.common.attendance.network.request.GetCustomerRequest
import com.i.common.attendance.network.request.GetLedgerPdfRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.ledgerreport.data.LedgerPdfGenerator
import com.i.common.attendance.ui.home.ledgerreport.data.LedgerPdfParams
import com.i.common.attendance.ui.home.ledgerreport.data.LedgerRowItem
import com.i.common.attendance.ui.home.ledgerreport.data.PdfResult
import com.i.common.attendance.ui.home.ledgerreport.viewmodel.CustomerUiState
import com.i.common.attendance.ui.home.ledgerreport.viewmodel.LedgerPdfShowUiState
import com.i.common.attendance.ui.home.ledgerreport.viewmodel.LedgerPdfUiState
import com.i.common.attendance.ui.home.ledgerreport.viewmodel.LedgerReportViewModel
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.i.common.attendance.ui.home.ledgerreport.adapter.LedgerReportAdapter

@AndroidEntryPoint
class LedgerReportFragment : BaseFragment() {

    private lateinit var binding: LedgerReportFragmentBinding
    private val ledgerReportViewModel: LedgerReportViewModel by viewModels()
    private val ledgerReportAdapter by lazy {
        LedgerReportAdapter()
    }
    private var selectedCustomerId   = ""
    private var selectedDivisionId   = ""
    private var selectedDivisionName = ""
    private var pdfViewLink = ""

    // Keep the last successful raw balance value so the PDF can decide colour
    private var currentBalanceRaw = 0.0

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LedgerReportFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
            txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
            txtLayDivision.visibility    = View.GONE
            txtLayEmployeeName.visibility = View.GONE
            txtLayDistrict.visibility    = View.GONE
            btnPDFView.visibility = View.GONE
        }

        callCustomerApi()
        observeCustomerState()
        observeLedgerPdfState()
        observeLedgerPdfShowState()
        manageToolBar()
        setupClickListeners()
        setRecyclerViewAdapter()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Click listeners
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupClickListeners() = with(binding) {

        btnCloseFilter.setSafeOnClickListener {
            val showing = constViewFilter.isVisible
            btnCloseFilter.text         = if (showing) "Show Filter" else "Close Filter"
            constViewFilter.visibility  = if (showing) View.GONE    else View.VISIBLE
        }

        txtFromDate.setSafeOnClickListener {
            Constants.hideKeyboard(it)
            openDatePicker(isFromDate = true)
        }
        txtToDate.setSafeOnClickListener   {
            Constants.hideKeyboard(it)
            openDatePicker(isFromDate = false)
        }

        txtCustomerName.setSafeOnClickListener {
            val list = ledgerReportViewModel.getCachedCustomerList() ?: return@setSafeOnClickListener
            SelectCustomerNameBottomSheetFragment.newInstance(list)
                .also { sheet ->
                    sheet.setDismissCallback { selected ->
                        Constants.hideKeyboard(it)
                        txtViewPartyName.text = selected.Name
                        txtCustomerName.setText(selected.Name)
                        selectedCustomerId = selected.LgrId ?: ""
                    }
                }
                .show(childFragmentManager, "SelectPlanFor")
        }

        btnFilter.setSafeOnClickListener {
            pdfViewLink = ""
            btnPDFView.visibility = View.GONE
            btnPDFViewEyes.visibility = View.GONE
            Constants.hideKeyboard(it)
            callLedgerPdfApi()
        }

        // "View PDF" button – visible only after PDF is ready
        btnPDFView.setSafeOnClickListener {
            Constants.hideKeyboard(it)
            generateAndSharePdf()
        }
        btnPDFViewEyes.setSafeOnClickListener {
            Constants.hideKeyboard(it)
            if(pdfViewLink.isNotEmpty()){
                openPdfUrl(pdfViewLink)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Toolbar
    // ─────────────────────────────────────────────────────────────────────────

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_ledger_report))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun setRecyclerViewAdapter() = with(binding){
        recyclerViewLedgerReport.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ledgerReportAdapter
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API calls
    // ─────────────────────────────────────────────────────────────────────────

    private fun callCustomerApi() {
        ledgerReportViewModel.loadCustomerList(
            GetCustomerRequest(customerName = "", districtId = "", cityId = "")
        )
    }

    private fun callLedgerPdfApi() {
        binding.btnPDFView.visibility = View.GONE       // hide until fresh data arrives
        ledgerReportViewModel.loadLedgerPdf(buildLedgerRequest())
    }

    private fun callLedgerPdfShowApi() {
        ledgerReportViewModel.loadLedgerPdfShow(buildLedgerRequest(showPdf = "True"))
    }

    private fun buildLedgerRequest(showPdf: String = "") = GetLedgerPdfRequest(
        lgrId      = selectedCustomerId,
        divisionId = selectedDivisionId,
        branchName = selectedDivisionName,
        fromDt     = binding.txtFromDate.text.toString(),
        toDt       = binding.txtToDate.text.toString(),
        showPdf    = showPdf
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Observers
    // ─────────────────────────────────────────────────────────────────────────

    private fun observeCustomerState() {
        ledgerReportViewModel.customerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CustomerUiState.Idle        -> Unit
                is CustomerUiState.Loading     -> showLoader()
                is CustomerUiState.Success     -> {
                    hideLoader()
                    Log.d(TAG, "Customers loaded: ${state.list.size}")
                }
                is CustomerUiState.ApiError    -> { hideLoader(); showToast(state.message) }
                is CustomerUiState.NetworkError -> { hideLoader(); showToast(state.message) }
            }
        }
    }

    private fun observeLedgerPdfState() = with(binding) {
        ledgerReportViewModel.ledgerPdfState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LedgerPdfUiState.Idle -> Unit
                is LedgerPdfUiState.Loading -> showLoader()
                is LedgerPdfUiState.Success -> {
                    hideLoader()
                    val first = state.data.firstOrNull()
                    pdfViewLink = ""
                    currentBalanceRaw = first?.DrAmt?.toDoubleOrNull() ?: 0.0

                    txtViewAmount.text = "₹${first?.DrAmt?.takeIf { it.isNotBlank() } ?: "0.00"} Dr"
                    txtViewAmountDr.text =
                        "₹${first?.DrAmt?.takeIf { it.isNotBlank() } ?: "0.00"} Dr"
                    txtViewAmountCr.text =
                        "₹${first?.CrAmt?.takeIf { it.isNotBlank() } ?: "0.00"} Cr"

                    ledgerReportAdapter.submitList(state.data)

                    callLedgerPdfShowApi()      // chain to show-pdf call
                }

                is LedgerPdfUiState.ApiError -> {
                    hideLoader();
                    showToast(state.message);
                    btnPDFView.visibility = View.GONE
                    btnPDFViewEyes.visibility = View.GONE
                }

                is LedgerPdfUiState.NetworkError -> {
                    hideLoader();
                    showToast(state.message);
                    btnPDFView.visibility = View.GONE
                    btnPDFViewEyes.visibility = View.GONE
                }

                is LedgerPdfUiState.Empty -> {
                    hideLoader()
                    btnPDFView.visibility = View.GONE
                    btnPDFViewEyes.visibility = View.GONE

                    pdfViewLink = ""
                    currentBalanceRaw = 0.0
                    ledgerReportAdapter.submitList(emptyList())
                    txtViewAmount.text = "₹0.00 Dr"
                    txtViewAmountDr.text = "₹0.00 Dr"
                    txtViewAmountCr.text = "₹0.00 Cr"
                    showToast(state.message)   // No Record Found
                }

            }
        }
    }

    private fun observeLedgerPdfShowState() = with(binding){
        ledgerReportViewModel.ledgerPdfShowState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LedgerPdfShowUiState.Idle        -> Unit
                is LedgerPdfShowUiState.Loading     -> showLoader()
                is LedgerPdfShowUiState.Success     -> {
                    hideLoader()
                    constViewExport.visibility = View.VISIBLE
                    btnPDFView.visibility = View.VISIBLE
                    btnPDFViewEyes.visibility = View.VISIBLE
                    if (state.pdfUrl.isNotEmpty()) {
                        pdfViewLink = state.pdfUrl
                        //openPdfUrl(state.pdfUrl)
                    } else {
                        // No remote URL – fall back to local PDF generation
                        generateAndSharePdf()
                    }
                    binding.btnPDFView.visibility = View.VISIBLE
                }
                is LedgerPdfShowUiState.ApiError    -> { hideLoader(); showToast(state.message) }
                is LedgerPdfShowUiState.NetworkError -> { hideLoader(); showToast(state.message) }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PDF generation (runs on IO, result back on Main)
    // ─────────────────────────────────────────────────────────────────────────

    private fun generateAndSharePdf() {
        val ctx = context ?: return

        val transactions = ledgerReportViewModel.getCachedLedgerPdf().map { item ->
            LedgerRowItem (
                transactionDt = item.TransactionDt ?: "",   // ← was "date", now "transactionDt"
                transactionNo = item.TransactionNo ?: "",
                narration     = item.Narration     ?: "",
                drAmt         = item.DrAmt         ?: "",
                crAmt         = item.CrAmt         ?: ""
            )
        }

        val params = LedgerPdfParams(
            customerName = binding.txtCustomerName.text.toString().trim(),
            fromDate = binding.txtFromDate.text.toString().trim(),
            toDate = binding.txtToDate.text.toString().trim(),
            closingBalance = currentBalanceRaw,
            closingBalanceLabel = binding.txtViewAmount.text.toString().trim(),
            creditLabel = binding.txtViewAmountCr.text.toString().trim(),
            debitLabel = binding.txtViewAmountDr.text.toString().trim(),
            transactions = transactions
        )

        showLoader()

        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                LedgerPdfGenerator.generate(ctx, params)
            }

            hideLoader()

            when (result) {
                is PdfResult.Success -> {
                    showToast("File saved at ${result.savedPath}")
                    startActivity(LedgerPdfGenerator.buildShareIntent(ctx, result.file))
                }
                is PdfResult.Failure -> {
                    Log.e(TAG, "PDF failed: ${result.error}", result.cause)
                    showToast("Could not create PDF: ${result.error}")
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun openPdfUrl(url: String) {
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }.onFailure {
            showToast("Unable to open PDF")
        }
    }

    private fun openDatePicker(isFromDate: Boolean) {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
            .also { picker ->
                picker.show(childFragmentManager, "DATE_PICKER")
                picker.addOnPositiveButtonClickListener { millis ->
                    val formatted = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                        .format(Date(millis))
                    if (isFromDate) binding.txtFromDate.setText(formatted)
                    else            binding.txtToDate.setText(formatted)
                }
            }
    }

    private fun showLoader() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBarHome.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarHome.visibility = View.GONE
    }

    companion object {
        private const val TAG = "LedgerReportFragment"
    }
}