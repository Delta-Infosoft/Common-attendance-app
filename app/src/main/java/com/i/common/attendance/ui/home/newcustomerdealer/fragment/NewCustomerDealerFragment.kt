package com.i.common.attendance.ui.home.newcustomerdealer.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentNewCustomerDealerBinding
import com.i.common.attendance.network.request.InsertVisitRequest
import com.i.common.attendance.network.response.ViewPortFolioModel
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.tourvoucher.data.CommonSelect
import com.i.common.attendance.ui.home.tourvoucher.data.MediaPickType
import com.i.common.attendance.ui.home.tourvoucher.fragment.CommonSelectBottomSheetFragment
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.ui.home.newcustomerdealer.viewmodel.InsertVisitUiState
import com.i.common.attendance.ui.home.newcustomerdealer.viewmodel.PortfolioViewModel
import com.i.common.attendance.ui.home.newcustomerdealer.viewmodel.UpdateVisitUiState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class NewCustomerDealerFragment : BaseFragment() {

    private lateinit var binding : FragmentNewCustomerDealerBinding
    private val viewModel: PortfolioViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper

    private var editItem: ViewPortFolioModel? = null

    companion object {
        private const val ARG_PORTFOLIO_ITEM = "arg_portfolio_item"

        // ✅ Call this from fragment when navigating for EDIT
        fun newInstance(item: ViewPortFolioModel?=null): NewCustomerDealerFragment {
            return NewCustomerDealerFragment().apply {
                item?.let {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PORTFOLIO_ITEM, item)
                    }
                }
            }
        }
    }

    private var selectedImageUri: Uri? = null
    private lateinit var cameraImageUri: Uri

    // -------------------- LOCATION --------------------
    // Holds the fetched lat/long. Null means location not yet obtained.
    private var currentLat: String? = null
    private var currentLong: String? = null
    private var portfolioId: String? = null

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                // Permission just granted → check if GPS is on, then fetch
                checkGpsAndFetchLocation()
            } else {
                // User denied — show rationale dialog
                showLocationPermissionRationaleDialog()
            }
        }


    // -------------------- CAMERA --------------------
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri = cameraImageUri
                binding.imgUpload.setImageURI(selectedImageUri)
            }
        }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) openCameraInternal()
        else showToast("Camera permission denied")
    }

    // -------------------- GALLERY --------------------
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                selectedImageUri = it
                binding.imgUpload.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewCustomerDealerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        observePortfolio()
        moveOnClickListeners()
        requestLocationPermission()

        editItem = arguments?.getParcelable(ARG_PORTFOLIO_ITEM)
        editItem?.let { prefillForm(it) }
    }

    private fun prefillForm(item: ViewPortFolioModel) = with(binding) {
        item.PortfolioId?.let {
            portfolioId = it
        }
        btnSubmit.text = getString(R.string.btn_update)

        txtCompany.setText(item.CompanyName)
        txtCity.setText(item.City)
        txtContactPersonName.setText(item.ContactPersonName)
        txtContactPersonMobileNo.setText(item.ContactPersonMobileNo)
        txtContactPersonEmail.setText(item.ContactPersonEmailId)
        txtRemark.setText(item.Remarks)

        if (!item.FilePathShow.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(item.FilePathShow)
                .into(imgUpload)
        }
    }

    // =========================================================
    // LOCATION FLOW
    // =========================================================
    /** Step 1 — ask permission */
    private fun requestLocationPermission() {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

        val alreadyGranted = ContextCompat.checkSelfPermission(requireContext(), fine) ==
                PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            // Already have it → go straight to GPS check
            checkGpsAndFetchLocation()
        } else {
            locationPermissionLauncher.launch(arrayOf(fine, coarse))
        }
    }
    /** Step 2 — make sure GPS (device location) is enabled */
    private fun checkGpsAndFetchLocation() {
        val locationManager = requireContext().getSystemService<LocationManager>()
        val isGpsOn = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
                || locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        if (isGpsOn) {
            fetchCurrentLocation()
        } else {
            showGpsDisabledDialog()
        }
    }
    /** Step 3 — get the actual coordinates */
    @SuppressLint("MissingPermission")   // permission is checked before this call
    private fun fetchCurrentLocation() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val cancellationToken = CancellationTokenSource()

        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude.toString()
                currentLong = location.longitude.toString()
            } else {
                // Fallback to last known location
                fusedClient.lastLocation.addOnSuccessListener { last ->
                    currentLat = last?.latitude?.toString() ?: "0.0"
                    currentLong = last?.longitude?.toString() ?: "0.0"
                }
            }
        }.addOnFailureListener {
            showToast("Failed to get location. Please try again.")
        }
    }
    // =========================================================
    // DIALOGS
    // =========================================================
    /** Shown when user denies location permission */
    private fun showLocationPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Required")
            .setMessage("This screen needs your location to record the visit coordinates. Please grant location permission to continue.")
            .setCancelable(false)
            .setPositiveButton("Grant Permission") { dialog, _ ->
                dialog.dismiss()
                // Re-launch permission request
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton("Open Settings") { dialog, _ ->
                dialog.dismiss()
                // User permanently denied → send to App Settings
                openAppSettings()
            }
            .show()
    }
    /** Shown when GPS is switched off */
    private fun showGpsDisabledDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("GPS Disabled")
            .setMessage("Your GPS / location service is turned off. Please enable it to record visit location.")
            .setCancelable(false)
            .setPositiveButton("Enable GPS") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                showToast("Location is required to submit the form.")
            }
            .show()
    }
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }
    // =========================================================
    // CLICK LISTENERS
    // =========================================================
    private fun moveOnClickListeners()= with(binding){
        constLayoutCamGallery.setSafeOnClickListener {
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
        btnSubmit.setSafeOnClickListener {
            Constants.hideKeyboard(it)
            // ✅ Block submit if location not yet fetched
            if (currentLat == null || currentLong == null) {
                showToast("Fetching your location, please wait…")
                requestLocationPermission()   // re-trigger the full permission → GPS → fetch flow
                return@setSafeOnClickListener
            }

            if (validatePortfolioForm()) {
                val request = InsertVisitRequest(
                    portfolioId = portfolioId,
                    companyName = txtCompany.text.toString().trim(),
                    city = txtCity.text.toString().trim(),
                    contactPersonName = txtContactPersonName.text.toString().trim(),
                    contactPersonMobileNo = txtContactPersonMobileNo.text.toString().trim(),
                    contactPersonEmailId = txtContactPersonEmail.text.toString().trim(),
                    lat = currentLat!!,        // ✅ real lat
                    long = currentLong!!,      // ✅ real long
                    insertedByUserId = sharedPref.getUser()?.AutoId ?: "",
                    remarks = txtRemark.text.toString().trim(),
                    imageUri = selectedImageUri   // ✅ use selected image
                )

                if (editItem != null) {
                    viewModel.updateVisit(request)   // 🔄 update
                } else {
                    viewModel.insertVisit(request)   // ➕ insert
                }
            }
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.label_new_customer_dealer))
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
        cameraImageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        cameraLauncher.launch(cameraImageUri)
    }
    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private fun validatePortfolioForm(): Boolean = with(binding) {

        val company = txtCompany.text?.toString()?.trim().orEmpty()
        val city = txtCity.text?.toString()?.trim().orEmpty()
        val personName = txtContactPersonName.text?.toString()?.trim().orEmpty()
        val mobile = txtContactPersonMobileNo.text?.toString()?.trim().orEmpty()
        val email = txtContactPersonEmail.text?.toString()?.trim().orEmpty()
        val remark = txtRemark.text?.toString()?.trim().orEmpty()

        when {

            company.isEmpty() -> {
                showToast("Please enter company name")
                txtCompany.requestFocus()
                return false
            }

            city.isEmpty() -> {
                showToast("Please enter city")
                txtCity.requestFocus()
                return false
            }

            personName.isEmpty() -> {
                showToast("Please enter contact person name")
                txtContactPersonName.requestFocus()
                return false
            }

            mobile.isEmpty() -> {
                showToast("Please enter mobile number")
                txtContactPersonMobileNo.requestFocus()
                return false
            }

            mobile.length != 10 || !mobile.all { it.isDigit() } -> {
                showToast("Enter valid 10 digit mobile number")
                txtContactPersonMobileNo.requestFocus()
                return false
            }

            email.isEmpty() -> {
                showToast("Please enter email address")
                txtContactPersonEmail.requestFocus()
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter valid email address")
                txtContactPersonEmail.requestFocus()
                return false
            }

            remark.isEmpty() -> {
                showToast("Please enter remarks")
                txtRemark.requestFocus()
                return false
            }

            selectedImageUri == null -> {
                showToast("Please select Picture")
                return false
            }
        }

        true
    }
    private fun observePortfolio() = with(binding){
        viewModel.insertVisitState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is InsertVisitUiState.Loading -> {
                    showLoader()
                }

                is InsertVisitUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                   parentFragmentManager.popBackStackImmediate()
                }

                is InsertVisitUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is InsertVisitUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> {}
            }
        }
        // ✅ Update observer — new
        viewModel.updateVisitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UpdateVisitUiState.Loading -> showLoader()
                is UpdateVisitUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }
                is UpdateVisitUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is UpdateVisitUiState.NetworkError -> {
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



}