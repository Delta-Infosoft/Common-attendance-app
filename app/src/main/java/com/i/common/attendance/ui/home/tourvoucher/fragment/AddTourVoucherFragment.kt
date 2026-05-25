package com.i.common.attendance.ui.home.tourvoucher.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
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
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentAddTourVoucherBinding
import com.i.common.attendance.network.request.CheckPJCEntryRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.network.request.SaveTourVoucherRequest
import com.i.common.attendance.network.request.TourVoucherEditDataRequest
import com.i.common.attendance.network.request.TravelAttachmentDeleteRequest
import com.i.common.attendance.network.request.TravelingByRequest
import com.i.common.attendance.network.response.GetAttechmentTourVoucherRequest
import com.i.common.attendance.network.response.UploadAttachmentItem
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.adapter.DailyTourDetailsAdapter
import com.i.common.attendance.ui.home.dailytour.fragment.AddDailyTourDetailsFragment
import com.i.common.attendance.ui.home.tourvoucher.data.CommonSelect
import com.i.common.attendance.ui.home.tourvoucher.data.MediaPickType
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.AttachmentState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.AttachmentType
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.BackDateRightUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.CheckPJCEntryUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.PjcPermissionUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.SaveTourVoucherUiEditState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.SaveTourVoucherUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourDetailsState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TravelingByUiState
import com.i.common.attendance.ui.home.viewmodel.TravelAttachmentDeleteUiState
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.doubleValue
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.isEmpty
import com.i.common.attendance.utils.Constants.removeTrailingZeros
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
class AddTourVoucherFragment: BaseFragment() {

    private lateinit var binding : FragmentAddTourVoucherBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    private lateinit var cameraImageUri: Uri
    private var cameraImageFile: File? = null
    private var selectedImageUri : Uri ?= null
    private var pendingPickType: AttachmentType? = null
    private val photoList = mutableListOf<UploadAttachmentItem>()
    private val documentList = mutableListOf<UploadAttachmentItem>()

    var noOfDays: Int = 0
    private var expenseRights = ""
    private var expenseId = ""


    // -------------------- CAMERA --------------------
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                //handleSelectedUri(cameraImageUri, AttachmentType.PHOTO)
                cameraImageFile?.let { file ->
                    handleCameraFile(file, AttachmentType.PHOTO)
                }
            }
        }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCameraInternal()
            else showToast("Camera permission denied")
        }

    // -------------------- GALLERY --------------------
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                handleSelectedUri(it, AttachmentType.PHOTO)
            }
        }

    // -------------------- DOCUMENT / AUDIO --------------------
    private val documentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.FLAVOR != "flotech" && BuildConfig.FLAVOR != "singla" && BuildConfig.FLAVOR != "algo" && BuildConfig.FLAVOR != "mascot " && BuildConfig.FLAVOR != "unnati ") {
            tourVoucherViewmodel.loadTravelingByList(request = TravelingByRequest(type = "TravelBy"))
        }

        expenseId = arguments?.getString("ExpenseId")?:""

        if (expenseId.isNotEmpty()) {
            tourVoucherViewmodel.getTourDetails(request = TourVoucherEditDataRequest(expenseId))
            tourVoucherViewmodel.getAttachmentFileParam(
                GetAttechmentTourVoucherRequest(recordId = expenseId, attachmentType = AttachmentType.PHOTO.value),
                AttachmentType.PHOTO
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTourVoucherBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        manageOnClickListeners()
        initApiCall()

        observeTravelByData()
        observeSaveTourVoucherData()
        observeSaveTourVoucherEditData()
        observePJCEntryData()

        observeBackDateRight()
        observePjcPermissionData()

        observeEditData()
        observeAttechmentData()
        observeDeleteFile()
    }

    private fun initApiCall() = with(binding){
        val user = shredPref.getUser()
        if (BuildConfig.FLAVOR != "mascot") {
            tourVoucherViewmodel.getBackDatedRight( PjcDateRequest(empId = user?.EmpID?:""))
            tourVoucherViewmodel.getWithoutPJCTourRights(PjcDateRequest(empId = user?.EmpID?:""))
        }

        if (BuildConfig.FLAVOR == "flotech" || BuildConfig.FLAVOR == "singla" || BuildConfig.FLAVOR == "algo" || BuildConfig.FLAVOR == "mascot" || BuildConfig.FLAVOR == "unnati") {
            txtTravellingBy.apply {
                setText("")
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
                isClickable = true
            }

            txtLayTravellingBy.endIconMode = TextInputLayout.END_ICON_NONE
        }

        val hintText =
            if (BuildConfig.FLAVOR == "unnati") {
                "Daily allowance"
            } else {
                "Food allowance"
            }

        txtLayDailyAllowance.hint = hintText
        //txtDailyAllowance.hint = hintText

    }

    private fun manageOnClickListeners() = with(binding){
        //txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        //txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))

        linearAttachDocument.setSafeOnClickListener {
            val bottomSheet = CommonSelectBottomSheetFragment.newInstance(CommonSelect.DOCUMENT_FILE)

            bottomSheet.setDismissCallback {
                when (it) {
                    MediaPickType.DOCUMENT -> openFile()
                    else -> Unit
                }
            }
            bottomSheet.show(childFragmentManager, "MediaPicker")
        }
        linearAttachPhoto.setSafeOnClickListener {
            val bottomSheet = CommonSelectBottomSheetFragment.newInstance(CommonSelect.CAMERA_GALLERY)
            bottomSheet.setDismissCallback {
                when (it) {
                    MediaPickType.CAMERA -> openCamera()
                    MediaPickType.GALLERY -> openGallery()
                    else -> Unit
                }
            }
            bottomSheet.show(childFragmentManager, "MediaPicker")
        }

        txtFromDate.setSafeOnClickListener {
            openDatePicker(true,noOfDays)
        }
        txtToDate.setSafeOnClickListener {
            openDatePicker(false,noOfDays)
        }

        txtStartTime.setSafeOnClickListener {
            openTimePicker(true)
        }
        txtEndTime.setSafeOnClickListener {
            openTimePicker(false)
        }

        txtTravellingBy.setSafeOnClickListener {
            if (BuildConfig.FLAVOR != "flotech" || BuildConfig.FLAVOR != "singla" || BuildConfig.FLAVOR != "algo" || BuildConfig.FLAVOR != "mascot") {
                val list = tourVoucherViewmodel.getCachedTravelingByList()
                val bottomSheet = list?.let { it1 -> SelectTravelByBottomSheetFragment.Companion.newInstance(it1) }
                bottomSheet?.setDismissCallback { selected ->
                    txtTravellingBy.setText(selected.Text)
                }
                bottomSheet?.show(childFragmentManager, "SelectPlanFor")
            }
        }

        btnSubmit.setSafeOnClickListener {
            if (!validateFormWithToast()) {
                return@setSafeOnClickListener
            }

            val request = SaveTourVoucherRequest(
                empMobileNo = shredPref.getUser()?.MobileNo ?: "",
                fromDate = txtFromDate.getTrimmedText(),
                toDate = txtToDate.getTrimmedText(),
                fromPlace = txtFromPlace.getTrimmedText(),
                toPlace = txtToPlace.getTrimmedText(),
                startTime = txtStartTime.getTrimmedText(),
                endTime = txtEndTime.getTrimmedText(),
                nightHault = if (radioButtonYes.isChecked) "true" else "false",
                travellingBy = txtTravellingBy.getTrimmedText(),
                fareAmt = txtFareAmount.getTrimmedText(),
                autoCharges = txtAutoCharges.getTrimmedText(),
                autoChargesDetail = txtAutoChargesDetails.getTrimmedText(),
                lodging = txtLodging.getTrimmedText(),
                dailyAllowance = txtDailyAllowance.getTrimmedText(),
                otherExpenses = txtOtherExpense.getTrimmedText(),
                otherChargesDetails = txtExtraExpenseDetails.getTrimmedText(),
                totalExpenses = calculateTotalExpense(),
                expenseId = expenseId,
                designation = "",
                departmentId = "",
                imageUri = selectedImageUri
            )

            if(expenseId.isNotEmpty()){
                tourVoucherViewmodel.saveTourEditVoucher(request)
            }else{
                tourVoucherViewmodel.saveTourVoucher(request)
            }
        }

    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toobar_title_tour_voucher))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    // -------------------- MEDIA OPENERS --------------------
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCameraInternal()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCameraInternal() {
        val file = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        cameraImageFile = file
        cameraImageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        cameraLauncher.launch(cameraImageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun openFile(){
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

    private fun openDatePicker(isFromDate: Boolean, noOfDays: Int) {

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
            if (isFromDate) {
                binding.txtFromDate.setText(selectedDate)
            } else {
                binding.txtToDate.setText(selectedDate)
            }

            checkPJCEntry(selectedDate)
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

    private fun checkPJCEntry(date : String) = with(binding){

        val fromDate = binding.txtFromDate.text?.toString()?.trim().orEmpty()
        val toDate = binding.txtToDate.text?.toString()?.trim().orEmpty()

        if (!expenseRights.equals("True", ignoreCase = true)) {
            if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                val request = CheckPJCEntryRequest(
                    mobileNo = shredPref.getUser()?.MobileNo ?: "",
                    fromDate = txtFromDate.getTrimmedText(),
                    toDate = txtToDate.getTrimmedText(),
                    date = txtFromDate.getTrimmedText(),
                    type = "CHECK_DAILY_TOUR_DETAILS"
                )

                tourVoucherViewmodel.checkPJCEntry(request)
            }
        }

    }


    /*==========================================================================================*/
    /*========================== Observer ===========================*/
    private fun observeTravelByData(){
        tourVoucherViewmodel.travelingByState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TravelingByUiState.Loading -> {
                    showLoader()
                }

                is TravelingByUiState.Success -> {
                    hideLoader()
                }

                is TravelingByUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is TravelingByUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    private fun observeSaveTourVoucherData(){
        tourVoucherViewmodel.saveTourVoucherState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is SaveTourVoucherUiState.Loading -> {
                    manageButtonVisibility(true)
                    showLoader()
                }

                is SaveTourVoucherUiState.Success -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }

                is SaveTourVoucherUiState.ApiError -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast(state.message)
                }

                is SaveTourVoucherUiState.NetworkError -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    private fun observeSaveTourVoucherEditData(){
        tourVoucherViewmodel.saveTourVoucherEditState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is SaveTourVoucherUiEditState.Loading -> {
                    manageButtonVisibility(true)
                    showLoader()
                }

                is SaveTourVoucherUiEditState.Success -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }

                is SaveTourVoucherUiEditState.ApiError -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast(state.message)
                }

                is SaveTourVoucherUiEditState.NetworkError -> {
                    manageButtonVisibility(false)
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
                    //showToast("Tour voucher allowed")
                }

                is CheckPJCEntryUiState.NotAllowed -> {
                    manageButtonVisibility(false)
                    hideLoader()
                    showToast("Not filed dates: ${state.notFiledDates}")


                    val bottomSheet = SelectCommonDialogBottomSheetFragment.Companion.newInstance(
                        errorText = "You had not filed daily tour details for these dates: ${state.notFiledDates}",
                        pageText = "Would you like to open Daily Tour Details form?"
                    )

                    bottomSheet.setDismissCallback { selected ->
                        when (selected) {
                            "Yes" -> {
                                binding.txtFromDate.setText("")
                                binding.txtToDate.setText("")
                                loadFragment(AddDailyTourDetailsFragment(),false,true)
                                //startActivity(Intent(requireContext(), DailyWorkDetailsActivity::class.java))
                            }
                            "No" -> {
                                binding.txtFromDate.setText("")
                                binding.txtToDate.setText("")
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

    /*private fun openErrorDialogBox() {

        val errorMsg =
            "You had not filed daily tour details for this dates : $notFiledDate"

        val pageOpenTo = getString(R.string.tour_voucher_page_open)

        val fragment = ErrorMesageFragment(
            errorMsg,
            pageOpenTo
        ) { answer ->

            // Clear dates
            binding.txtFromDate.setText("")
            binding.txtToDate.setText("")

            if (answer.equals("YES", ignoreCase = true)) {
                startActivity(
                    Intent(requireContext(), DailyWorkDetailsActivity::class.java)
                )
            }
        }

        fragment.isCancelable = false
        fragment.show(parentFragmentManager, "bottom_sheet")
    }*/

    private fun calculateTotalExpense(): String = with(binding) {
        val total = txtFareAmount.doubleValue() +
                    txtAutoCharges.doubleValue() +
                    txtLodging.doubleValue() +
                    txtDailyAllowance.doubleValue() +
                    txtOtherExpense.doubleValue()
        txtViewTotalExpense.text = getString(R.string.place_holder_total_expense,total.toInt().toString())
        return total.toInt().toString()
    }

    private fun validateFormWithToast(): Boolean = with(binding)  {

        if (BuildConfig.FLAVOR.equals("unnati", true)) {
            val allowance = txtDailyAllowance.text.toString().toDoubleOrNull() ?: 0.0
            val maxAllowance = shredPref.getUser()?.DailyCost?.toDoubleOrNull() ?: 0.0
            if (allowance > maxAllowance) {
                showToast("Daily Allowance should not be greater than ₹$maxAllowance")
                return false
            }
        }

        if (txtFromDate.isEmpty()) {
            showToast("Please select from date")
            return false
        }

        if (txtToDate.isEmpty()) {
            showToast("Please select to date")
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

        if (txtStartTime.isEmpty()) {
            showToast("Please select start time")
            return false
        }

        if (txtEndTime.isEmpty()) {
            showToast("Please select end time")
            return false
        }

        if (txtTravellingBy.isEmpty()) {
            showToast("Please select travelling by")
            return false
        }

        if (txtFareAmount.isEmpty()) {
            showToast("Please enter fare amount")
            return false
        }

        return true
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

    private fun observeEditData() = with(binding){
        tourVoucherViewmodel.tourDetailsState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is TourDetailsState.Loading -> showLoader()

                is TourDetailsState.Success -> {
                    hideLoader()

                    val item = state.data

                    // 🔥 Prefill fields
                    txtFromDate.setText(Constants.convertDateFormat(
                            item.TravelDt,
                            "dd-MMM-yyyy hh:mm:ss a",
                            "dd-MMM-yyyy"
                        ))
                    txtToDate.setText(Constants.convertDateFormat(
                            item.TravelToDt,
                            "dd-MMM-yyyy hh:mm:ss a",
                            "dd-MMM-yyyy"
                        ))
                    txtFromPlace.setText(item.FromPlace ?: "")
                    txtToPlace.setText(item.ToPlace ?: "")
                    txtStartTime.setText(Constants.convertDateFormat(
                        item.StartTime,
                        "dd-MMM-yyyy hh:mm:ss a",
                        "hh:mm a"
                    ))
                    txtEndTime.setText(Constants.convertDateFormat(
                        item.EndTime,
                        "dd-MMM-yyyy hh:mm:ss a",
                        "hh:mm a"
                    ))
                    radioGroupNightHold.check(if (item.NighHault == "False") R.id.radioButtonNo else R.id.radioButtonYes)
                    txtTravellingBy.setText(item.TravellingBy)
                    txtFareAmount.setText(item.FareAmount?.removeTrailingZeros())
                    txtAutoCharges.setText(item.AutoCharges?.removeTrailingZeros())
                    txtAutoChargesDetails.setText(item.AutoChargesDetail?:"")
                    txtLodging.setText(item.Lodging?.removeTrailingZeros())
                    txtDailyAllowance.setText(item.DailyAllowance?.removeTrailingZeros())
                    txtOtherExpense.setText(item.OtherExpenses?.removeTrailingZeros())
                    txtExtraExpenseDetails.setText(item.OtherChargesDetail?:"")
                    txtViewTotalExpense.text = getString(R.string.place_holder_total_expense,item.TotalExpenses?.removeTrailingZeros())
                }

                is TourDetailsState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }

                is TourDetailsState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }

                is TourDetailsState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    private fun observeAttechmentData() = with(binding){
        tourVoucherViewmodel.attachmentState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is AttachmentState.Loading -> {
                    showLoader()
                }

                is AttachmentState.Success -> {
                    hideLoader()

                    val attachmentList = state.list

                    if (attachmentList.isNotEmpty()) {
                        showPhotoList(state.list)
                    }
                }

                is AttachmentState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }

                is AttachmentState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    private fun observeDeleteFile() = with(binding){
        tourVoucherViewmodel.deleteState.observe(viewLifecycleOwner) { state ->

            when (state) {

                TravelAttachmentDeleteUiState.Loading -> {
                    showLoader()
                }

                is TravelAttachmentDeleteUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    tourVoucherViewmodel.getAttachmentFileParam(
                        GetAttechmentTourVoucherRequest(recordId = expenseId, attachmentType = AttachmentType.PHOTO.value),
                        AttachmentType.PHOTO
                    )
                }

                is TravelAttachmentDeleteUiState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }
    private fun showPhotoList(list: List<UploadAttachmentItem>) {
        binding.lylAttachmentPhotoListRow.visibility = View.VISIBLE
        renderAttachmentList(
            parent = binding.lylAttachmentPhotoListRow,
            list = list
        )
    }

    private fun renderAttachmentList(parent: LinearLayout, list: List<UploadAttachmentItem>) {
        parent.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        list.forEach { model ->
            val view = inflater.inflate(R.layout.layout_upload_photo, parent, false)

            val fileName = view.findViewById<TextView>(R.id.fileName)
            val downloadAttach = view.findViewById<ImageView>(R.id.downloadAttach)
            val deleteBtn = view.findViewById<ImageView>(R.id.imgDelete)
            deleteBtn.visibility = View.VISIBLE

            fileName.text = model.fileName ?: "Unknown file"

            downloadAttach.setOnClickListener {
                openAttachment(model.file1)
            }

            // 🗑️ Delete (API call)
            deleteBtn.setSafeOnClickListener {
                list[0].recordId?.let { recordId ->
                    list[0].fUId?.let{ fuId ->
                        tourVoucherViewmodel.deleteTravelAttachment(
                            TravelAttachmentDeleteRequest(
                                recordId = recordId,
                                fuId = fuId
                            )
                        )
                    }
                }
            }
            parent.addView(view)
        }
    }

    private fun openAttachment(fileUrl: String?) {
        if (fileUrl.isNullOrEmpty()) {
            showToast("File path is invalid")
            return
        }

        try {
            val uri = Uri.parse(fileUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            }
            startActivity(intent)
        } catch (e: Exception) {
            showToast("Unable to open file")
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


