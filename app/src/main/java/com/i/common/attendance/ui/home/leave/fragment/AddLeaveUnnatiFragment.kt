package com.i.common.attendance.ui.home.leave.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentAddLeaveUnnatiBinding
import com.i.common.attendance.network.request.AddLeaveUnnatiRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.PromotionalActivityViewModel
import com.i.common.attendance.ui.home.leave.viewmodel.LeaveRequestState
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class AddLeaveUnnatiFragment : BaseFragment() {

    private lateinit var binding : FragmentAddLeaveUnnatiBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val targetOutStandingViewModel: PromotionalActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddLeaveUnnatiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        moveOnClickListener()
        observeInsertLeaveApi()
    }

    private fun moveOnClickListener() = with(binding) {
        txtFromDate.setSafeOnClickListener {
            openDatePicker(true)
        }
        txtToDate.setSafeOnClickListener {
            openDatePicker(false)
        }
        btnSubmit.setSafeOnClickListener {
            if(isValidFields()){
                val leaveFor = when (radioGroupLeaveFor.checkedRadioButtonId) {
                    R.id.radioButtonFirstHalf -> "First Half"
                    R.id.radioButtonSecondHalf -> "Second Half"
                    R.id.radioButtonFullDay -> "Full Day"
                    else -> ""
                }

                val request = AddLeaveUnnatiRequest(
                    frmDt = binding.txtFromDate.text.toString().trim(),
                    toDt = binding.txtToDate.text.toString().trim(),
                    empId = shredPref.getUser()?.EmpID?:"",
                    empName = shredPref.getUser()?.UsersName?:"",
                    leaveReason = binding.txtExtraExpenseDetails.text.toString().trim(),
                    leaveFor = leaveFor,
                    approvedDisapproved = "S",
                    userId = shredPref.getUser()?.AutoId?:""
                )

                targetOutStandingViewModel.insertLeaveRequestUnnati(request)

            }
        }
    }
    private fun isValidFields(): Boolean {

        val fromDate = binding.txtFromDate.text.toString().trim()
        val toDate = binding.txtToDate.text.toString().trim()
        val reason = binding.txtExtraExpenseDetails.text.toString().trim()

        if (fromDate.isEmpty()) {
           showToast("Please select from date")
            return false
        }

        if (toDate.isEmpty()) {
           showToast("Please select to date")
            return false
        }

        if (binding.radioGroupLeaveFor.checkedRadioButtonId == -1) {
           showToast("Please select leave type")
            return false
        }

        if (reason.isEmpty()) {
           showToast("Please enter reason for leave")
            return false
        }

        return true
    }


    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.label_leave))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun openDatePicker(isFromDate: Boolean) {

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->

            val selectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(selection))
            if (isFromDate) {
                binding.txtFromDate.setText(selectedDate)
            } else {
                binding.txtToDate.setText(selectedDate)
            }

        }
    }

    private fun observeInsertLeaveApi(){
        targetOutStandingViewModel.leaveRequestState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LeaveRequestState.Loading -> {
                    showLoader()
                }

                is LeaveRequestState.Success -> {
                    hideLoader()
                    showToast(state.response.message)
                    parentFragmentManager.popBackStackImmediate()
                }

                is LeaveRequestState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is LeaveRequestState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
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