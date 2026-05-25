package com.i.common.attendance.ui.home.dealercheckin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.i.common.attendance.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Full-screen GPS camera fragment — Kotlin port of GpsPhotoActivity.java
 *
 * Flow  :  Permission check → CameraX live preview → Capture button →
 *          GPS overlay stamped on bitmap (background thread) →
 *          Preview with Glide → "Save" returns path via FragmentResult.
 *          "Retake" restores the live viewfinder.
 *
 * ⚠️ Location guard: The capture button is BLOCKED until a real GPS fix
 *    arrives. Tapping while "Fetching..." shows a toast and does NOT
 *    proceed — so the stamped image always has valid coordinates.
 */
class GpsPhotoFragment : Fragment() {

    // ─────────────────────────────────────────────────────────────────────────
    //  Companion / factory
    // ─────────────────────────────────────────────────────────────────────────
    companion object {
        private const val TAG = "GpsPhotoFragment"
        private const val ARG_SITE_NAME = "site_name"
        private const val GPS_DIR = "GPSCamera"
        private const val UPDATE_INTERVAL_MS = 8_000L
        private const val FASTEST_INTERVAL_MS = 4_000L

        // FragmentResult keys — used by DealerCheckInUnnatiFragment to receive the result
        const val GPS_RESULT_KEY = "gps_photo_result"
        const val KEY_IMAGE_PATH = "imagePath"
        const val KEY_LAT        = "latitude"
        const val KEY_LON        = "longitude"

        fun newInstance(siteName: String = ""): GpsPhotoFragment =
            GpsPhotoFragment().apply {
                arguments = Bundle().apply { putString(ARG_SITE_NAME, siteName) }
            }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Views (inflated manually — no ViewBinding generated for this fragment)
    // ─────────────────────────────────────────────────────────────────────────
    private lateinit var viewFinder: PreviewView
    private lateinit var ivCapturedPhoto: ImageView
    private lateinit var tvLocation: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvSiteName: TextView
    private lateinit var llCapturePhoto: LinearLayout
    private lateinit var llRetakePhoto: LinearLayout
    private lateinit var tvRetake: TextView
    private lateinit var flContainer: FrameLayout
    private lateinit var layoutGpsDetails: LinearLayout
    private lateinit var progressBar: ProgressBar

    // ─────────────────────────────────────────────────────────────────────────
    //  CameraX
    // ─────────────────────────────────────────────────────────────────────────
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    // ─────────────────────────────────────────────────────────────────────────
    //  Location (FusedLocationProvider)
    // ─────────────────────────────────────────────────────────────────────────
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var isRequestingLocationUpdates = false

    private var finalLat = "0.0"
    private var finalLon = "0.0"
    private var addressLine = ""

    /**
     * TRUE only after the first real GPS fix is received.
     *
     * [takePhoto] checks this flag and blocks the capture with a toast if
     * the location is still being fetched. This prevents "Location: Fetching"
     * from being stamped onto the saved image.
     */
    private var isLocationReady = false

    // ─────────────────────────────────────────────────────────────────────────
    //  State
    // ─────────────────────────────────────────────────────────────────────────
    private var savedImagePath = ""
    private var siteName = ""
    private val coroutineJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + coroutineJob)

    // ─────────────────────────────────────────────────────────────────────────
    //  Permission launcher
    // ─────────────────────────────────────────────────────────────────────────
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val cameraOk = results[Manifest.permission.CAMERA] == true
            val locationOk =
                results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        results[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            when {
                cameraOk && locationOk -> {
                    startCamera()
                    startLocationUpdates()
                }
                !cameraOk ->
                    toast("Camera permission is required to use this feature.")
                else ->
                    toast("Location permission is required to stamp GPS coordinates.")
            }
        }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        siteName = arguments?.getString(ARG_SITE_NAME) ?: ""
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_gps_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setCurrentDateAndTime()
        tvSiteName.text = "Site : $siteName"

        // Show "Fetching..." immediately so the user knows GPS is warming up
        setLocationFetching()

        checkPermissionsAndStart()

        llCapturePhoto.setOnClickListener { takePhoto() }
        llRetakePhoto.setOnClickListener { onRetakeOrSave() }
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineJob.cancel()
        cameraProvider?.unbindAll()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  View binding (manual)
    // ─────────────────────────────────────────────────────────────────────────

    private fun bindViews(root: View) {
        viewFinder       = root.findViewById(R.id.view_finder)
        ivCapturedPhoto  = root.findViewById(R.id.ivCapturedPhoto)
        tvLocation       = root.findViewById(R.id.tvLocation)
        tvDateTime       = root.findViewById(R.id.tvDateTime)
        tvSiteName       = root.findViewById(R.id.tvSiteName)
        llCapturePhoto   = root.findViewById(R.id.llCapturePhoto)
        llRetakePhoto    = root.findViewById(R.id.llRetakePhoto)
        tvRetake         = root.findViewById(R.id.retakeText)
        flContainer      = root.findViewById(R.id.flContainer)
        layoutGpsDetails = root.findViewById(R.id.layoutGpsDetails)
        progressBar      = root.findViewById(R.id.progressBar)
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Permission check
    // ─────────────────────────────────────────────────────────────────────────

    private fun allPermissionsGranted(): Boolean {
        val ctx = context ?: return false
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
    }

    private fun checkPermissionsAndStart() {
        if (allPermissionsGranted()) {
            startCamera()
            startLocationUpdates()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CameraX
    // ─────────────────────────────────────────────────────────────────────────

    private fun startCamera() {
        val ctx = context ?: return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                showViewFinder()

            } catch (e: Exception) {
                Log.e(TAG, "startCamera failed: ${e.message}", e)
                toast("Camera could not be started. Please try again.")
            }
        }, ContextCompat.getMainExecutor(ctx))
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Take photo — guarded by isLocationReady
    // ─────────────────────────────────────────────────────────────────────────

    private fun takePhoto() {
        // ── Location guard ────────────────────────────────────────────────────
        // Block capture if GPS fix hasn't arrived yet.
        // This ensures the stamped image never shows "Location: Fetching".
        if (!isLocationReady) {
            toast("Please wait, fetching your GPS location…")
            return
        }
        // ─────────────────────────────────────────────────────────────────────

        val cap = imageCapture ?: run {
            toast("Camera not ready, please wait.")
            return
        }

        val ctx = context ?: return

        val photoFile = File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_DCIM),
            "${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        showProgress(true)

        cap.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(ctx),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val rawBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    if (rawBitmap == null) {
                        showProgress(false)
                        toast("Failed to read captured image.")
                        return
                    }
                    previewCapturedImage(photoFile, rawBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    showProgress(false)
                    Log.e(TAG, "Capture failed: ${exception.message}", exception)
                    toast("Capture failed: ${exception.message}")
                }
            }
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Preview
    // ─────────────────────────────────────────────────────────────────────────

    private fun previewCapturedImage(file: File, bitmap: Bitmap) {
        llCapturePhoto.visibility = View.GONE
        llRetakePhoto.visibility  = View.GONE
        ivCapturedPhoto.visibility = View.VISIBLE
        viewFinder.visibility     = View.GONE

        Glide.with(this)
            .asBitmap()
            .load(file)
            .error(android.R.mipmap.sym_def_app_icon)
            .fitCenter()
            .dontTransform()
            .dontAnimate()
            .listener(object : com.bumptech.glide.request.RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: com.bumptech.glide.load.engine.GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    showProgress(false)
                    showSaveButton()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Bitmap>,
                    dataSource: com.bumptech.glide.load.DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    view?.postDelayed({
                        if (isAdded) {
                            ioScope.launch {
                                val stampedPath = stampGpsAndSave(resource)
                                withContext(Dispatchers.Main) {
                                    if (isAdded) {
                                        savedImagePath = stampedPath
                                        showProgress(false)
                                        showSaveButton()
                                    }
                                }
                            }
                        }
                    }, 500)
                    return false
                }
            })
            .into(ivCapturedPhoto)
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Retake / Save
    // ─────────────────────────────────────────────────────────────────────────

    private fun onRetakeOrSave() {
        val label = tvRetake.text.toString()

        if (label.equals("Retake", ignoreCase = true)) {
            ivCapturedPhoto.visibility = View.GONE
            llRetakePhoto.visibility   = View.GONE
            showViewFinder()
            savedImagePath = ""
        } else {
            if (savedImagePath.isEmpty()) {
                toast("Image not ready yet, please wait.")
                return
            }
            val bundle = Bundle().apply {
                putString(KEY_IMAGE_PATH, savedImagePath)
                putString(KEY_LAT, finalLat)
                putString(KEY_LON, finalLon)
            }
            parentFragmentManager.setFragmentResult(GPS_RESULT_KEY, bundle)
            parentFragmentManager.popBackStackImmediate()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GPS overlay + save
    // ─────────────────────────────────────────────────────────────────────────

    private fun stampGpsAndSave(sourceBitmap: Bitmap): String {
        return try {
            val stamped = drawGpsOverlay(sourceBitmap)
            val outFile = getOutputMediaFile()
            FileOutputStream(outFile).use { fos ->
                stamped.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            }
            if (stamped != sourceBitmap && !stamped.isRecycled) stamped.recycle()
            outFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "stampGpsAndSave error: ${e.message}", e)
            try {
                val outFile = getOutputMediaFile()
                FileOutputStream(outFile).use { fos ->
                    sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                outFile.absolutePath
            } catch (ex: Exception) {
                Log.e(TAG, "Fallback save also failed: ${ex.message}", ex)
                ""
            }
        }
    }

    private fun drawGpsOverlay(src: Bitmap): Bitmap {
        val result = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val bmpW = result.width.toFloat()
        val bmpH = result.height.toFloat()

        val barH  = (bmpH * 0.15f).coerceAtLeast(80f)
        val barTop = bmpH - barH

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0)
        }
        canvas.drawRect(0f, barTop, bmpW, bmpH, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color    = Color.WHITE
            textSize = (barH * 0.22f).coerceAtLeast(20f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val padding = bmpW * 0.025f
        val lineH   = textPaint.textSize * 1.5f

        // Line 1 — Location (always valid here because isLocationReady was checked)
        val locationText = when {
            addressLine.isNotEmpty() -> "Location: $addressLine"
            else ->
                "Lat: ${"%.6f".format(finalLat.toDoubleOrNull() ?: 0.0)}  " +
                        "Lon: ${"%.6f".format(finalLon.toDoubleOrNull() ?: 0.0)}"
        }
        canvas.drawText(locationText, padding, barTop + lineH, textPaint)

        // Line 2 — Date/time
        val dateText = DateFormat.getDateTimeInstance().format(Date())
        canvas.drawText(dateText, padding, barTop + lineH * 2f, textPaint)

        return result
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Output file
    // ─────────────────────────────────────────────────────────────────────────

    private fun getOutputMediaFile(): File {
        val ctx = requireContext()
        val dir = File(ctx.externalCacheDir, GPS_DIR).also { if (!it.exists()) it.mkdirs() }
        val ts  = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        return File(dir, "GPSCamera_${ts}_2.JPEG")
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Location updates
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!allPermissionsGranted()) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_MS
        ).setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                finalLat = loc.latitude.toString()
                finalLon = loc.longitude.toString()
                isLocationReady = true          // ← GPS fix received
                updateLocationUI(loc.latitude, loc.longitude)
                Log.d(TAG, "Location updated: lat=$finalLat  lon=$finalLon")
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            isRequestingLocationUpdates = true

            // Seed from last known location immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null && !isLocationReady) {
                    finalLat = loc.latitude.toString()
                    finalLon = loc.longitude.toString()
                    isLocationReady = true      // ← last-known counts as ready
                    updateLocationUI(loc.latitude, loc.longitude)
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission revoked: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "startLocationUpdates error: ${e.message}")
        }
    }

    private fun stopLocationUpdates() {
        if (!isRequestingLocationUpdates) return
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
                .addOnCompleteListener { isRequestingLocationUpdates = false }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Update UI with address
    // ─────────────────────────────────────────────────────────────────────────

    private fun updateLocationUI(lat: Double, lon: Double) {
        // Show real coordinates immediately
        tvLocation.text = "Location: Lat ${"%.5f".format(lat)}, Lon ${"%.5f".format(lon)}"

        // Reverse geocode in background
        ioScope.launch {
            try {
                val geocoder = Geocoder(requireContext(), Locale.ENGLISH)
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr    = addresses[0]
                    val city    = addr.locality    ?: ""
                    val state   = addr.adminArea   ?: ""
                    val country = addr.countryName ?: ""
                    val line0   = addr.getAddressLine(0) ?: ""
                    addressLine = "$line0\n$city, $state, $country"

                    withContext(Dispatchers.Main) {
                        if (isAdded) tvLocation.text = "Location: $addressLine"
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Geocoder failed: ${e.message}")
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Date / time
    // ─────────────────────────────────────────────────────────────────────────

    private fun setCurrentDateAndTime() {
        try {
            val currentDateTime = DateFormat.getDateTimeInstance().format(Date())
            tvDateTime.text = "Time : $currentDateTime"
        } catch (e: Exception) {
            Log.w(TAG, "setCurrentDateAndTime: ${e.message}")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UI helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Shows a "Fetching location…" placeholder on [tvLocation].
     * Called immediately in onViewCreated so the user sees it from the start
     * instead of an empty text view.
     */
    private fun setLocationFetching() {
        tvLocation.text = "Location: Fetching…"
    }

    private fun showViewFinder() {
        viewFinder.visibility      = View.VISIBLE
        llCapturePhoto.visibility  = View.VISIBLE
        llRetakePhoto.visibility   = View.GONE
        ivCapturedPhoto.visibility = View.GONE
        tvRetake.text = "Retake"
    }

    private fun showSaveButton() {
        llRetakePhoto.visibility = View.VISIBLE
        tvRetake.text = "Save"
    }

    private fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toast(msg: String) {
        if (isAdded) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}