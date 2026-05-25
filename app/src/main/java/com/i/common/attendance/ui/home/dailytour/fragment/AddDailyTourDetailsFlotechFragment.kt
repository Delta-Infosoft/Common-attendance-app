package com.i.common.attendance.ui.home.dailytour.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FramgentAddDailyTourDetailsFlotechBinding
import com.i.common.attendance.network.request.CheckPJCEntryRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.DailyTourFlotechRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourViewModel
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerNameState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DistrictState
import com.i.common.attendance.ui.home.dailytour.viewmodel.InsertDailyDetailsState
import com.i.common.attendance.ui.home.dailytour.viewmodel.InsertDailyTourFlotechUiState
import com.i.common.attendance.ui.home.pjc.fragment.PjcInsertPlanFragment
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
class AddDailyTourDetailsFlotechFragment : BaseFragment() {

    private lateinit var binding : FramgentAddDailyTourDetailsFlotechBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val dailyTourViewModel: DailyTourViewModel by viewModels()
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    private val fieldOrder = listOf(
        "ORDER_DISCUSSION",
        "PAYMENT_DISCUSSION",
        "PAYMENT_AMOUNT"
    )
    private val selectedFields = mutableSetOf<String>()
    private val dynamicFieldMap = mutableMapOf<String, TextInputEditText>()
    private var expenseRights = ""
    private var selectedDealerCategoryID = ""
    var noOfDays: Int = 0
    var isOrderFollowUp = 0
    var isPaymentFollowUp = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FramgentAddDailyTourDetailsFlotechBinding.inflate(inflater,container,false)
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
        observeDealerCategory()
        observeDealerName()
        observeDistrict()
        observeInsertDailyTourFlotech()
    }
    private fun initApiCall() {
        val user = shredPref.getUser()
        tourVoucherViewmodel.getBackDatedRight( PjcDateRequest(empId = user?.EmpID?:""))
        tourVoucherViewmodel.getWithoutPJCTourRights(PjcDateRequest(empId = user?.EmpID?:""))
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
        txtDealerCategory.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDealerCategoryList
            val bottomSheet = list?.let { it1 -> SelectDailyTourDealerCategoryBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtDealerCategory.setText(selected.Text)
                selectedDealerCategoryID = selected.TextListId?:""

                if (selected.Text == "Company Dealer") {
                    val user = shredPref.getUser()

                    // Call API only when category changes
                    dailyTourViewModel.getDealerName(
                        DailyTourDealerNameRequest(
                            empId = user?.EmpID ?: "",
                            dealerType = selected.Text ?: ""
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
                        endIconDrawable =
                            ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)
                        setEndIconTintList(ContextCompat.getColorStateList(context, R.color.primary))
                    }
                }else {
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
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        txtDistrict.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDistrictList
            val bottomSheet = list?.let { it1 -> SelectDailyTourDistrictBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtDistrict.setText(selected.District)
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        cbOrderDiscussion.setOnCheckedChangeListener { _, isChecked ->
            isOrderFollowUp = if (isChecked) 1 else 0
            updateDynamicField("ORDER_DISCUSSION", "Order Follow-Up On *", isChecked)
        }
        cbPaymentDiscussion.setOnCheckedChangeListener { _, isChecked ->
            isPaymentFollowUp = if (isChecked) 1 else 0
            updateDynamicField("PAYMENT_DISCUSSION", "Payment Follow-Up On *", isChecked)
            updateDynamicField("PAYMENT_AMOUNT", "Payment Amount", isChecked)
        }
        btnSubmit.setSafeOnClickListener {
            if (!validateFormWithToast()) {
                return@setSafeOnClickListener
            }

            if (!validateDynamicFields()) {
                return@setSafeOnClickListener
            }

            val user = shredPref.getUser()
            dailyTourViewModel.insertDailyTourDetailsFlotech(
                request = DailyTourFlotechRequest(
                    date = txtDate.getTrimmedText(),
                    startTime = "",
                    endTime = "",
                    fromPlace = txtPalace.getTrimmedText(),
                    toPlace = "",
                    typeTextListId = selectedDealerCategoryID,
                    dealerName = txtDealerName.getTrimmedText(),
                    district = txtDistrict.getTrimmedText(),
                    area ="",
                    businessCenter = txtBusignessCenter.getTrimmedText(),
                    mobileNo = txtMobileNo.getTrimmedText(),
                    pointDiscussion = txtCommonDiscussion.getTrimmedText(),
                    empMobileNo = user?.MobileNo?:"",
                    isPaymentFollowUp = isPaymentFollowUp.toString(),
                    isOrderFollowUp = isOrderFollowUp.toString(),
                    isDiscountDiscussion = 0.toString(),
                    isSchemeDiscussion = 0.toString(),
                    isSalesPromotionalActivity = 0.toString(),
                    isStockPlanning = 0.toString(),
                    isServiceOrRepairing = 0.toString(),
                    paymentFollowUpDt = dynamicFieldMap["PAYMENT_DISCUSSION"]?.text.toString(),
                    orderFollowUpDt = dynamicFieldMap["ORDER_DISCUSSION"]?.text.toString(),
                    paymentFollowUpAmount = dynamicFieldMap["PAYMENT_AMOUNT"]?.text.toString(),
                    isAllowPayment = 0.toString(),
                    isOtherFollowUp = 0.toString(),
                    allowPaymentDt = "",
                    otherFollowUpDt = ""
                )
            )

           /* val request = DailyTourAddDetailsRequest(
                date = txtDate.getTrimmedText(),
                typeTextListId = selectedDealerCategoryID,
                dealerName = txtDealerName.getTrimmedText(),
                district = txtDistrict.getTrimmedText(),
                businessCenter = txtMarketCenterName.getTrimmedText(),
                mobileNo = txtMobileNo.getTrimmedText(),
                pointDiscussion = txtCommonDiscussion.getTrimmedText(),
                empMobileNo = shredPref.getUser()?.MobileNo?:"",
                isPaymentFollowUp = isPaymentFollowUp.toString(),
                isOrderFollowUp = isOrderFollowUp.toString(),
                isNewDealerSurvey = isNewDealerSurvey.toString(),
                newDealerAppointmentDt = dynamicFieldMap["NEW_DEALER_APPOINTMENT"]?.text.toString(),
                subDealerVisitDate = dynamicFieldMap["SUB_DEALER_VISIT"]?.text.toString(),
                newDealerSurveyDate = dynamicFieldMap["NEW_DEALER_SURVEY"]?.text.toString(),
                orderFollowUpDt =  dynamicFieldMap["ORDER_DISCUSSION"]?.text.toString(),
                paymentFollowUpDt =  dynamicFieldMap["PAYMENT_DISCUSSION"]?.text.toString(),
                paymentFollowUpAmount =  dynamicFieldMap["PAYMENT_AMOUNT"]?.text.toString()
            )
            dailyTourViewModel.insertDailyDetails(request)*/

        }
    }
    private fun validateFormWithToast(): Boolean = with(binding)  {

        if (txtDate.isEmpty()) {
            showToast("Please select date")
            return false
        }

        if (txtDealerCategory.isEmpty()) {
            showToast("Please select dealer category")
            return false
        }

        if (txtDistrict.isEmpty()) {
            showToast("Please select district")
            return false
        }

        if (txtBusignessCenter.isEmpty()) {
            showToast("Please enter busigness center name")
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
    private fun updateDynamicField(key: String, hint: String, isChecked: Boolean) {
        if (isChecked) {
            selectedFields.add(key)
        } else {
            selectedFields.remove(key)
        }

        rebuildDynamicLayout()
    }
    private fun rebuildDynamicLayout() = with(binding){
        constDynamic.removeAllViews()
        dynamicFieldMap.clear()

        val inflater = LayoutInflater.from(requireContext())

        fieldOrder.forEach { key ->

            if (selectedFields.contains(key)) {
                val view = inflater.inflate(R.layout.item_followup_field, constDynamic, false)
                val textInputLayout = view.findViewById<TextInputLayout>(R.id.textInputLayout)
                val editText = view.findViewById<TextInputEditText>(R.id.edtDynamic)
                dynamicFieldMap[key] = editText
                when (key) {

                    "ORDER_DISCUSSION" -> {
                        textInputLayout.hint = "Order Follow-Up On *"
                        textInputLayout.isHintEnabled = true
                        textInputLayout.isHintAnimationEnabled = true
                        editText.hint = null

                        editText.setOnClickListener {
                            openDatePicker(editText)
                        }
                    }

                    "PAYMENT_DISCUSSION" -> {
                        textInputLayout.hint = "Payment Follow-Up On *"
                        textInputLayout.isHintEnabled = true
                        textInputLayout.isHintAnimationEnabled = true
                        editText.hint = null

                        editText.setOnClickListener {
                            openDatePicker(editText)
                        }
                    }

                    "PAYMENT_AMOUNT" -> {
                        textInputLayout.hint = "Payment Amount"
                        textInputLayout.isHintEnabled = true
                        textInputLayout.isHintAnimationEnabled = true
                        editText.hint = null
                        editText.apply {
                            filters = arrayOf(InputFilter.LengthFilter(7))
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                            keyListener = DigitsKeyListener.getInstance("0123456789.")
                            isFocusable = true
                            isFocusableInTouchMode = true
                            isClickable = true
                            isCursorVisible = true
                            isLongClickable = true
                        }
                    }
                }

                constDynamic.addView(view)
            }
        }

        constDynamic.visibility =
            if (constDynamic.childCount > 0) View.VISIBLE else View.GONE
    }
    private fun validateDynamicFields(): Boolean {

        dynamicFieldMap.forEach { (key, editText) ->

            val value = editText.text.toString().trim()

            when (key) {
                "ORDER_DISCUSSION" -> {
                    if (value.isEmpty()) {
                        showToast("Please select Order Follow-Up On *")
                        editText.requestFocus()
                        return false
                    }
                }
                "PAYMENT_DISCUSSION" -> {
                    if (value.isEmpty()) {
                        showToast("Please select Payment Follow-Up On *")
                        editText.requestFocus()
                        return false
                    }
                }

                "PAYMENT_AMOUNT" -> {
                    if (value.isEmpty()) {
                        showToast("Enter payment amount")
                        editText.requestFocus()
                        return false
                    }
                }
            }
        }

        return true
    }
    private fun openDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, day)

                val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val formattedDate = sdf.format(selectedCalendar.time)

                editText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

        datePicker.show()
    }
    private fun openDatePicker(noOfDays: Int) {

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val minCalendar = Calendar.getInstance()

        if (noOfDays != 0) {
            minCalendar.add(Calendar.DATE, -noOfDays)
        } else {
            minCalendar.add(Calendar.DATE, -3)
        }

        // Normalize min date
        minCalendar.set(Calendar.HOUR_OF_DAY, 0)
        minCalendar.set(Calendar.MINUTE, 0)
        minCalendar.set(Calendar.SECOND, 0)
        minCalendar.set(Calendar.MILLISECOND, 0)

        val minDate = minCalendar.timeInMillis

        // ✅ Combine both validators (IMPORTANT)
        val constraints = CalendarConstraints.Builder()
            .setStart(minDate)
            .setEnd(today)
            .setValidator(
                CompositeDateValidator.allOf(
                    listOf(
                        DateValidatorPointForward.from(minDate),   // >= minDate
                        DateValidatorPointBackward.before(today + 1) // <= today
                    )
                )
            )
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val formattedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                .format(Date(selection))

            binding.txtDate.setText(formattedDate)
            //checkPJCEntry()
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
    private fun observeInsertDailyTourFlotech() {
        dailyTourViewModel.insertDailyTourFlotechUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertDailyTourFlotechUiState.Idle -> { }
                is InsertDailyTourFlotechUiState.Loading -> {
                    showLoader()
                }
                is InsertDailyTourFlotechUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }
                is InsertDailyTourFlotechUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertDailyTourFlotechUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
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