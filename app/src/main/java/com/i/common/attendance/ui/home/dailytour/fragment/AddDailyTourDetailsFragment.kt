package com.i.common.attendance.ui.home.dailytour.fragment

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FramgentAddDailyTourDetailsBinding
import com.i.common.attendance.network.request.CheckPJCEntryRequest
import com.i.common.attendance.network.request.DailyTourAddDetailsRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.network.response.UploadAttachmentItem
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourViewModel
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerNameState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DistrictState
import com.i.common.attendance.ui.home.dailytour.viewmodel.InsertDailyDetailsState
import com.i.common.attendance.ui.home.pjc.fragment.PjcInsertPlanFragment
import com.i.common.attendance.ui.home.tourvoucher.data.CommonSelect
import com.i.common.attendance.ui.home.tourvoucher.data.MediaPickType
import com.i.common.attendance.ui.home.tourvoucher.fragment.CommonSelectBottomSheetFragment
import com.i.common.attendance.ui.home.tourvoucher.fragment.SelectCommonDialogBottomSheetFragment
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.AttachmentType
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.BackDateRightUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.CheckPJCEntryUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.PjcPermissionUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.isEmpty
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddDailyTourDetailsFragment : BaseFragment() {

    private lateinit var binding: FramgentAddDailyTourDetailsBinding
    @Inject
    lateinit var shredPref: EncryptedPrefHelper
    private val dailyTourViewModel: DailyTourViewModel by viewModels()
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    private val fieldOrder = listOf(
        "NEW_DEALER_APPOINTMENT",
        "SUB_DEALER_VISIT",
        "NEW_DEALER_SURVEY",
        "ORDER_DISCUSSION",
        "PAYMENT_DISCUSSION",
        "PAYMENT_AMOUNT"
    )
    private val selectedFields = mutableSetOf<String>()
    private val dynamicFieldMap = mutableMapOf<String, TextInputEditText>()
    private var expenseRights = ""
    private var selectedDealerCategoryID = ""
    var noOfDays: Int = 0

    var isDiscountDiscussion = 0
    var isSchemeDiscussion = 0
    var isSalesPromotional = 0
    var isStockPlanning = 0
    var isServiceOrRepairing = 0

    var isNewDealerAppointment = 0
    var isSubDealerVisit = 0
    var isNewDealerSurvey = 0

    var isOrderFollowUp = 0
    var isPaymentFollowUp = 0

    private lateinit var cameraImageUri: Uri
    private var cameraImageFile: File? = null
    private var selectedImageUri: Uri? = null
    private var pendingPickType: AttachmentType? = null
    private val photoList = mutableListOf<UploadAttachmentItem>()
    private val documentList = mutableListOf<UploadAttachmentItem>()

    // -------------------- CAMERA --------------------
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                //handleSelectedUri(cameraImageUri, AttachmentType.PHOTO)
                cameraImageFile?.let { file ->
                    handleCameraFile(file, AttachmentType.PHOTO)
                }
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCameraInternal()
            else showToast("Camera permission denied")
        }

    // -------------------- GALLERY --------------------
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                handleSelectedUri(it, AttachmentType.PHOTO)
            }
        }

    // -------------------- DOCUMENT / AUDIO --------------------
    private val documentLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                when (pendingPickType) {
                    AttachmentType.FILE -> handleSelectedUri(it, AttachmentType.FILE)
                    else -> Unit
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FramgentAddDailyTourDetailsBinding.inflate(inflater, container, false)
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
        observeInsertData()
    }

    private fun initApiCall() = with(binding) {
        val user = shredPref.getUser()
        val isUnnati = BuildConfig.FLAVOR.equals("unnati", true)
        if (BuildConfig.FLAVOR != "mascot") {
            tourVoucherViewmodel.getWithoutPJCTourRights(PjcDateRequest(empId = user?.EmpID ?: ""))
        }
        tourVoucherViewmodel.getBackDatedRight(PjcDateRequest(empId = user?.EmpID ?: ""))
        dailyTourViewModel.getDealerCategory(
            DailyTourDealerCategoryRequest(
                type = "Weekly",
                deptId = ""
            )
        )
        dailyTourViewModel.getDistrictList(DailyTourDistrictRequest(stateId = ""))


        val visibility = if (isUnnati) View.VISIBLE else View.GONE
        linearAttachDocument.visibility = visibility
        linearAttachPhoto.visibility = visibility
        lylAttachmentPhotoListRow.visibility = visibility
    }

    private fun checkPJCEntry() = with(binding) {
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

    private fun moveOnClickListeners() = with(binding) {
        linearAttachDocument.setSafeOnClickListener {
            val bottomSheet =
                CommonSelectBottomSheetFragment.newInstance(CommonSelect.DOCUMENT_FILE)

            bottomSheet.setDismissCallback {
                when (it) {
                    MediaPickType.DOCUMENT -> openFile()
                    else -> Unit
                }
            }
            bottomSheet.show(childFragmentManager, "MediaPicker")
        }
        linearAttachPhoto.setSafeOnClickListener {
            val bottomSheet =
                CommonSelectBottomSheetFragment.newInstance(CommonSelect.CAMERA_GALLERY)
            bottomSheet.setDismissCallback {
                when (it) {
                    MediaPickType.CAMERA -> openCamera()
                    MediaPickType.GALLERY -> openGallery()
                    else -> Unit
                }
            }
            bottomSheet.show(childFragmentManager, "MediaPicker")
        }
        txtDate.setSafeOnClickListener {
            openDatePicker(noOfDays)
        }
        txtStartTime.setSafeOnClickListener {
            openTimePicker(true)
        }
        txtEndTime.setSafeOnClickListener {
            openTimePicker(false)
        }
        txtDealerCategory.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDealerCategoryList
            val bottomSheet = list?.let { it1 ->
                SelectDailyTourDealerCategoryBottomSheetFragment.Companion.newInstance(it1)
            }
            bottomSheet?.setDismissCallback { selected ->
                txtDealerCategory.setText(selected.Text)
                selectedDealerCategoryID = selected.TextListId ?: ""

                if (selected.Text == "Company Dealer"
                    || selected.Text == "Distributor"
                    || selected.Text == "New Dealer/Distributor Appointment"
                ) {
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
                        setEndIconTintList(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.primary
                            )
                        )
                    }
                } else {
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
            val bottomSheet = list?.let { it1 ->
                SelectDailyTourDealerNameBottomSheetFragment.Companion.newInstance(it1)
            }
            bottomSheet?.setDismissCallback { selected ->
                txtDealerName.setText(selected.Name)
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        txtDistrict.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDistrictList
            val bottomSheet = list?.let { it1 ->
                SelectDailyTourDistrictBottomSheetFragment.Companion.newInstance(it1)
            }
            bottomSheet?.setDismissCallback { selected ->
                txtDistrict.setText(selected.District)
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }

        cbDiscountDiscussion.setOnCheckedChangeListener { _, isChecked ->
            isDiscountDiscussion = if (isChecked) 1 else 0
        }

        cbSchemeDiscussion.setOnCheckedChangeListener { _, isChecked ->
            isSchemeDiscussion = if (isChecked) 1 else 0
        }

        cbSalesPromotion.setOnCheckedChangeListener { _, isChecked ->
            isSalesPromotional = if (isChecked) 1 else 0
        }

        cbStockPlanning.setOnCheckedChangeListener { _, isChecked ->
            isStockPlanning = if (isChecked) 1 else 0
        }

        cbServiceRepair.setOnCheckedChangeListener { _, isChecked ->
            isServiceOrRepairing = if (isChecked) 1 else 0
        }

        cbNewDealerAppointment.setOnCheckedChangeListener { _, isChecked ->
            isNewDealerAppointment = if (isChecked) 1 else 0
            updateDynamicField("NEW_DEALER_APPOINTMENT", "New Dealer Appointment Date *", isChecked)
        }

        cbSubDealerVisit.setOnCheckedChangeListener { _, isChecked ->
            isSubDealerVisit = if (isChecked) 1 else 0
            updateDynamicField("SUB_DEALER_VISIT", "Sub Dealer Visit Date *", isChecked)
        }

        cbNewDealerSurvey.setOnCheckedChangeListener { _, isChecked ->
            isNewDealerSurvey = if (isChecked) 1 else 0
            updateDynamicField("NEW_DEALER_SURVEY", "New Dealer Survey Date *", isChecked)
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

            val request = DailyTourAddDetailsRequest(
                date = txtDate.getTrimmedText(),
                startTime = txtStartTime.getTrimmedText(),
                endTime = txtEndTime.getTrimmedText(),
                fromPlace = txtFromPlace.getTrimmedText(),
                toPlace = txtToPlace.getTrimmedText(),
                typeTextListId = selectedDealerCategoryID,
                dealerName = txtDealerName.getTrimmedText(),
                district = txtDistrict.getTrimmedText(),
                area = txtTaluka.getTrimmedText(),
                businessCenter = txtMarketCenterName.getTrimmedText(),
                mobileNo = txtMobileNo.getTrimmedText(),
                pointDiscussion = txtCommonDiscussion.getTrimmedText(),
                empMobileNo = shredPref.getUser()?.MobileNo ?: "",
                isPaymentFollowUp = isPaymentFollowUp.toString(),
                isOrderFollowUp = isOrderFollowUp.toString(),
                isDiscountDiscussion = isDiscountDiscussion.toString(),
                isSchemeDiscussion = isSchemeDiscussion.toString(),
                isSalesPromotionalActivity = isSalesPromotional.toString(),
                isStockPlanning = isStockPlanning.toString(),
                isServiceOrRepairing = isServiceOrRepairing.toString(),
                isNewDealerAppointment = isNewDealerAppointment.toString(),
                isSubDealerVisit = isSubDealerVisit.toString(),
                isNewDealerSurvey = isNewDealerSurvey.toString(),
                newDealerAppointmentDt = dynamicFieldMap["NEW_DEALER_APPOINTMENT"]?.text.toString(),
                subDealerVisitDate = dynamicFieldMap["SUB_DEALER_VISIT"]?.text.toString(),
                newDealerSurveyDate = dynamicFieldMap["NEW_DEALER_SURVEY"]?.text.toString(),
                orderFollowUpDt = dynamicFieldMap["ORDER_DISCUSSION"]?.text.toString(),
                paymentFollowUpDt = dynamicFieldMap["PAYMENT_DISCUSSION"]?.text.toString(),
                paymentFollowUpAmount = dynamicFieldMap["PAYMENT_AMOUNT"]?.text.toString(),
                imageUri = if (BuildConfig.FLAVOR.equals("unnati", true)) selectedImageUri else null
            )
            dailyTourViewModel.insertDailyDetails(request)

        }
    }

    private fun validateFormWithToast(): Boolean = with(binding) {

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

        if (txtFromPlace.isEmpty()) {
            showToast("Please enter from place")
            return false
        }

        if (txtToPlace.isEmpty()) {
            showToast("Please enter to place")
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

    private fun updateDynamicField(key: String, hint: String, isChecked: Boolean) {
        if (isChecked) {
            selectedFields.add(key)
        } else {
            selectedFields.remove(key)
        }

        rebuildDynamicLayout()
    }

    private fun rebuildDynamicLayout() = with(binding) {
        constDynamic.removeAllViews()
        dynamicFieldMap.clear()

        val inflater = LayoutInflater.from(requireContext())

        fieldOrder.forEach { key ->

            if (selectedFields.contains(key)) {
                val view = inflater.inflate(R.layout.item_followup_field, constDynamic, false)
                val textInputLayout =
                    view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayout)
                val editText = view.findViewById<TextInputEditText>(R.id.edtDynamic)
                dynamicFieldMap[key] = editText
                when (key) {

                    "NEW_DEALER_APPOINTMENT" -> {
                        textInputLayout.hint = "New Dealer Appointment Date *"
                        textInputLayout.isHintEnabled = true
                        textInputLayout.isHintAnimationEnabled = true
                        editText.hint = null

                        editText.setOnClickListener {
                            openDatePicker(editText)
                        }
                    }

                    "SUB_DEALER_VISIT" -> {
                        textInputLayout.hint = "Sub Dealer Visit Date *"
                        textInputLayout.isHintEnabled = true
                        textInputLayout.isHintAnimationEnabled = true
                        editText.hint = null

                        editText.setOnClickListener {
                            openDatePicker(editText)
                        }
                    }

                    "NEW_DEALER_SURVEY" -> {
                        textInputLayout.hint = "New Dealer Survey Date *"
                        textInputLayout.isHintEnabled = true
                        textInputLayout.isHintAnimationEnabled = true
                        editText.hint = null

                        editText.setOnClickListener {
                            openDatePicker(editText)
                        }
                    }

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
                            inputType =
                                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
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
                "NEW_DEALER_APPOINTMENT" -> {
                    if (value.isEmpty()) {
                        showToast("Please select New Dealer Appointment Date *")
                        editText.requestFocus()
                        return false
                    }
                }

                "SUB_DEALER_VISIT" -> {
                    if (value.isEmpty()) {
                        showToast("Please select Sub Dealer Visit Date *")
                        editText.requestFocus()
                        return false
                    }
                }

                "NEW_DEALER_SURVEY" -> {
                    if (value.isEmpty()) {
                        showToast("Please select New Dealer Survey Date *")
                        editText.requestFocus()
                        return false
                    }
                }

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
            val selectedDate =
                SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(selection))
            binding.txtDate.setText(selectedDate)
            if (BuildConfig.FLAVOR != "mascot") {
                checkPJCEntry()
            }
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
                        Log.e("expenseRights", expenseRights)
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

    private fun observePJCEntryData() {
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
                                loadFragment(
                                    fragment = pjcInsertPlanFragment,
                                    isAdd = false,
                                    isAddBackStack = true
                                )
                                binding.txtDate.setText("")
                            }

                            "No" -> {
                                binding.txtDate.setText("")
                            }
                        }
                    }
                    bottomSheet.show(childFragmentManager, "SelectCommon")
                }

                // =========================
                // UNCOVER TYPE CASE
                // =========================

                is CheckPJCEntryUiState.UncoverType -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast(state.message)
                    binding.txtDate.setText("")
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
                    Log.e("NoOfDays", "${firstItem?.noOfDays ?: "0"}")
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


    // -------------------- MEDIA OPENERS --------------------
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCameraInternal()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCameraInternal() {
        val file = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        cameraImageFile = file
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        cameraLauncher.launch(cameraImageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun openFile() {
        pendingPickType = AttachmentType.FILE
        documentLauncher.launch(arrayOf("*/*"))
    }
    // -------------------- CORE UPLOAD HANDLER --------------------

    /**
     * Camera-specific path: we already have the File on disk — no URI copy needed.
     * This avoids the SecurityException / IOException that happens when you try to
     * openInputStream() on a FileProvider URI from the same app.
     */
    private fun handleCameraFile(file: File, type: AttachmentType) {
        // Expose as a Uri so the rest of the request pipeline can use it
        selectedImageUri = Uri.fromFile(file)

        val mimeType = "image/jpeg"
        val item = UploadAttachmentItem(
            fileName = file.name,
            filePath = file.absolutePath,
            fileType = mimeType,
            attachmentType = type.name
        )

        photoList.clear()
        photoList.add(item)
        showPhotoList()
    }

    // -------------------- CORE UPLOAD HANDLER --------------------
    private fun handleSelectedUri(uri: Uri, type: AttachmentType) {

        val context = requireContext()

        // store latest selected uri
        selectedImageUri = uri

        val fileName = getFileNameFromUri(context, uri)
        val mimeType = context.contentResolver.getType(uri) ?: ""
        val cacheFile = uriToCacheFile(context, uri)

        val item = UploadAttachmentItem(
            fileName = fileName,
            filePath = cacheFile.absolutePath,
            fileType = mimeType,
            attachmentType = type.name
        )

        when (type) {

            AttachmentType.PHOTO -> {
                photoList.clear()   // keep only 1 item
                photoList.add(item)
                showPhotoList()
            }

            AttachmentType.FILE -> {
                documentList.clear()   // keep only 1 item
                documentList.add(item)
                showDocumentList()
            }

            AttachmentType.AUDIO -> Unit
        }
    }

    private fun showPhotoList() {
        if (photoList.isEmpty()) {
            binding.lylAttachmentPhotoListRow.visibility = View.GONE
            return
        }

        binding.lylAttachmentPhotoListRow.visibility = View.VISIBLE
        renderAttachmentList(
            binding.lylAttachmentPhotoListRow,
            photoList,
            AttachmentType.PHOTO
        )
    }

    private fun showDocumentList() {
        if (documentList.isEmpty()) {
            binding.lylAttachmentPhotoListRow.visibility = View.GONE
            return
        }

        binding.lylAttachmentPhotoListRow.visibility = View.VISIBLE
        renderAttachmentList(
            binding.lylAttachmentPhotoListRow,
            documentList,
            AttachmentType.FILE
        )
    }

    private fun renderAttachmentList(
        parent: LinearLayout,
        list: MutableList<UploadAttachmentItem>,
        type: AttachmentType
    ) {
        parent.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        list.forEachIndexed { index, item ->

            val view = inflater.inflate(R.layout.layout_upload_photo, parent, false)

            val fileNameTv = view.findViewById<TextView>(R.id.fileName)
            val removeBtn = view.findViewById<ImageView>(R.id.downloadAttach)

            fileNameTv.text = item.fileName ?: "Unknown file"
            removeBtn.setImageResource(R.drawable.ic_close)

            removeBtn.setOnClickListener {
                list.removeAt(index)

                when (type) {
                    AttachmentType.PHOTO -> showPhotoList()
                    AttachmentType.FILE -> showDocumentList()
                    else -> Unit
                }
            }

            parent.addView(view)
        }
    }

    // -------------------- FILE INFO FROM URI --------------------

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var name = "file_${System.currentTimeMillis()}"

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) {
                name = cursor.getString(index)
            }
        }
        return name
    }

    private fun uriToCacheFile(context: Context, uri: Uri): File {
        val fileName = getFileNameFromUri(context, uri)
        val file = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}