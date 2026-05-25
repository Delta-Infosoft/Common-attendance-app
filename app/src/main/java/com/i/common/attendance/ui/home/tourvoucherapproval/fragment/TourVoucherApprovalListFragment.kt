package com.i.common.attendance.ui.home.tourvoucherapproval.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentTourVoucherApprovalListBinding
import com.i.common.attendance.network.request.TourVoucherApprovalListUpdateStatusRequest
import com.i.common.attendance.network.request.TourVoucherRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.tourvoucherapproval.adapter.TourVoucherApprovalListAdapter
import com.i.common.attendance.ui.home.tourvoucherapproval.viewmodel.TourVoucherApprovalViewModel
import com.i.common.attendance.ui.home.tourvoucherapproval.viewmodel.TourVoucherUiState
import com.i.common.attendance.ui.home.tourvoucherapproval.viewmodel.UpdateExpenseStatusUiState
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TourVoucherApprovalListFragment : BaseFragment() {
    private lateinit var binding : FragmentTourVoucherApprovalListBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val tourVoucherApprovalViewmodel: TourVoucherApprovalViewModel by viewModels()
    private val tourVoucherListAdapter by lazy {
        TourVoucherApprovalListAdapter(
            onSubmitClick = { item ->
                val user = shredPref.getUser()
                // ✅ All business logic here
                tourVoucherApprovalViewmodel.updateExpenseStatus(request = TourVoucherApprovalListUpdateStatusRequest(
                    empId = user?.EmpID?:"",
                    remark = item.ApprovedDisapprovedRemarks ?:"",
                    approvalStatus = item.ApprovedDisapproved ?:"D",
                    expenseId = item.ExpenseId ?:""
                ))
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTourVoucherApprovalListBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageToolBar()
        manageOnClickListeners()
        manageRecyclerView()

        observeVoucherList()
        observeSubmitResult()
    }
    private fun manageRecyclerView() = with(binding){
        val user = shredPref.getUser()
        tourVoucherApprovalViewmodel.loadTourVoucherList(
            TourVoucherRequest(
                MobileNo = user?.MobileNo ?: "",
                FromDt = txtFromDate.getTrimmedText(),
                ToDt = txtToDate.getTrimmedText(),
            )
        )
        recyclerViewTourVoucherApproval.apply {
            adapter = tourVoucherListAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_tour_voucher_approval))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun manageOnClickListeners() = with(binding){
        txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))

        txtFromDate.setSafeOnClickListener {
            openDatePicker(true)
        }
        txtToDate.setSafeOnClickListener {
            openDatePicker(false)
        }
    }
    private fun openDatePicker(isFromDate: Boolean) = with(binding){

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraints = CalendarConstraints.Builder()
            .setEnd(today)
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today) // Default = today
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->

            val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val selectedDate = sdf.format(Date(selection))

            if(isFromDate){
                txtFromDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    val user = shredPref.getUser()
                    tourVoucherApprovalViewmodel.loadTourVoucherList(
                        TourVoucherRequest(
                            MobileNo = user?.MobileNo ?: "",
                            FromDt = txtFromDate.getTrimmedText(),
                            ToDt = txtToDate.getTrimmedText(),
                        )
                    )
                }
            }else{
                txtToDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    val user = shredPref.getUser()
                    tourVoucherApprovalViewmodel.loadTourVoucherList(
                        TourVoucherRequest(
                            MobileNo = user?.MobileNo ?: "",
                            FromDt = txtFromDate.getTrimmedText(),
                            ToDt = txtToDate.getTrimmedText(),
                        )
                    )
                }
            }
        }
    }
    private fun observeVoucherList(){
        tourVoucherApprovalViewmodel.tourVoucherState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TourVoucherUiState.Loading -> {
                    showLoader()
                }
                is TourVoucherUiState.Success -> {
                    hideLoader()
                    tourVoucherListAdapter.submitList(state.list)
                }
                is TourVoucherUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is TourVoucherUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }

    // Observe submit result (add this in observeVoucherList or separately)
    private fun observeSubmitResult() {
        tourVoucherApprovalViewmodel.updateExpenseStatusState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UpdateExpenseStatusUiState.Loading -> showLoader()
                is UpdateExpenseStatusUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    refreshList()   // reload list after approval
                }
                is UpdateExpenseStatusUiState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }

    private fun refreshList() {
        val user = shredPref.getUser()
        tourVoucherApprovalViewmodel.loadTourVoucherList(
            TourVoucherRequest(
                MobileNo = user?.MobileNo ?: "",
                FromDt   = binding.txtFromDate.getTrimmedText(),
                ToDt     = binding.txtToDate.getTrimmedText(),
            )
        )
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