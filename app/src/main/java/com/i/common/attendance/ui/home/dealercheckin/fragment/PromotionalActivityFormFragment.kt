package com.i.common.attendance.ui.home.dealercheckin.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
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
import com.google.android.material.textfield.TextInputLayout
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentPromotionalActivityFormBinding
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.GetCityTypeListDukeRequest
import com.i.common.attendance.network.request.SavePromotionalActivityRequest
import com.i.common.attendance.network.response.UploadAttachmentItem
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.attendancereport.fragment.SelectMonthBottomSheetFragment
import com.i.common.attendance.ui.home.attendancereport.viewmodel.AttendanceReportViewModel
import com.i.common.attendance.ui.home.attendancereport.viewmodel.MonthUiState
import com.i.common.attendance.ui.home.carairapproval.fragment.SelectCityBottomSheetFragment
import com.i.common.attendance.ui.home.carairapproval.fragment.SelectCityTypeBottomSheetFragment
import com.i.common.attendance.ui.home.carairapproval.viewmodel.CarAirApprovalViewModel
import com.i.common.attendance.ui.home.dailytour.fragment.SelectDailyTourDealerCategoryBottomSheetFragment
import com.i.common.attendance.ui.home.dailytour.fragment.SelectDailyTourDealerNameBottomSheetFragment
import com.i.common.attendance.ui.home.dailytour.fragment.SelectDailyTourDistrictBottomSheetFragment
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourViewModel
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerNameState
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.PromotionalActivityUiState
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.PromotionalActivityViewModel
import com.i.common.attendance.ui.home.tourvoucher.data.CommonSelect
import com.i.common.attendance.ui.home.tourvoucher.data.MediaPickType
import com.i.common.attendance.ui.home.tourvoucher.fragment.CommonSelectBottomSheetFragment
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.AttachmentType
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PromotionalActivityFormFragment : BaseFragment() {

    private lateinit var binding: FragmentPromotionalActivityFormBinding
    private val dailyTourViewModel: DailyTourViewModel by viewModels()
    private val attendanceReportViewmodel: AttendanceReportViewModel by viewModels()
    private val carAirApprovalViewModel: CarAirApprovalViewModel by viewModels()
    private val promotionalActivityViewModel: PromotionalActivityViewModel by viewModels()
    @Inject
    lateinit var shredPref: EncryptedPrefHelper
    private var selectedDealerCategoryID = ""
    private var selectedDealerID = ""
    private var selectedDistrictID = ""
    private var selectedCitiesId : String = ""

    private lateinit var cameraImageUri: Uri
    private var cameraImageFile: File? = null
    private var selectedImageUri : Uri ?= null
    private var pendingPickType: AttachmentType? = null
    private val photoList = mutableListOf<UploadAttachmentItem>()
    private val documentList = mutableListOf<UploadAttachmentItem>()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromotionalActivityFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveOnClickListeners()
        manageToolBar()
        observeDealerCategory()
        observeDealerName()
        observeMonthList()
        observePromotionalActivityApi()
        initApiCall()
    }

    private fun initApiCall() {
        attendanceReportViewmodel.loadMonthList()
        dailyTourViewModel.getDealerCategory(
            DailyTourDealerCategoryRequest(
                type = "Weekly",
                deptId = ""
            )
        )
        promotionalActivityViewModel.getDistrictList()
        carAirApprovalViewModel.loadCityList()
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_promotional_activity_form))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun moveOnClickListeners() = with(binding) {
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
        txtMonth.setSafeOnClickListener {
            val list = attendanceReportViewmodel.getCachedMonthList()
            val bottomSheet =
                list?.let { it1 -> SelectMonthBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtMonth.setText(selected.Month)
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
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
                selectedDealerID = selected.DealerId?:""
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        txtDistrict.setSafeOnClickListener {
            val list = promotionalActivityViewModel.cachedDistrictList
            val bottomSheet = list?.let { it1 ->
                SelectDailyTourDistrictBottomSheetFragment.Companion.newInstance(it1)
            }
            bottomSheet?.setDismissCallback { selected ->
                txtDistrict.setText(selected.District)
                selectedDistrictID = selected.DistrictId ?: ""
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        txtCenterName.setSafeOnClickListener {
            val list = carAirApprovalViewModel.cachedCityList
            val bottomSheet = list?.let { it1 -> SelectCityBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtCenterName.setText(selected.city?:"")
                selectedCitiesId = selected.cityId?:""
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
        btnSubmit.setSafeOnClickListener {
            validateAndSubmitPromotionalActivity()
        }
    }

    private fun validateAndSubmitPromotionalActivity(): Boolean {
        when {

            binding.txtMonth.getTrimmedText().isEmpty() -> {
                showToast("Please select month")
                return false
            }

            binding.txtActivityName.getTrimmedText().isEmpty() -> {
                showToast("Please enter activity name")
                return false
            }

            selectedDealerCategoryID.isEmpty() -> {
                showToast("Please select dealer category")
                return false
            }

            binding.txtDealerName.getTrimmedText().isEmpty() -> {
                showToast("Please enter/select dealer name")
                return false
            }

            binding.txtDistrict.getTrimmedText().isEmpty() -> {
                showToast("Please select district")
                return false
            }

            selectedCitiesId.isEmpty() -> {
                showToast("Please select center name")
                return false
            }

            binding.txtApproxExpense.getTrimmedText().isEmpty() -> {
                showToast("Please enter approx expense")
                return false
            }

            selectedImageUri == null -> {
                showToast("Please attach photo")
                return false
            }
        }

        val request = SavePromotionalActivityRequest(
            month = binding.txtMonth.getTrimmedText().trim(),
            activityName = binding.txtActivityName.getTrimmedText().trim(),
            dealerCategoryId = selectedDealerCategoryID,
            dealerName = binding.txtDealerName.getTrimmedText().trim(),
            dealerId = selectedDealerID,
            districtId = selectedDistrictID,
            cityId = selectedCitiesId,
            approxExpense = binding.txtApproxExpense.text.toString().trim(),
            userId = shredPref.getUser()?.AutoId?:"",
            empId = shredPref.getUser()?.EmpID?:"",
            imageUri = selectedImageUri
        )

        promotionalActivityViewModel.insertPromotionalActivity(request)

        return true
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

    private fun observeMonthList() {
        attendanceReportViewmodel.monthListState.observe(viewLifecycleOwner) { state ->
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

    private fun observePromotionalActivityApi() {
        promotionalActivityViewModel.savePromotionalActivityState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is PromotionalActivityUiState.Idle -> {
                }

                is PromotionalActivityUiState.Loading -> {
                    showLoader()
                }

                is PromotionalActivityUiState.Success -> {
                    hideLoader()
                    showToast(state.response.message)
                    parentFragmentManager.popBackStackImmediate()
                }

                is PromotionalActivityUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is PromotionalActivityUiState.NetworkError -> {
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
}