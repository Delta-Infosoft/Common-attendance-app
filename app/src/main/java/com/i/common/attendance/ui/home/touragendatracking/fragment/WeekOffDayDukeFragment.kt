package com.i.common.attendance.ui.home.touragendatracking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentWeekOffDayDukeBinding
import com.i.common.attendance.network.request.ValidateSundayRequest
import com.i.common.attendance.network.request.WeekOffRequest
import com.i.common.attendance.network.response.ValidateSundayResult
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.SubmitWeekOffUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.ValidateSundayUiState
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class WeekOffDayDukeFragment : BaseFragment() {

    private lateinit var binding : FragmentWeekOffDayDukeBinding
    private val tourAgendaViewModel: TourAgendaTrackingViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekOffDayDukeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        manageOnClickListeners()
        observeValidateSunday()
        observeSubmitWeekOff()
    }

    private fun initApiCall() = with(binding){
        val user = sharedPref.getUser()
        tourAgendaViewModel.validateSunday(
            request = ValidateSundayRequest(empId = user?.EmpID?:"", requestDate = txtDate.getTrimmedText() )
        )
    }

    private fun manageOnClickListeners() = with(binding){
        txtDate.setSafeOnClickListener {
            openDatePicker()
        }
        btnSubmit.setSafeOnClickListener {
            if(txtDate.getTrimmedText().isEmpty()){
                showToast("Please select date")
            }else if (txtReason.getTrimmedText().isEmpty()){
                showToast("Please enter reason")
            }else {
                initApiCall()
            }
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_week_off_day))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun openDatePicker() {

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraints = CalendarConstraints.Builder()
            .setStart(today) // ✅ Start from today
            .setValidator(DateValidatorPointForward.now()) // ✅ Only today + future
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(selection))
            binding.txtDate.setText(selectedDate)
        }
    }
    private fun observeValidateSunday() {
        tourAgendaViewModel.validateSundayUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ValidateSundayUiState.Idle -> { }
                is ValidateSundayUiState.Loading -> {
                    showLoader()
                }
                is ValidateSundayUiState.Success -> {
                    hideLoader()
                    handleValidateSunday(state.list)
                }
                is ValidateSundayUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is ValidateSundayUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun handleValidateSunday(list: List<ValidateSundayResult>) = with(binding){
        val status = list.firstOrNull()?.Status
        when {
            status.equals("WeekOff", ignoreCase = true)  -> {
                val user = sharedPref.getUser()
                val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val request = WeekOffRequest(
                    empId = user?.EmpID?:"",
                    date = todayDate,
                    requestDate = txtDate.getTrimmedText(),
                    empName = user?.UsersName?:"",
                    mobileNo = user?.MobileNo?:"",
                    reason = txtReason.getTrimmedText(),
                    insertedByUserId = user?.InsertedByUserId?:""
                )
                tourAgendaViewModel.submitWeekOff(request)
            }
            else -> {
                showToast("Selected date is not a WeekOff (" + status + ")")
            }
        }
    }
    private fun observeSubmitWeekOff() {
        tourAgendaViewModel.submitWeekOffUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SubmitWeekOffUiState.Idle -> { }
                is SubmitWeekOffUiState.Loading -> {
                    showLoader()
                }
                is SubmitWeekOffUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }
                is SubmitWeekOffUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is SubmitWeekOffUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun showLoader() {
        requireActivity().window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }

        binding.progressBarPJC.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        binding.progressBarPJC.visibility = View.GONE
    }

}