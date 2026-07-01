package com.i.common.attendance.ui.home.dealerwisereport.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentNewDealerwiseTargetEntryBinding
import com.i.common.attendance.network.request.GetMonthForTargetRequest
import com.i.common.attendance.network.response.DealerWiseTarget
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.attendancereport.fragment.SelectMonthBottomSheetFragment
import com.i.common.attendance.ui.home.attendancereport.viewmodel.MonthUiState
import com.i.common.attendance.ui.home.dealerwisereport.adapter.DealerWiseTargetAdapter
import com.i.common.attendance.ui.home.dealerwisereport.viewmodel.DealerWiseTargetUiState
import com.i.common.attendance.ui.home.dealerwisereport.viewmodel.DealerWiseTargetViewModel
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewDealerWiseTargetEntryFragment : BaseFragment() {

    private lateinit var binding : FragmentNewDealerwiseTargetEntryBinding
    private val reportViewmodel: DealerWiseTargetViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val dealerTargetList = mutableListOf<DealerWiseTarget>()
    private val dealerTargetTypeAdapter by lazy {
        DealerWiseTargetAdapter { item, qty ->

            item.qty = qty.toDoubleOrNull() ?: 0.0
            val grandTotal = dealerTargetList.sumOf {
                (it.AvgRate?.toDoubleOrNull() ?: 0.0) * it.qty
            }

            binding.txtViewGrandTotal.text = String.format("%.2f", grandTotal)
        }
    }
    private var selectedDealerId : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewDealerwiseTargetEntryBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        moveOnClickListeners()
        manageInitVisibility()
        setUpAdapter()
        observeMonthList()
        observeTargetTypeList()
    }

    private fun setUpAdapter() = with(binding){
        recyclerViewRecords.apply {
            adapter = dealerTargetTypeAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }
    }

    private fun manageInitVisibility() = with(binding) {
        //reportViewmodel.loadDealerList(DealerDetailsAccountRequest(sharedPref.getUser()?.InsertedByUserId?:""))
        reportViewmodel.loadMonthList(GetMonthForTargetRequest(userId = "b7911d7a-b389-4d9a-9cb5-a10a75ae4659"))

        constViewMonthRecyclerView.visibility = View.VISIBLE
        constraintLayoutData.visibility = View.GONE
        btnFillData.visibility = View.GONE
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_new_dealer_wise_target_entry))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun moveOnClickListeners() = with(binding) {
        txtMonth.setSafeOnClickListener {
            val list = reportViewmodel.getCachedMonthList()
            val bottomSheet = list?.let { it1 -> SelectMonthBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtMonth.setText(selected.Month)
                reportViewmodel.loadDealerWiseTargetType()

                constViewMonthRecyclerView.visibility = View.VISIBLE
                constraintLayoutData.visibility = View.VISIBLE
                btnFillData.visibility = View.VISIBLE
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
    }
    private fun observeMonthList() {
        reportViewmodel.monthListState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is MonthUiState.Loading -> showLoader()

                is MonthUiState.Success -> {
                    hideLoader()
                }

                is MonthUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is MonthUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> {}
            }
        }
    }
    private fun observeTargetTypeList(){
        reportViewmodel.dealerWiseTargetState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is DealerWiseTargetUiState.Loading -> {
                    showLoader()
                }

                is DealerWiseTargetUiState.Success -> {
                    hideLoader()
                    dealerTargetList.clear()
                    dealerTargetList.addAll(state.list)
                    dealerTargetTypeAdapter.submitList(state.list)
                }

                is DealerWiseTargetUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                    dealerTargetList.clear()
                    dealerTargetList.addAll(emptyList())
                    dealerTargetTypeAdapter.submitList(emptyList())
                }

                is DealerWiseTargetUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                    dealerTargetList.clear()
                    dealerTargetList.addAll(emptyList())
                    dealerTargetTypeAdapter.submitList(emptyList())
                }

                else -> Unit
            }
        }
    }

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