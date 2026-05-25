package com.i.common.attendance.ui.home.leave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentLeaveViewListUnnatiBinding
import com.i.common.attendance.network.request.ViewLeaveListUnnatiRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.PromotionalActivityViewModel
import com.i.common.attendance.ui.home.leave.adapter.LeaveRequestAdapter
import com.i.common.attendance.ui.home.leave.viewmodel.ViewLeaveListState
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class ViewLeaveListUnnatiFragment : BaseFragment() {

    // ─────────────────────────────────────────────────────────────────────────
    //  Binding / ViewModel / DI
    // ─────────────────────────────────────────────────────────────────────────

    private lateinit var binding: FragmentLeaveViewListUnnatiBinding

    @Inject lateinit var shredPref: EncryptedPrefHelper

    private val targetOutStandingViewModel: PromotionalActivityViewModel by viewModels()

    // ─────────────────────────────────────────────────────────────────────────
    //  Adapter
    // ─────────────────────────────────────────────────────────────────────────

    private val leaveRequestAdapter by lazy {
        LeaveRequestAdapter(
            showApprovalButtons = false,
            onApproveClick = { item ->

            },
            onRejectClick = { item ->

            }
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaveViewListUnnatiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        setupRecyclerView()
        moveOnClickListener()
        observeViewLeaveList()
        initApiCall()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  API
    // ─────────────────────────────────────────────────────────────────────────

    private fun initApiCall() = with(binding) {
        targetOutStandingViewModel.viewLeaveListUnnati(
            request = ViewLeaveListUnnatiRequest(
                frmDt = txtFromDate.getTrimmedText(),
                toDt  = txtToDate.getTrimmedText(),
                empId = shredPref.getUser()?.MobileNo ?: ""
            )
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  RecyclerView
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupRecyclerView() = with(binding) {
        recyclerViewLeaveList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = leaveRequestAdapter
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Toolbar
    // ─────────────────────────────────────────────────────────────────────────

    private fun manageToolBar() = with(binding) {
        txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))

        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.label_leave_view))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Click listeners
    // ─────────────────────────────────────────────────────────────────────────

    private fun moveOnClickListener() = with(binding) {
        txtFromDate.setSafeOnClickListener { openDatePicker(isFromDate = true) }
        txtToDate.setSafeOnClickListener   { openDatePicker(isFromDate = false) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Date picker
    // ─────────────────────────────────────────────────────────────────────────

    private fun openDatePicker(isFromDate: Boolean) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                .format(Date(selection))

            if (isFromDate) binding.txtFromDate.setText(selectedDate)
            else            binding.txtToDate.setText(selectedDate)

            initApiCall()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Confirmation dialog
    // ─────────────────────────────────────────────────────────────────────────

    private fun showConfirmationDialog(
        title: String,
        message: String,
        onYesClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ -> dialog.dismiss(); onYesClick() }
            .setNegativeButton("No")  { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ViewModel observer
    // ─────────────────────────────────────────────────────────────────────────

    private fun observeViewLeaveList() {
        targetOutStandingViewModel.viewLeaveListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewLeaveListState.Loading      -> showLoader()
                is ViewLeaveListState.Success      -> { hideLoader(); leaveRequestAdapter.submitList(state.list) }
                is ViewLeaveListState.ApiError     -> { hideLoader(); leaveRequestAdapter.submitList(emptyList()); showToast(state.message) }
                is ViewLeaveListState.NetworkError -> { hideLoader(); showToast(state.message) }
                else                               -> Unit
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UI helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun showLoader() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBarPJC.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarPJC.visibility = View.GONE
    }
}