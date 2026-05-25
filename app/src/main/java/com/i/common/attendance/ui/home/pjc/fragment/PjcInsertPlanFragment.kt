package com.i.common.attendance.ui.home.pjc.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentPjcInsertPlanBinding
import com.i.common.attendance.network.request.BusinessCenterNameRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.InsertPjcEventRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.pjc.viewmodel.CalendarViewModel
import com.i.common.attendance.ui.home.pjc.viewmodel.GetServerTimeState
import com.i.common.attendance.ui.home.pjc.viewmodel.InsertPjcState
import com.i.common.attendance.ui.home.touragendatracking.fragment.SelectBusignessCenterNameBottomSheetFragment
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetStationUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.BackDateRightUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.utils.Constants.getCurrentTimestamp
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import androidx.core.view.isVisible

@AndroidEntryPoint
class PjcInsertPlanFragment : BaseFragment() {
    private lateinit var binding: FragmentPjcInsertPlanBinding
    private val viewmodel: CalendarViewModel by viewModels()
    private val tourAgendaViewModel: TourAgendaTrackingViewModel by viewModels()
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    var noOfDays: Int = 0
    var fromNoOffDays: Int = 0
    var toNoOffDays: Int = 0
    var serverYear = 0
    var serverMonth = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPjcInsertPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding.txtDate.setText(it.getString("selected_date"))
        }
        manageFlavor()
        manageToolBar()
        moveOnClickListeners()
        initApiCallAndObserver()

        viewmodel.getServerTime()
        observeServerTimeApi()
        observeBackDateRight()
        observeBusignessCenterNameData()
        observeInsertPjcEvent()
    }

    private fun manageFlavor() = with(binding){
        // THis for the night hold option only show in the below mentioned flavor
        val showNightHold = BuildConfig.FLAVOR in listOf("flotech", "singla", "algo", "mascot")
        txtLayNightHold.visibility = if (showNightHold) View.VISIBLE else View.GONE
        radioGroupNightHold.visibility = if (showNightHold) View.VISIBLE else View.GONE

        // THis for the Cover/Uncover option only show in the below mentioned flavor
        val showCoverUncover = BuildConfig.FLAVOR == "unnati"
        txtLayCoverUncover.visibility = if (showCoverUncover) View.VISIBLE else View.GONE
        radioGroupCoverUncover.visibility = if (showCoverUncover) View.VISIBLE else View.GONE

        if (BuildConfig.FLAVOR == "algo") {
            val currentDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(Date())
            binding.txtDate.setText(currentDate)
        }
    }

    private fun showPopUp() = with(binding){
        try {
            val parser = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

            val currentTimeStr = getCurrentTimestamp("hh:mm a") ?: return@with
            val currentTimeDt = parser.parse(currentTimeStr) ?: return@with
            val eightAMCompareTimeDt = parser.parse("08:00 AM") ?: return@with

            if (getCurrentTimestamp("dd-MMM-yyyy").equals(getCurrentTimestamp("dd-MMM-yyyy"), ignoreCase = true)) {

                // If current time is AFTER or EQUAL 08:00 AM
                if (!currentTimeDt.before(eightAMCompareTimeDt)) {
                    btnSubmit.isEnabled = false

                    AlertDialog.Builder(requireContext())
                        .setTitle("Not Fill Plan !")
                        .setMessage("Same day's plan can be added by today's 8:00 AM only.")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    btnSubmit.isEnabled = true
                }

            } else {
                btnSubmit.isEnabled = true
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    private fun initApiCallAndObserver() = with(binding){
        val user = sharedPref.getUser()
        when (BuildConfig.FLAVOR) {
            "duke" -> {
                tourAgendaViewModel.refreshStationList(request = BusinessCenterNameRequest(empId = user?.EmpID?:""))
                txtStation.apply {
                    isFocusable = false
                    isFocusableInTouchMode = false
                    isCursorVisible = false
                    isClickable = true
                }

                txtLayStation.apply {
                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                    endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)
                    setEndIconTintList(ContextCompat.getColorStateList(context, R.color.primary))
                }
            }
            else -> {
                tourVoucherViewmodel.getBackDatedRight( PjcDateRequest(empId = user?.EmpID?:""))
            }
        }
    }
    private fun moveOnClickListeners() = with(binding) {
        if(BuildConfig.FLAVOR == "duke"){
            txtStation.setSafeOnClickListener {
                val activityList = tourAgendaViewModel.cachedStationList
                if (activityList.isNullOrEmpty()) {
                    showToast(getString(R.string.validation_data_not_ready_yet))
                    return@setSafeOnClickListener
                }
                val bottomSheet = SelectBusignessCenterNameBottomSheetFragment.Companion.newInstance(activityList)
                bottomSheet.setDismissCallback { selected ->
                    txtStation.setText(selected.Name)
                }
                bottomSheet.show(childFragmentManager, "ActivityPlan")
            }
        }

        txtDate.setSafeOnClickListener {
            if(BuildConfig.FLAVOR == "duke" || BuildConfig.FLAVOR == "mascot"){
                openDatePicker()
            }else{
                openDatePickerCurrentMonth()
            }
        }

       /* btnSubmit.setOnClickListener {
            if(txtDate.getTrimmedText().isEmpty()){
                showToast("Please select date")
            }else if(txtStation.getTrimmedText().isEmpty()){
                showToast("Please enter station")
            }else if(txtVisitAgenda.getTrimmedText().isEmpty()){
                showToast("Please enter visit agenda")
            }else{
                val user = sharedPref.getUser()
                val request = InsertPjcEventRequest(
                    date = txtDate.getTrimmedText(),
                    place = txtStation.getTrimmedText(),
                    notes = txtVisitAgenda.getTrimmedText(),
                    mobileNo = user?.MobileNo ?: "",
                    monthYear = getCurrentTimestamp("MMM yyyy"),
                    nightHault = (radioGroupNightHold.isVisible && radioButtonYes.isChecked).toString()

                )
                Log.e("request", request.toString())

                if(BuildConfig.FLAVOR == "mascot") {
                    viewmodel.insertPjcEvent(request)
                } else {
                    if(user?.IsAllowPJCWOValidation.equals("True", ignoreCase = true)){
                        viewmodel.insertPjcEvent(request)
                    }else{
                        val today = LocalDate.now()

                        val canSelectDay = today.year == serverYear && today.monthValue == serverMonth && today.dayOfMonth in 1..4
                        if (canSelectDay) {
                            viewmodel.insertPjcEvent(request)
                        }else {
                            showToast("Not Fill PJC After Current Month 1 To 4 Date")
                        }
                    }
                }
            }
        }*/
        btnSubmit.setOnClickListener {

            when {
                txtDate.getTrimmedText().isEmpty() -> {
                    showToast("Please select date")
                }

                txtStation.getTrimmedText().isEmpty() -> {
                    showToast("Please enter station")
                }

                txtVisitAgenda.getTrimmedText().isEmpty() -> {
                    showToast("Please enter visit agenda")
                }

                else -> {

                    val user = sharedPref.getUser()

                    val request = InsertPjcEventRequest(
                        date = txtDate.getTrimmedText(),
                        place = txtStation.getTrimmedText(),
                        notes = txtVisitAgenda.getTrimmedText(),
                        mobileNo = user?.MobileNo ?: "",
                        monthYear = getCurrentTimestamp("MMM yyyy"),
                        nightHault = (radioGroupNightHold.isVisible && radioButtonYes.isChecked).toString(),
                        type =
                            if (radioGroupCoverUncover.isVisible) {
                                if (radioButtonCover.isChecked) "Cover" else "Uncover"
                            } else {
                                null
                            }
                    )

                    Log.e("request", request.toString())
                    val isMascot = BuildConfig.FLAVOR == "mascot"
                    val isAlgo = BuildConfig.FLAVOR == "algo"

                    val hasValidationRight = user?.IsAllowPJCWOValidation.equals("True", true)
                    val today = LocalDate.now()

                    val isAllowedDate = today.year == serverYear && today.monthValue == serverMonth && today.dayOfMonth in 1..4
                    when {
                        isMascot || isAlgo -> {
                            viewmodel.insertPjcEvent(request)
                        }

                        hasValidationRight -> {
                            viewmodel.insertPjcEvent(request)
                        }

                        isAllowedDate -> {
                            viewmodel.insertPjcEvent(request)
                        }

                        else -> {
                            viewmodel.insertPjcEvent(request)
                            showToast("Not Fill PJC After Current Month 1 To 4 Date")
                        }
                    }
                }
            }
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.tootlbar_title_plan))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeInsertPjcEvent() = with(binding){
        viewmodel.insertPjcState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is InsertPjcState.Loading -> {
                    showLoader()
                }

                is InsertPjcState.Success -> {
                    hideLoader()
                    showToast("Inserted successfully")
                    parentFragmentManager.popBackStackImmediate()
                }

                is InsertPjcState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }
    private fun observeServerTimeApi() = with(binding){
        viewmodel.serverTimeState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is GetServerTimeState.Loading -> {
                    showLoader()
                }

                is GetServerTimeState.Success -> {
                    hideLoader()

                    val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH)
                    //val serverDateTime = LocalDateTime.parse(state.serverTime.serverTime, formatter)

                    val primaryFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH)
                    val fallbackFormatter = DateTimeFormatter.ofPattern("M/d/yyyy hh:mm:ss a", Locale.ENGLISH)

                    val serverDateTime = try {
                        LocalDateTime.parse(state.serverTime.serverTime, primaryFormatter)
                    } catch (e: Exception) {
                        try {
                            LocalDateTime.parse(state.serverTime.serverTime, fallbackFormatter)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: return@observe  // or handle null case as per your logic

                    serverYear = serverDateTime.year
                    serverMonth = serverDateTime.monthValue
                }

                is GetServerTimeState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeBackDateRight() {
        tourVoucherViewmodel.backDateRightState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is BackDateRightUiState.Idle -> {
                }

                is BackDateRightUiState.Loading -> {
                    showLoader()
                }

                is BackDateRightUiState.Success -> {
                    hideLoader()
                    val list = state.data
                    val firstItem = list.firstOrNull()
                    noOfDays = firstItem?.noOfDays?.toIntOrNull() ?: 0
                    fromNoOffDays = firstItem?.fromPjcDate?.toIntOrNull() ?: 0
                    toNoOffDays = firstItem?.toPjcDate?.toIntOrNull() ?: 0
                }

                is BackDateRightUiState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }

                is BackDateRightUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is BackDateRightUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeBusignessCenterNameData() {
        tourAgendaViewModel.getStationUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetStationUiState.Idle -> { }
                is GetStationUiState.Loading -> {
                    showLoader()
                }
                is GetStationUiState.Success -> {
                    hideLoader()
                }
                is GetStationUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetStationUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun openDatePickerCurrentMonth() {

        try {

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)

            // 🔹 Min date (FromPJCDate)
            val startCalendar = Calendar.getInstance().apply {
                set(year, month, fromNoOffDays, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // 🔹 Max date (ToPJCDate)
            val endCalendar = Calendar.getInstance().apply {
                set(year, month, toNoOffDays, 23, 59, 59)
                set(Calendar.MILLISECOND, 999)
            }

            val startMillis = startCalendar.timeInMillis
            val endMillis = endCalendar.timeInMillis

            val constraints = CalendarConstraints.Builder()
                .setStart(startMillis)
                .setEnd(endMillis)
                .setOpenAt(startMillis) // open current month
                .setValidator(object : CalendarConstraints.DateValidator {
                    override fun isValid(date: Long): Boolean {
                        return date in startMillis..endMillis
                    }
                    override fun describeContents() = 0
                    override fun writeToParcel(dest: android.os.Parcel, flags: Int) {}
                })
                .build()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(startMillis)
                .setCalendarConstraints(constraints)
                .build()

            datePicker.show(childFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->

                val selectedDate = SimpleDateFormat(
                    "dd-MMM-yyyy",
                    Locale.getDefault()
                ).format(Date(selection))

                binding.txtDate.setText(selectedDate)
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
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
            val selectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                .format(Date(selection))
            binding.txtDate.setText(selectedDate)
        }
    }
    private fun showLoader() {
        requireActivity().window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            attributes = attributes.apply {
                alpha = 0.5f   // 👈 0.0 (fully transparent) to 1.0 (normal)
            }
        }

        binding.progressBarPJC.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            attributes = attributes.apply {
                alpha = 1.0f   // 👈 restore normal
            }
        }

        binding.progressBarPJC.visibility = View.GONE
    }

}