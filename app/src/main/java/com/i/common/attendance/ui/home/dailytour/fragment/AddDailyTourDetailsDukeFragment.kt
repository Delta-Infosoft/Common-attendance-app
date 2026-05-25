package com.i.common.attendance.ui.home.dailytour.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FramgentAddDailyTourDetailsDukeBinding
import com.i.common.attendance.network.request.BusinessCenterNameRequest
import com.i.common.attendance.network.request.CheckPJCEntryRequest
import com.i.common.attendance.network.request.DailyTourAddDetailsDukeRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.network.request.TourAgendaTrackingServiceCenterRequest
import com.i.common.attendance.network.request.TourAgendaTrackingSubDealerNameRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourViewModel
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerNameState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DistrictState
import com.i.common.attendance.ui.home.dailytour.viewmodel.InsertDailyDetailsState
import com.i.common.attendance.ui.home.pjc.fragment.PjcInsertPlanFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.SelectBusignessCenterNameBottomSheetFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.SelectServiceCenterBottomSheetFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.SelectSubDealerNameBottomSheetFragment
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetStationUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.ui.home.tourvoucher.fragment.SelectCommonDialogBottomSheetFragment
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.BackDateRightUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.CheckPJCEntryUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.PjcPermissionUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.isEmpty
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddDailyTourDetailsDukeFragment : BaseFragment() {

    private lateinit var binding : FramgentAddDailyTourDetailsDukeBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val dailyTourViewModel: DailyTourViewModel by viewModels()
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    private val tourAgendaViewModel: TourAgendaTrackingViewModel by viewModels()
    private var expenseRights = ""
    private var selectedDealerCategoryID = ""
    private var serviceCenterID = ""
    private var subDealerId = ""
    private var dealerId = ""
    private var districtNew = ""
    var noOfDays: Int = 0

    private var busignessCenterId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FramgentAddDailyTourDetailsDukeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        moveOnClickListeners()

        initApiCall()
        observePjcPermissionData()
        observePJCEntryData()
        observeBackDateRight()
        observeBusignessCenterNameData()
        observeDealerCategory()
        observeDealerName()
        observeDistrict()
        observeInsertData()
    }
    private fun initApiCall() {
        val user = shredPref.getUser()
        tourVoucherViewmodel.getBackDatedRight( PjcDateRequest(empId = user?.EmpID?:""))
        tourVoucherViewmodel.getWithoutPJCTourRights(PjcDateRequest(empId = user?.EmpID?:""))
        tourAgendaViewModel.refreshStationList(request = BusinessCenterNameRequest(empId = user?.EmpID?:""))
        dailyTourViewModel.getDealerCategory(DailyTourDealerCategoryRequest(type = "Weekly", deptId = ""))
        dailyTourViewModel.getDistrictList(DailyTourDistrictRequest(stateId = ""))
    }
    private fun checkPJCEntry() = with(binding){
        val fromDate = txtDate.text?.toString()?.trim().orEmpty()
        if (!expenseRights.equals("True", ignoreCase = true)) {
            if (fromDate.isNotEmpty()) {
                val request = CheckPJCEntryRequest(
                    mobileNo = shredPref.getUser()?.MobileNo ?: "",
                    date = txtDate.getTrimmedText(),
                    type = "CHECK_PJC"
                )
                tourVoucherViewmodel.checkPJCEntry(request)
            }
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_daily_tour_details))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun moveOnClickListeners() = with(binding){
        txtDate.setSafeOnClickListener {
            openDatePicker(noOfDays)
        }
        txtStartTime.setSafeOnClickListener {
            openTimePicker(true)
        }
        txtEndTime.setSafeOnClickListener {
            openTimePicker(false)
        }
        txtBusignessCenter.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedStationList
            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectBusignessCenterNameBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtBusignessCenter.setText(selected.Name)
                busignessCenterId = selected.BusiCntrId ?:""

                txtLayDealerName.visibility = View.VISIBLE
                txtLaySubDealerName.visibility = View.INVISIBLE
                txtLayServiceCenter.visibility = View.INVISIBLE
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtDealerCategory.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDealerCategoryList
            val bottomSheet = list?.let { it1 -> SelectDailyTourDealerCategoryBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtDealerCategory.setText(selected.Text)
                selectedDealerCategoryID = selected.TextListId?:""

                if (selected.Text == "Dealer" || selected.Text == "Distributor") {
                    txtLayDealerName.visibility = View.VISIBLE
                    txtLaySubDealerName.visibility = View.INVISIBLE
                    txtLayServiceCenter.visibility = View.INVISIBLE

                    val user = shredPref.getUser()

                    // Call API only when category changes
                    dailyTourViewModel.getDealerName(
                        DailyTourDealerNameRequest(
                            empId = user?.EmpID ?: "",
                            dealerType = selected.Text ?: "",
                            busignessCenterId = busignessCenterId
                        )
                    )

                    txtDealerName.apply {
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isCursorVisible = false
                        isClickable = true
                    }

                    txtLayDealerName.apply {
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)
                        setEndIconTintList(ContextCompat.getColorStateList(context, R.color.primary))
                    }

                } else if(selected.Text.equals("Sub Dealer")) {
                    val user = shredPref.getUser()

                    tourAgendaViewModel.refreshSubDealerNameList(
                        TourAgendaTrackingSubDealerNameRequest(
                            empId = user?.EmpID ?: "",
                            busignessCenterId = busignessCenterId,
                            dealerType = "SUBDEALER"
                        )
                    )
                    txtLaySubDealerName.visibility = View.VISIBLE
                    txtLayDealerName.visibility = View.INVISIBLE
                    txtLayServiceCenter.visibility = View.INVISIBLE

                } else if(selected.Text.equals("Service Center")) {

                    tourAgendaViewModel.refreshServiceCenterList(TourAgendaTrackingServiceCenterRequest())

                    txtLayDealerName.visibility = View.INVISIBLE
                    txtLaySubDealerName.visibility = View.INVISIBLE
                    txtLayServiceCenter.visibility = View.VISIBLE

                } else {
                    txtLayDealerName.visibility = View.VISIBLE
                    txtLaySubDealerName.visibility = View.INVISIBLE
                    txtLayServiceCenter.visibility = View.INVISIBLE

                    txtDealerName.apply {
                        setText("")
                        isFocusable = true
                        isFocusableInTouchMode = true
                        isCursorVisible = true
                        isClickable = true
                    }

                    txtLayDealerName.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        txtDealerName.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDealerNameList
            val bottomSheet = list?.let { it1 -> SelectDailyTourDealerNameBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtDealerName.setText(selected.Name)
                dealerId = selected.DealerId?:""
                txtTaluka.setText(selected.Area)
                txtDistrict.setText(selected.District)
                txtMarketCenterName.setText(selected.BusinessCenter)
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        txtSubDealerName.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedSubDealerNameList

            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_no_data_found_for_sub_dealer))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectSubDealerNameBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtSubDealerName.setText(selected.Name)
                subDealerId = selected.SubDealerId?:""

                txtTaluka.setText(selected.Area)
                txtDistrict.setText(selected.District)
                txtMarketCenterName.setText(selected.BusinessCenter)
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtServiceCenter.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedServiceCenterList

            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validaiton_no_data_found_for_service_center))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectServiceCenterBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtServiceCenter.setText(selected.Name)
                serviceCenterID = selected.SubDealerId?:""

                txtTaluka.setText("")
                txtDistrict.setText("")
                txtMarketCenterName.setText("")
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }

        imgViewAddManualDistrict.setSafeOnClickListener {
            requireContext().showMarketCenterDialog { enteredDistrict ->
                districtNew = enteredDistrict
                txtDistrict.setText(enteredDistrict)
            }
        }

        txtDistrict.setSafeOnClickListener {
            districtNew = ""
            val list = dailyTourViewModel.cachedDistrictList
            val bottomSheet = list?.let { it1 -> SelectDailyTourDistrictBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtDistrict.setText(selected.District)
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }

        btnSubmit.setSafeOnClickListener {
            if (!validateFormWithToast()) {
                return@setSafeOnClickListener
            }

            val request = DailyTourAddDetailsDukeRequest(
                date = txtDate.getTrimmedText(),
                startTime = txtStartTime.getTrimmedText(),
                endTime = txtEndTime.getTrimmedText(),
                fromPlace = txtBusignessCenter.getTrimmedText(),
                toPlace = txtBusignessCenter.getTrimmedText(),
                typeTextListId = selectedDealerCategoryID,
                dealerName = txtDealerName.getTrimmedText(),
                dealerId = dealerId,
                district = txtDistrict.getTrimmedText(),
                districtNew = districtNew,
                area = txtTaluka.getTrimmedText(),
                businessCenter = txtMarketCenterName.getTrimmedText(),
                mobileNo = txtMobileNo.getTrimmedText(),
                pointDiscussion = txtCommonDiscussion.getTrimmedText(),
                empMobileNo = shredPref.getUser()?.MobileNo ?: "",
                serviceCenterId = serviceCenterID,
                subDealerName = txtSubDealerName.getTrimmedText(),
                subDealerId = subDealerId
            )
            dailyTourViewModel.insertDailyDukeDetails(request)

        }
    }

    fun Context.showMarketCenterDialog(
        onSave: (String) -> Unit
    ) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input_district, null)

        val etName = view.findViewById<TextInputEditText>(R.id.txtMarketCenterName)
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btnSubmit)
        val btnCancel = view.findViewById<AppCompatButton>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()

        btnSubmit.setOnClickListener {
            val value = etName.text.toString().trim()

            if (value.isEmpty()) {
                etName.error = "Enter district"
            } else {
                onSave.invoke(value)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun validateFormWithToast(): Boolean = with(binding)  {

        if (txtDate.isEmpty()) {
            showToast("Please select date")
            return false
        }

        if (txtStartTime.isEmpty()) {
            showToast("Please select start time")
            return false
        }

        if (txtEndTime.isEmpty()) {
            showToast("Please select end time")
            return false
        }


        if (txtDealerCategory.isEmpty()) {
            showToast("Please select dealer category")
            return false
        }

        if (txtDealerName.isEmpty()) {
            showToast("Please enter/select dealer name")
            return false
        }

        if (txtDistrict.isEmpty()) {
            showToast("Please select district")
            return false
        }

        if (txtTaluka.isEmpty()) {
            showToast("Please enter taluka")
            return false
        }

        if (txtMarketCenterName.isEmpty()) {
            showToast("Please enter market center name")
            return false
        }

        if (txtMobileNo.isEmpty()) {
            showToast("Please enter mobile no")
            return false
        }

        if (txtMobileNo.getTrimmedText().length < 10) {
            showToast("Please enter valid mobile no")
            return false
        }

        if (txtCommonDiscussion.isEmpty()) {
            showToast("Please enter common discussion")
            return false
        }


        return true
    }


    private fun openDatePicker(noOfDays: Int) {

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        // 🔹 Calculate minimum date
        val calendar = Calendar.getInstance()

        if (noOfDays != 0) {
            calendar.add(Calendar.DATE, -noOfDays)
        } else {
            calendar.add(Calendar.DATE, -1)
        }

        val minDate = calendar.timeInMillis

        val constraints = CalendarConstraints.Builder()
            .setStart(minDate)     // ✅ Min date (Today - noOfDays)
            .setEnd(today)         // ✅ Max date (Today)
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(selection))
            binding.txtDate.setText(selectedDate)
            checkPJCEntry()
        }
    }
    private fun openTimePicker(isFromTime: Boolean) {
        val calendar = Calendar.getInstance()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()

        timePicker.show(childFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {

            val hour = timePicker.hour
            val minute = timePicker.minute

            val timeCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val selectedTime = sdf.format(timeCalendar.time).uppercase(Locale.ENGLISH)

            if (isFromTime) {
                binding.txtStartTime.setText(selectedTime)
            } else {
                val startTimeText = binding.txtStartTime.text.toString()

                if (startTimeText.isEmpty()) {
                    showToast("Please select Start Time first")
                    return@addOnPositiveButtonClickListener
                }

                val startDate = sdf.parse(startTimeText)
                val endDate = sdf.parse(selectedTime)

                if (startDate != null && endDate != null) {

                    if (!endDate.before(startDate)) {
                        // ✅ Allowed (End <= Start)
                        binding.txtEndTime.setText(selectedTime)
                    } else {
                        showToast("End Time should not be less than Start Time")
                    }

                } else {
                    showToast("Invalid time format")
                }
            }
        }
    }
    /*======================================================================================*/
    private fun observePjcPermissionData() = with(binding) {
        tourVoucherViewmodel.pjcPermissionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PjcPermissionUiState.Loading -> showLoader()
                is PjcPermissionUiState.Success -> {
                    hideLoader()
                    state.data.column1?.let {
                        expenseRights = it
                        Log.e("expenseRights",expenseRights)
                    }
                }
                is PjcPermissionUiState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }
                is PjcPermissionUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is PjcPermissionUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }
    private fun observePJCEntryData(){
        tourVoucherViewmodel.checkPJCEntryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CheckPJCEntryUiState.Loading -> {
                    showLoader()
                    manageButtonVisibility(true)
                }

                is CheckPJCEntryUiState.Allowed -> {
                    manageButtonVisibility(false)
                    hideLoader()
                }

                is CheckPJCEntryUiState.NotAllowed -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    val bottomSheet = SelectCommonDialogBottomSheetFragment.Companion.newInstance(
                        errorText = "You had not filed PJC details of selected date. So please first add your PJC details of selected date.",
                        pageText = "Would you like to open PJC entry form?"
                    )
                    bottomSheet.setDismissCallback { selected ->
                        tourVoucherViewmodel.resetCheckPJCState()
                        when (selected) {
                            "Yes" -> {
                                val pjcInsertPlanFragment = PjcInsertPlanFragment()
                                val bundle = Bundle()
                                bundle.putString("selected_date", binding.txtDate.getTrimmedText())
                                pjcInsertPlanFragment.arguments = bundle
                                loadFragment(fragment = pjcInsertPlanFragment, isAdd = false, isAddBackStack = true)
                                binding.txtDate.setText("")
                            }
                            "No" -> {
                                binding.txtDate.setText("")
                            }
                        }
                    }
                    bottomSheet.show(childFragmentManager, "SelectCommon")
                }

                is CheckPJCEntryUiState.Error -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast("Something went wrong")
                }

                else -> Unit
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
                    Log.e("NoOfDays","${firstItem?.noOfDays ?: "0"}")
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
    private fun observeDealerCategory() {
        dailyTourViewModel.dealerCategoryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DealerCategoryState.Loading -> {
                    showLoader()
                }
                is DealerCategoryState.Success -> {
                    hideLoader()
                }
                is DealerCategoryState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is DealerCategoryState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> {}
            }
        }
    }
    private fun observeDealerName() {
        dailyTourViewModel.dealerNameState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DealerNameState.Loading -> showLoader()
                is DealerNameState.Success -> {
                    hideLoader()
                }
                is DealerNameState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is DealerNameState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> {}
            }
        }
    }
    private fun observeDistrict() {
        dailyTourViewModel.districtState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DistrictState.Loading -> showLoader()
                is DistrictState.Success -> {
                    hideLoader()
                }

                is DistrictState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is DistrictState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> {}
            }
        }
    }
    private fun observeInsertData() {
        dailyTourViewModel.insertDailyDetailsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertDailyDetailsState.Loading -> {
                    showLoader()
                }
                is InsertDailyDetailsState.Success -> {
                    hideLoader()
                    val response = state.data
                    showToast(response.message ?: "Saved successfully")
                    parentFragmentManager.popBackStackImmediate()
                }
                is InsertDailyDetailsState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertDailyDetailsState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> {}
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
    private fun manageButtonVisibility(isInProgress: Boolean) = with(binding) {
        if (isInProgress) {
            btnSubmit.alpha = 0.5f
            btnSubmit.isEnabled = false
        } else {
            btnSubmit.alpha = 1f
            btnSubmit.isEnabled = true
        }
    }
}