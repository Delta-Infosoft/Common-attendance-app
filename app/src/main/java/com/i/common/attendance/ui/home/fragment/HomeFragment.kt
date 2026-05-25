package com.i.common.attendance.ui.home.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentHomeBinding
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.adapter.LastFiveDayRecordsAdapter
import com.i.common.attendance.ui.home.pjc.fragment.SelectPjcEventBottomSheetFragment
import com.i.common.attendance.ui.home.pjc.viewmodel.CalendarViewModel
import com.i.common.attendance.ui.home.pjc.viewmodel.PjcEventState
import com.i.common.attendance.ui.home.viewmodel.ApiState
import com.i.common.attendance.ui.home.viewmodel.GetCheckInOutState
import com.i.common.attendance.ui.home.viewmodel.GetRecordsState
import com.i.common.attendance.ui.home.viewmodel.HomeViewModel
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTodayDateFormatted
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: com.i.common.attendance.ui.viewmodel.HomeViewModel by viewModels()
    private val viewModel: HomeViewModel by viewModels()
    private val calViewmodel: CalendarViewModel by viewModels()

    @Inject lateinit var sharedPref: EncryptedPrefHelper
    @Inject lateinit var fusedClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var pendingCheckIn = false
    private var pendingCheckOut = false
    private val lastFiveDayAdapter by lazy { LastFiveDayRecordsAdapter(sharedPrefHelper = sharedPref) }

    // Pre-fetched GPS fix: we start location fetch BEFORE the user opens SelectStatusFragment
    // so GPS is already warm by the time they submit.
    private var earlyLocationLat: Double? = null
    private var earlyLocationLng: Double? = null
    private var earlyLocationCallback: LocationCallback? = null

    private var isAwaitingLocation = false

    /* ---------------------------------- */
    /* Permission + Location Handling     */
    /* ---------------------------------- */

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                checkLocationAndStartTracking()
            } else {
                showToast("All permissions are required to start tracking")
            }
        }
    private fun hasRequiredPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        return fineLocation && backgroundLocation && notification
    }
    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun checkLocationAndStartTracking() {
        if (isLocationEnabled()) {
            //homeViewModel.startTracking()
            //showToast("Location tracking started")
        } else {
            showEnableLocationDialog()
        }
    }
    private fun showEnableLocationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Required")
            .setMessage("Please turn on location to track attendance accurately.")
            .setCancelable(false)
            .setPositiveButton("Turn On") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { _, _ ->
                showToast("Location is mandatory for attendance")
            }
            .show()
    }
    private fun checkLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            val fine = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarse = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            fine == PackageManager.PERMISSION_GRANTED ||
                    coarse == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun checkBatteryOptimization(): Boolean {
        val pm = requireContext().getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.isIgnoringBatteryOptimizations(requireContext().packageName)
        } else {
            true
        }
    }
    private fun showPermissionError() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage(
                "This app requires:\n\n" +
                        "1. 'Allow all the time' location permission\n" +
                        "2. 'No restrictions' battery settings\n\n" +
                        "Enable in settings and relaunch."
            )
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Exit") { _, _ ->
                requireActivity().finishAffinity()
            }
            .setCancelable(false)
            .show()
    }

    /* ---------------------------------- */
    /* LOCATION (REAL + SAFE)             */
    /* ---------------------------------- */

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(status: String, remark: String) {
        Log.e("CHECKIN_FLOW", "6️⃣ Fresh GPS request started : ${System.currentTimeMillis()}")
        if (!hasRequiredPermissions()) {
            Log.e("REAL_LOCATION", "❌ Permission not granted")
            return
        }

        if (!isLocationEnabled()) {
            Log.e("REAL_LOCATION", "❌ GPS is OFF")
            return
        }

        // Remove any previous callback to avoid duplicates
        //locationCallback?.let { fusedClient.removeLocationUpdates(it) }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            500L
        )
            .setWaitForAccurateLocation(false)  // ✅ Don't hold — deliver first available fix
            .setMinUpdateIntervalMillis(500L)
            .setMaxUpdates(3)                   // ✅ Try up to 3 fixes, stop once we accept one
            .setMaxUpdateDelayMillis(3000L)     // ✅ Hard deadline: force a result within 3 sec
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {

                val location = result.lastLocation ?: return

                // 🚫 Block cached locations (IMPORTANT)
                if (location.elapsedRealtimeNanos == 0L) {
                    return
                }

                // 🚫 Fake GPS detection
                val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    location.isMock
                } else {
                    location.isFromMockProvider
                }

                if (isMock) {
                    Log.e("REAL_LOCATION", "🚫 Fake GPS detected")
                    fusedClient.removeLocationUpdates(this)
                    return
                }

                // 🚫 Accuracy threshold
                if (location.accuracy > 50f) {
                    Log.e("REAL_LOCATION", "⚠️ Waiting for better accuracy: ${location.accuracy}")
                    return
                }

                val lat = location.latitude
                val lng = location.longitude

                // ✅ REAL FRESH GPS
                Log.e("REAL_LOCATION", "🔥 REAL GPS → lat=$lat, lng=$lng, acc=${location.accuracy}")

                if (pendingCheckIn) {
                    isAwaitingLocation = false  // ✅ GPS done, API takes over the loader
                    callAttendanceCheckInApi(lat = lat, long = lng, status = status, remark = remark)
                    pendingCheckIn = false
                }

                if (pendingCheckOut) {
                    isAwaitingLocation = false  // ✅ GPS done, API takes over the loader
                    callAttendanceCheckOutApi(lat = lat, long = lng, status = status, remark = remark)
                    pendingCheckOut = false
                    homeViewModel.stopTracking()
                }

                fusedClient.removeLocationUpdates(this)
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    /**
     * Called immediately when the Check In button is pressed — BEFORE opening
     * SelectStatusFragment. This gives GPS 3-5 seconds to warm up while the user
     * is picking a status, so by the time they submit we already have a fresh fix.
     */
    @SuppressLint("MissingPermission")
    private fun prefetchLocation() {
        Log.e("CHECKIN_FLOW", "2️⃣ Prefetch started : ${System.currentTimeMillis()}")

        if (!hasRequiredPermissions() || !isLocationEnabled()) return

        // Clear any stale cached fix from a previous session
        earlyLocationLat = null
        earlyLocationLng = null

        // Remove any previous prefetch callback
        earlyLocationCallback?.let { fusedClient.removeLocationUpdates(it) }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500L)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500L)
            .setMaxUpdates(5)
            .setMaxUpdateDelayMillis(5000L)
            .build()

        earlyLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                if (location.elapsedRealtimeNanos == 0L) return

                val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    location.isMock
                } else {
                    @Suppress("DEPRECATION")
                    location.isFromMockProvider
                }
                if (isMock) return

                // Accept FIRST LIVE LOCATION immediately
                earlyLocationLat = location.latitude
                earlyLocationLng = location.longitude
                Log.e(
                    "CHECKIN_FLOW",
                    "3️⃣ Prefetch GPS received : ${System.currentTimeMillis()} acc=${location.accuracy}"
                )
                Log.e("PREFETCH_LOC", "✅ Fast pre-fetched GPS → lat=${location.latitude}, lng=${location.longitude}, acc=${location.accuracy}")

                fusedClient.removeLocationUpdates(this)
                earlyLocationCallback = null

                // Accept the first fix that is accurate enough
               /* if (location.accuracy <= 50f) {
                    earlyLocationLat = location.latitude
                    earlyLocationLng = location.longitude
                    Log.e("PREFETCH_LOC", "✅ Pre-fetched GPS → lat=${location.latitude}, lng=${location.longitude}, acc=${location.accuracy}")
                    fusedClient.removeLocationUpdates(this)
                    earlyLocationCallback = null
                }*/
            }
        }

        fusedClient.requestLocationUpdates(request, earlyLocationCallback!!, Looper.getMainLooper())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        earlyLocationCallback?.let { fusedClient.removeLocationUpdates(it) }
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
    }

    /* ---------------------------------- */
    /* Fragment Lifecycle                 */
    /* ---------------------------------- */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        manageLog()
        setUpInitData()
        manageOnClickListeners()
        setUpRecyclerView()
        printInitData()
        observeAttendanceCheckInOutApi()
        listenForStatusResult()
        observePjcEventApi()
    }
    private fun observeAttendanceCheckInOutApi() = with(binding){
        viewModel.attendanceCheckInOutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ApiState.Loading -> showLoader()
                is ApiState.Success -> {
                    hideLoader()
                    callCheckInOutStatus()
                }
                is ApiState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (hasRequiredPermissions()) {
            if (isLocationEnabled()) {
                Log.d("HomeFragment", "Permissions & location OK")
            }
        }
    }
    private fun callAttendanceCheckInApi(lat: Double,long: Double,status: String, remark: String) {
        Log.e("CHECKIN_FLOW", "8️⃣ Attendance API START : ${System.currentTimeMillis()}")
        viewModel.getAttendanceInOutAPI(
            latitude = lat,
            longitude = long,
            inTime = Constants.getCurrentTimestamp("dd-MMM-yyyy hh:mm:ss"),
            outTime = "",
            status = status,
            remark = remark
        )
    }
    private fun callAttendanceCheckOutApi(lat: Double,long: Double,status: String, remark: String) {
        viewModel.getAttendanceInOutAPI(
            latitude = lat,
            longitude = long,
            inTime = "",
            outTime = Constants.getCurrentTimestamp("dd-MMM-yyyy hh:mm:ss"),
            status = status,
            remark = remark
        )
    }

    private fun observePjcEventApi() {
        calViewmodel.pjcEventStateFollowUp.observe(viewLifecycleOwner) { state ->
            when (state) {

                is PjcEventState.Loading -> {
                    showLoader()
                    Log.e("PjcEventState.Loading","PjcEventState.Loading")
                }

                is PjcEventState.Success -> {
                    hideLoader()
                    Log.e("PjcEventState.Success","PjcEventState.Success")
                    Log.e("event",state.events.toString())
                    SelectPjcEventBottomSheetFragment.Companion.newInstance(state.events).show(parentFragmentManager,"SelectPjcEventBottomSheet")
                }

                is PjcEventState.Error -> {
                    Log.e("PjcEventState.Error","PjcEventState.Error")
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }
    /* ---------------------------------- */
    /* UI & Business Logic                */
    /* ---------------------------------- */
    private fun printInitData() = with(binding){
        Log.e("UserData", sharedPref.getUser().toString())

    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.label_today_s_attendance))
            manageBackButtonClick(true)
            manageDrawerLock(BuildConfig.FLAVOR != "duke")
            setDrawerEnabled(BuildConfig.FLAVOR != "duke")
            manageInfo(BuildConfig.FLAVOR != "duke")
            updateDrawerHeader(
                userName = sharedPref.getUser()?.UsersName.toString(),
                email = "test@gmail.com"
            )

            /*manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_project_journey_cycle))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)*/
        }
    }
    private fun manageLog() {
        Log.e("User",sharedPref.getUser().toString())
        Log.e("getDeviceName", Constants.getDeviceName())
        Log.e("getAndroidVersion", Constants.getAndroidVersion())
        Log.e("isGpsEnabled", Constants.isGpsEnabled(requireContext()).toString())
        Log.e("getBatteryLevel", Constants.getBatteryLevel(requireContext()).toString())
        Log.e("isNetworkAvailable", Constants.isNetworkAvailable(requireContext()).toString())
        Log.e("getAppVersion", Constants.getAppVersion(requireContext()))
    }
    private fun setUpInitData() = with(binding) {
        txtViewTodayDate.text = Constants.getCurrentFormattedDate()
        txtViewEmpName.text = sharedPref.getUser()?.UsersName ?: "Employee"

        if (!hasRequiredPermissions()) {
            requestRequiredPermissions()
        }

        if (!isLocationEnabled()) {
            showEnableLocationDialog()
        }
    }
    private fun listenForStatusResult() {
        parentFragmentManager.setFragmentResultListener(
            FragmentResultKeys.STATUS_RESULT,
            viewLifecycleOwner
        ) { _, bundle ->

            val status = bundle.getString(FragmentResultKeys.KEY_STATUS).orEmpty()
            val remark = bundle.getString(FragmentResultKeys.KEY_REMARK).orEmpty()

            if(status.isEmpty()){
                showToast("Please select the status")
                return@setFragmentResultListener
            }

            Log.e("RESULT", "Status=$status, Remark=$remark")

            // ✅ Post to next frame so the loader shows AFTER HomeFragment is fully visible
            binding.root.post {
                handleStatusResult(status, remark.ifEmpty { "" })
            }
        }
    }
    private fun handleStatusResult(status: String, remark: String) = with(binding){
        Log.e("CHECKIN_FLOW", "4️⃣ Returned from status screen : ${System.currentTimeMillis()}")
        if (!hasRequiredPermissions()) {
            requestRequiredPermissions()
            return
        }

        if (!isLocationEnabled()) {
            showEnableLocationDialog()
            return
        }

        showLoader()

        val cachedLat = earlyLocationLat
        val cachedLng = earlyLocationLng

        if (cachedLat != null && cachedLng != null) {
            Log.e("CHECKIN_FLOW", "5️⃣ Using PREFETCH location")
            // ✅ GPS was already warmed up — call API instantly, no wait
            Log.e("REAL_LOCATION", "⚡ Using pre-fetched GPS → lat=$cachedLat, lng=$cachedLng")

            isAwaitingLocation = false  // ✅ cache hit, no wait needed
            earlyLocationLat = null
            earlyLocationLng = null
            earlyLocationCallback?.let { fusedClient.removeLocationUpdates(it) }
            earlyLocationCallback = null

            if (txtViewCheckIn.text == getString(R.string.label_check_in)) {
                callAttendanceCheckInApi(lat = cachedLat, long = cachedLng, status = status, remark = remark)
            } else {
                callAttendanceCheckOutApi(lat = cachedLat, long = cachedLng, status = status, remark = remark)
                homeViewModel.stopTracking()
            }
        } else {
            isAwaitingLocation = true  // 🔒 GPS fetching — block hideLoader
            Log.e("CHECKIN_FLOW", "5️⃣ Prefetch FAILED → requesting fresh GPS")
            // ⚠️ Fallback: GPS wasn't ready yet — request fresh fix now
            if (txtViewCheckIn.text == getString(R.string.label_check_in)) {
                pendingCheckIn = true
                pendingCheckOut = false
                requestFreshLocation(status, remark)
            } else {
                pendingCheckOut = true
                pendingCheckIn = false
                requestFreshLocation(status, remark)
            }
        }

        /*if (txtViewCheckIn.text == getString(R.string.label_check_in)) {
            pendingCheckIn = true
            pendingCheckOut = false
            requestFreshLocation(status,remark)
        } else {
            pendingCheckOut = true
            pendingCheckIn = false
            requestFreshLocation(status,remark)
        }*/
    }
    private fun manageOnClickListeners() = with(binding) {
        constLayoutCheckInOut.setSafeOnClickListener {
            Log.e("CHECKIN_FLOW", "1️⃣ CheckIn button clicked : ${System.currentTimeMillis()}")
            if(txtViewCheckIn.text.equals(getString(R.string.label_check_in))){
                // ── Step 1: Check location permission ──────────────────
                if (!hasLocationPermissionForCheckin()) {
                    showLocationPermissionDialog()
                    return@setSafeOnClickListener
                }

                // ── Step 2: Check GPS is actually ON ───────────────────
                if (!isLocationEnabled()) {
                    showEnableLocationDialog()
                    return@setSafeOnClickListener
                }

                // ── Step 3: Check battery optimization ─────────────────
                if (!checkBatteryOptimization()) {
                    showBatteryOptimizationDialog()
                    return@setSafeOnClickListener
                }

                // ⚡ Start GPS warm-up NOW — user will spend ~2-3 sec on status screen
                prefetchLocation()
                loadStatusFragment()
            }else{
                handleStatusResult("","")
            }
        }

        txtViewWithInRange.setSafeOnClickListener {
            calViewmodel.getFollowUps(mobileNo = sharedPref.getUser()?.MobileNo?:"")
        }
    }
    private fun loadStatusFragment(){
        loadFragment(fragment = SelectStatusFragment(), isAdd = false, isAddBackStack = true)
    }
    private fun setUpRecyclerView() = with(binding) {
        recyclerViewRecords.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = lastFiveDayAdapter
        }
        callCheckInOutStatus()
        observeCheckInOutStatus()
        observeAttendanceRecords()
    }
    private fun callCheckInOutStatus(){
        val user = sharedPref.getUser()
        viewModel.getCheckInCheckOutStatusAPI(user?.MobileNo ?: "")
        viewModel.getRecords(
            mobileNo = user?.MobileNo ?: "",
            month = "Dec 2025"
            //month = Constants.getCurrentTimestamp("MMM yyyy")
        )
    }
    private fun observeCheckInOutStatus() = with(binding){
        viewModel.checkInOutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetCheckInOutState.Loading -> showLoader()
                is GetCheckInOutState.Success -> {
                    hideLoader()

                    val record = state.records.firstOrNull()

                    if (record == null) {
                        showToast("No attendance record")
                        return@observe
                    }

                    txtViewLastAction.text = getString(R.string.place_holder_last_action,"${record.lastUpdatedOn}")

                    val isInTimeToday = Constants.isSameAsToday(record.inTime)
                    val isOutTimeToday = Constants.isSameAsToday(record.outTime)

                    when {
                        // ✅ CHECK-IN (InTime today, OutTime empty)
                        isInTimeToday && record.outTime.isNullOrBlank() -> {
                            Log.d("ATTENDANCE", "✅ Checked IN today, waiting for checkout")
                            txtViewCheckIn.text = getString(R.string.label_check_out)
                            txtViewCheckInTimeValue.text = getTodayDateFormatted(record.inTime)
                            homeViewModel.startTracking()
                        }

                        // ✅ CHECK-OUT (InTime & OutTime today)
                        isInTimeToday && isOutTimeToday -> {
                            Log.d("ATTENDANCE", "✅ Checked OUT today")
                            txtViewCheckIn.text = getString(R.string.label_check_in)
                            txtViewCheckInTimeValue.text = getTodayDateFormatted(record.inTime)
                            txtViewCheckOutTimeValue.text = getTodayDateFormatted(record.outTime)
                            homeViewModel.stopTracking()
                        }
                        else -> {
                            Log.d("ATTENDANCE", "❌ No active attendance today")
                            txtViewCheckIn.text = getString(R.string.label_check_in)
                            homeViewModel.stopTracking()
                        }
                    }
                }
                is GetCheckInOutState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetCheckInOutState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }
    private fun observeAttendanceRecords(){
        viewModel.recordsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetRecordsState.Loading -> showLoader()
                is GetRecordsState.Success -> {
                    hideLoader(); lastFiveDayAdapter.submitList(state.records)
                }
                is GetRecordsState.Empty -> {
                    hideLoader(); showToast(state.message)
                }
                is GetRecordsState.Error -> {
                    hideLoader(); showToast(state.message)
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
        binding.progressBarHome.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        if (isAwaitingLocation) return
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarHome.visibility = View.GONE
    }

    // Checks only fine + background location (for check-in button)
    private fun hasLocationPermissionForCheckin(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        return fine && background
    }

    // Shows dialog explaining location permission is needed, opens app settings
    private fun showLocationPermissionDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Required")
            .setMessage(
                "Location permission is required to check in.\n\n" +
                        "Please go to Settings and enable:\n" +
                        "Location → Allow all the time"
            )
            .setCancelable(false)
            .setPositiveButton("Open Settings") { _, _ ->
                // On Android 11+ background location must be granted from settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${requireContext().packageName}")
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Shows dialog explaining battery optimization needs to be disabled
    private fun showBatteryOptimizationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Battery Restriction Detected")
            .setMessage(
                "Your phone is restricting background activity.\n\n" +
                        "Please go to Settings and set:\n" +
                        "Battery → No restrictions / Unrestricted\n\n" +
                        "Without this, location tracking will stop in background."
            )
            .setCancelable(false)
            .setPositiveButton("Open Settings") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        // Direct to battery optimization page for this app
                        val intent = Intent(
                            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        ).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback to app details settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                        }
                        startActivity(intent)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
