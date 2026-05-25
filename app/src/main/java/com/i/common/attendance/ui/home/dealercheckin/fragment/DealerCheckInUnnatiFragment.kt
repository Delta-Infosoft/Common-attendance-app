package com.i.common.attendance.ui.home.dealercheckin.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentDealerCheckInUnnatiBinding
import com.i.common.attendance.network.request.CheckDealerInOutStatusRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.SaveDealerCheckInRequest
import com.i.common.attendance.network.response.UploadAttachmentItem
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.fragment.SelectDailyTourDealerCategoryBottomSheetFragment
import com.i.common.attendance.ui.home.dailytour.fragment.SelectDailyTourDealerNameBottomSheetFragment
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourViewModel
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerNameState
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.CheckDealerInOutStatusState
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.DealerCheckInState
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.PromotionalActivityViewModel
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.AttachmentType
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTodayDateFormatted
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DealerCheckInUnnatiFragment : BaseFragment() {

    // ─────────────────────────────────────────────────────────────────────────
    //  Binding / ViewModel / DI
    // ─────────────────────────────────────────────────────────────────────────

    private lateinit var binding: FragmentDealerCheckInUnnatiBinding
    private val dailyTourViewModel: DailyTourViewModel by viewModels()
    private val dealerCheckInViewModel: PromotionalActivityViewModel by viewModels()

    @Inject lateinit var shredPref: EncryptedPrefHelper
    private var selectedImageUri: Uri? = null

    // ─────────────────────────────────────────────────────────────────────────
    //  State
    // ─────────────────────────────────────────────────────────────────────────

    private var capturedImagePath = ""
    private var capturedLat = ""
    private var capturedLon = ""
    private var selectedDealerCategoryID = ""
    private var selectedDealerID = ""

    // ─────────────────────────────────────────────────────────────────────────
    //  Location Permission Launcher
    //
    //  Full check order on "Attach Photo" tap:
    //    1. Location permission granted?  NO  → ask → if still NO → stop
    //    2. GPS (device location) enabled? NO  → show dialog to open Settings
    //    3. Both OK → openGpsCamera()
    // ─────────────────────────────────────────────────────────────────────────

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val fineGranted   = results[Manifest.permission.ACCESS_FINE_LOCATION]   == true
            val coarseGranted = results[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                // Permission just granted — now verify GPS hardware is on
                checkGpsEnabledAndOpenCamera()
            } else {
                showToast("Location permission is required to stamp GPS coordinates on the photo.")
            }
        }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDealerCheckInUnnatiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenResultGpsPhoto()
        manageToolBar()
        setClickListeners()
        observeDealerCategory()
        observeDealerName()
        observeSaveDealerCheckIn()
        observeCheckDealerInOutStatus()
        initApiCall()
    }

    private fun initApiCall() {
        dealerCheckInViewModel.checkDealerInOutStatus(CheckDealerInOutStatusRequest(userName = shredPref.getUser()?.MobileNo ?: ""))
        dailyTourViewModel.getDealerCategory(DailyTourDealerCategoryRequest(type = "Weekly", deptId = ""))
    }

    private fun listenResultGpsPhoto() {
        parentFragmentManager.setFragmentResultListener(
            GpsPhotoFragment.GPS_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val imagePath = bundle.getString(GpsPhotoFragment.KEY_IMAGE_PATH, "")
            val lat       = bundle.getString(GpsPhotoFragment.KEY_LAT, "")
            val lon       = bundle.getString(GpsPhotoFragment.KEY_LON, "")

            capturedImagePath = imagePath
            capturedLat       = lat
            capturedLon       = lon
            onGpsPhotoReceived(imagePath)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Toolbar
    // ─────────────────────────────────────────────────────────────────────────

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_dealer_check_in))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Click listeners
    // ─────────────────────────────────────────────────────────────────────────

    private fun setClickListeners() = with(binding) {

        // Attach Photo tap → check location permission first, then open GPS camera
        linearAttachPhoto.setSafeOnClickListener {
            checkLocationPermissionAndOpenCamera()
        }

        txtDealerCategory.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDealerCategoryList ?: return@setSafeOnClickListener
            SelectDailyTourDealerCategoryBottomSheetFragment.newInstance(list)
                .apply {
                    setDismissCallback { selected ->
                        txtDealerCategory.setText(selected.Text)
                        selectedDealerCategoryID = selected.TextListId ?: ""

                        if (selected.Text == "Company Dealer" ||
                            selected.Text == "Distributor" ||
                            selected.Text == "New Dealer/Distributor Appointment"
                        ) {
                            val user = shredPref.getUser()
                            dailyTourViewModel.getDealerName(
                                DailyTourDealerNameRequest(
                                    empId      = user?.EmpID ?: "",
                                    dealerType = selected.Text ?: ""
                                )
                            )
                            txtDealerName.apply {
                                isFocusable          = false
                                isFocusableInTouchMode = false
                                isCursorVisible      = false
                                isClickable          = true
                            }
                            txtLayDealerName.apply {
                                endIconMode    = TextInputLayout.END_ICON_CUSTOM
                                endIconDrawable = ContextCompat.getDrawable(
                                    context, R.drawable.ic_arrow_drop_down
                                )
                                setEndIconTintList(
                                    ContextCompat.getColorStateList(context, R.color.primary)
                                )
                            }
                        } else {
                            txtDealerName.apply {
                                setText("")
                                isFocusable          = true
                                isFocusableInTouchMode = true
                                isCursorVisible      = true
                                isClickable          = true
                            }
                            txtLayDealerName.endIconMode = TextInputLayout.END_ICON_NONE
                        }
                    }
                }
                .show(childFragmentManager, "SelectDealerCategory")
        }

        txtDealerName.setSafeOnClickListener {
            val list = dailyTourViewModel.cachedDealerNameList ?: return@setSafeOnClickListener
            SelectDailyTourDealerNameBottomSheetFragment.newInstance(list)
                .apply {
                    setDismissCallback { selected ->
                        txtDealerName.setText(selected.Name)
                        selectedDealerID = selected.DealerId ?: ""
                    }
                }
                .show(childFragmentManager, "SelectDealerName")
        }

        btnCheckIn.setSafeOnClickListener {
            if (validateDealerCheckIn()) {
                val request = SaveDealerCheckInRequest(
                    mobileNo = shredPref.getUser()?.MobileNo?:"",
                    dealerCategory   = txtDealerCategory.text.toString(),
                    dealerCategoryId = selectedDealerCategoryID,
                    dealerName       = txtDealerName.text.toString(),
                    dealerId         = selectedDealerID,
                    lat              = capturedLat,
                    long             = capturedLon,
                    remarks          = txtRemarks.text.toString(),
                    checkInTime      = if (btnCheckIn.text.equals(getString(R.string.label_check_in)))
                        Constants.getCurrentTimestamp("dd-MMM-yyyy hh:mm:ss") else "",
                    checkOutTime     = if (btnCheckIn.text.equals(getString(R.string.label_check_in)))
                        "" else Constants.getCurrentTimestamp("dd-MMM-yyyy hh:mm:ss"),
                    photoUri         = selectedImageUri
                )
                dealerCheckInViewModel.insertDealerCheckIn(request)
            }
        }
    }

    private fun validateDealerCheckIn(): Boolean = with(binding) {
        when {
            txtDealerCategory.text.toString().trim().isEmpty() -> {
                showToast("Please select dealer category")
                return false
            }
            txtDealerName.text.toString().trim().isEmpty() -> {
                showToast("Please enter dealer name")
                return false
            }
            selectedImageUri == null -> {
                showToast("Please upload GPS photo")
                return false
            }
        }
        true
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Location Permission + GPS Check
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns true if at least one location permission (fine or coarse) is
     * already granted.
     */
    private fun isLocationPermissionGranted(): Boolean {
        val ctx = context ?: return false
        return ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    ctx, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Returns true if the device GPS provider is currently enabled.
     * Uses LocationManager — no permission needed for this check.
     */
    private fun isGpsEnabled(): Boolean {
        val locationManager = context?.getSystemService<LocationManager>() ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Shows an AlertDialog asking the user to turn on GPS.
     * "Open Settings" deep-links directly to the Location Settings screen.
     * "Cancel" dismisses without opening the camera.
     */
    private fun showGpsDisabledDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("GPS Disabled")
            .setMessage("GPS is turned off. Please enable location/GPS to capture a geotagged photo.")
            .setPositiveButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Step 1 — Entry point when the user taps "Attach Photo".
     *
     * Checks location permission first.
     * If not granted → request it (result handled in [locationPermissionLauncher]).
     * If already granted → move to step 2 (GPS check).
     */
    private fun checkLocationPermissionAndOpenCamera() {
        if (isLocationPermissionGranted()) {
            checkGpsEnabledAndOpenCamera()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    /**
     * Step 2 — Called after permission is confirmed.
     *
     * Checks whether the device GPS hardware is switched on.
     * If OFF → show dialog directing user to Settings.
     * If ON  → open GPS camera.
     */
    private fun checkGpsEnabledAndOpenCamera() {
        if (isGpsEnabled()) {
            openGpsCamera()
        } else {
            showGpsDisabledDialog()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Open GPS Camera
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Only called after BOTH location permission AND GPS hardware are confirmed.
     *
     * WHY FragmentResultListener instead of a lambda callback:
     * When loadFragment() replaces this fragment and adds it to the back stack,
     * Android may destroy its view entirely. FragmentResultListener survives this
     * and fires the moment this fragment's view is STARTED again — no race condition.
     */
    private fun openGpsCamera() {
        val gpsFragment = GpsPhotoFragment.newInstance(siteName = "Dealer Check-In")
        loadFragment(gpsFragment, false, true)
    }

    /**
     * Called once [GpsPhotoFragment] delivers the saved image via FragmentResult.
     */
    private fun onGpsPhotoReceived(imagePath: String) = with(binding) {
        if (imagePath.isEmpty()) {
            showToast("No image received. Please try again.")
            return@with
        }

        val item = UploadAttachmentItem(
            fileName       = "GPS_Photo_${System.currentTimeMillis()}.jpg",
            filePath       = imagePath,
            fileType       = "image/jpeg",
            attachmentType = AttachmentType.PHOTO.name
        )

        Glide.with(this@DealerCheckInUnnatiFragment)
            .load(File(imagePath))
            .placeholder(R.drawable.ic_camera)
            .error(R.drawable.ic_camera)
            .into(imgUpload)

        tvUploadPhoto.text = "GPS Photo Captured Successfully"
        selectedImageUri = Uri.fromFile(File(imagePath))
        Log.d("GPS_PHOTO", "Image Loaded : $imagePath")
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ViewModel observers
    // ─────────────────────────────────────────────────────────────────────────

    private fun observeDealerCategory() {
        dailyTourViewModel.dealerCategoryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DealerCategoryState.Loading      -> showLoader()
                is DealerCategoryState.Success      -> hideLoader()
                is DealerCategoryState.ApiError     -> { hideLoader(); showToast(state.message) }
                is DealerCategoryState.NetworkError -> { hideLoader(); showToast(state.message) }
                else -> {}
            }
        }
    }

    private fun observeDealerName() {
        dailyTourViewModel.dealerNameState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DealerNameState.Loading      -> showLoader()
                is DealerNameState.Success      -> hideLoader()
                is DealerNameState.ApiError     -> { hideLoader(); showToast(state.message) }
                is DealerNameState.NetworkError -> { hideLoader(); showToast(state.message) }
                else -> {}
            }
        }
    }

    private fun observeSaveDealerCheckIn() {
        dealerCheckInViewModel.dealerCheckInState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DealerCheckInState.Loading      -> showLoader()
                is DealerCheckInState.ApiError     -> { hideLoader(); showToast(state.message) }
                is DealerCheckInState.NetworkError -> { hideLoader(); showToast(state.message) }
                is DealerCheckInState.Idle         -> {}
                is DealerCheckInState.Success      -> {
                    hideLoader()
                    showToast(state.response.message)
                    parentFragmentManager.popBackStackImmediate()
                }
            }
        }
    }

    private fun observeCheckDealerInOutStatus() = with(binding){
        dealerCheckInViewModel.checkDealerInOutStatusState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CheckDealerInOutStatusState.Idle -> {}
                is CheckDealerInOutStatusState.Loading -> showLoader()

                is CheckDealerInOutStatusState.Success -> {
                    hideLoader()

                    val data = state.list.firstOrNull()

                    if (data != null) {
                        // Store IDs
                        selectedDealerCategoryID = data.dealerCategoryId ?: ""
                        selectedDealerID = data.dealerId ?: ""

                        // Auto fill UI
                        txtDealerCategory.setText(data.dealerCategory ?: "")
                        txtDealerName.setText(data.dealerName ?: "")
                        txtRemarks.setText(data.remarks ?: "")

                        // Disable editing if needed
                        txtDealerCategory.isEnabled = false
                        txtDealerName.isEnabled = false

                        val isInTimeToday = Constants.isSameAsToday(data.inTime)
                        val isOutTimeToday = Constants.isSameAsToday(data.outTime)

                        when {
                            // ✅ CHECK-IN (InTime today, OutTime empty)
                            isInTimeToday && data.outTime.isNullOrBlank() -> {
                                Log.d("Dealer", "✅ Checked IN today, waiting for checkout")
                                btnCheckIn.text = getString(R.string.label_check_out)
                                //txtViewCheckInTimeValue.text = getTodayDateFormatted(data.inTime)
                            }

                            // ✅ CHECK-OUT (InTime & OutTime today)
                            isInTimeToday && isOutTimeToday -> {
                                Log.d("Dealer", "✅ Checked OUT today")
                                btnCheckIn.text = getString(R.string.label_check_in)
                                // Optional reset
                                selectedDealerCategoryID = ""
                                selectedDealerID = ""

                                txtDealerCategory.setText("")
                                txtDealerName.setText("")
                                txtRemarks.setText("")

                                txtDealerCategory.isEnabled = true
                                txtDealerName.isEnabled = true
                                //txtViewCheckInTimeValue.text = getTodayDateFormatted(data.inTime)
                                //txtViewCheckOutTimeValue.text = getTodayDateFormatted(data.outTime)
                            }

                            else -> {
                                Log.d("Dealer", "❌ No active attendance today")
                                btnCheckIn.text = getString(R.string.label_check_in)
                                // Optional reset
                                selectedDealerCategoryID = ""
                                selectedDealerID = ""

                                txtDealerCategory.setText("")
                                txtDealerName.setText("")
                                txtRemarks.setText("")

                                txtDealerCategory.isEnabled = true
                                txtDealerName.isEnabled = true
                            }

                        }
                    }
                }


                is CheckDealerInOutStatusState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is CheckDealerInOutStatusState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    // ─────────────────────────────────────────────────────────────────────────
    //  UI helpers
    // ─────────────────────────────────────────────────────────────────────────

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