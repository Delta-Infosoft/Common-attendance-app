package com.i.common.attendance.utils
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.BuildConfig
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Constants {
    const val TAG_SERVICE = "LocationService"
    const val TAG_REPO = "LocationRepository"
    const val KEY_U0027: String = "u0027"
    const val KEY_U0026: String = "u0026"
    const val KEY_AFOSTROPHE: String = "'"
    const val KEY_EMPER: String = "&"
    const val KEY_U005B: String = "U+005B"
    const val KEY_U005D: String = "U+005D"
    const val KEY_OB: String = "["
    const val KEY_CB: String = "]"


    val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun String.cleanSql(): String {
        return this
            .replace("u0026", "&")
            .replace("u0027", "'")
            .replace("\r\n", "")
    }
    fun ipToBaseUrl(ip: String): String {
        return "http://$ip/"
    }

    fun hideKeyboard(view: View?) {
        view ?: return
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
    }

    fun provideMessage(context: Context, msg: String) {
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
    }

    fun getCurrentFormattedDate(): String {
        val formatter = DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM yyyy",
            Locale.getDefault()
        )
        return LocalDate.now().format(formatter)
    }

    /*fun convertDateFormat(
        inputDate: String?,
        inputDateFormat: String,
        outputDateFormat: String
    ): String? {
        var outPutData: String? = null

        try {
            val inputParser = SimpleDateFormat(inputDateFormat, Locale.ENGLISH)
            val outputParser = SimpleDateFormat(outputDateFormat, Locale.ENGLISH)

            var dobdate: Date? = null
            dobdate = inputParser.parse(inputDate)
            outPutData = outputParser.format(dobdate)
        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return outPutData
    }*/

    fun convertDateFormat(
        inputDate: String?,
        inputDateFormat: String,
        outputDateFormat: String
    ): String? {
        if (inputDate.isNullOrEmpty()) return null

        val fallbackFormats = listOf(
            inputDateFormat,            // Primary (whatever is passed)
            "M/d/yyyy hh:mm:ss a",     // 3/16/2026 12:00:00 AM
            "M/d/yyyy HH:mm:ss",       // 3/16/2026 11:32:05
            "dd/MMM/yyyy HH:mm:ss a",  // 16/Mar/2026 12:00:00 AM
            "dd-MMM-yyyy HH:mm:ss a",  // 16-Mar-2026 12:00:00 AM
            "dd/MMM/yyyy hh:mm:ss a",  // 16/Mar/2026 12:00:00 AM
        )

        val outputParser = SimpleDateFormat(outputDateFormat, Locale.ENGLISH)

        for (format in fallbackFormats) {
            try {
                val inputParser = SimpleDateFormat(format, Locale.ENGLISH)
                inputParser.isLenient = false  // Strict parsing — avoid false positives
                val date = inputParser.parse(inputDate) ?: continue
                return outputParser.format(date)
            } catch (e: Exception) {
                continue  // Try next format
            }
        }

        // All formats failed — log once
        FirebaseCrashlytics.getInstance().recordException(
            Exception("convertDateFormat: No matching format for input -> '$inputDate'")
        )

        return null
    }

    fun AppCompatEditText.isEmpty(): Boolean {
        return this.text.toString().trim().isEmpty()
    }
    fun TextInputEditText.isEmpty(): Boolean = this.text.toString().trim().isEmpty()
    fun TextInputEditText.getTrimmedText(): String = this.text?.toString()?.trim().orEmpty()

    fun TextInputEditText.doubleValue(): Double = this.text?.toString()?.trim().orEmpty().toDoubleOrNull() ?: 0.0

    fun AppCompatEditText.getTrimmedText(): String {
        return this.text.toString().trim()
    }

    fun String.removeTrailingZeros(): String {
        return try {
            BigDecimal(this).stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            this
        }
    }

    //This for Check Newtwork
    fun isInternetAvailable(context: Context): Boolean {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                // Check if the network is validated (i.e., actually connected to the internet)
                val isNetworkValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                // For Wi-Fi, ensure it's validated before returning true
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    val hasWiFiInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    return hasInternet && hasWiFiInternet
                }

                return hasInternet && isNetworkValidated

            } else {
                @Suppress("DEPRECATION")
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo?.isConnected == true
            }
        } catch (e: Exception) {
            Log.e("InternetCheck", "Error checking internet connection: ${e.localizedMessage}")
            return false
        }
    }

    fun View.setSafeOnClickListener(
        interval: Long = 800L,
        onSafeClick: (View) -> Unit
    ) {
        var lastClickTime = 0L

        setOnClickListener { view ->
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastClickTime >= interval) {
                lastClickTime = currentTime
                onSafeClick(view)
            }
        }
    }


    fun getCurrentTimestamp(dateTimeFormat: String): String {
        return try {
            val dateFormat = SimpleDateFormat(dateTimeFormat, Locale.ENGLISH)
            dateFormat.format(Date())
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            ""
        }
    }

    fun getDeviceName(): String {
        val deviceName = (Build.MANUFACTURER
                + " " + Build.MODEL
                + " " + Build.VERSION.RELEASE
                + " " + VERSION_CODES::class.java.getFields()[Build.VERSION.SDK_INT].getName())

        return deviceName
    }

    fun getAndroidVersion(): String {
        var version = ""
        val builder = StringBuilder()
        builder.append("android : ").append(Build.VERSION.RELEASE)

        val fields = VERSION_CODES::class.java.getFields()
        for (field in fields) {
            val fieldName = field.getName()
            var fieldValue = -1

            try {
                fieldValue = field.getInt(Any())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(" : ").append(fieldName).append(" : ")
                builder.append("sdk=").append(fieldValue)
            }
        }

        version = builder.toString()

        return version
    }

    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun getBatteryLevel(context: Context): Int {
        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY
        )
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun getAppVersion(context: Context): String {
        return try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun isSameAsToday(inTime: String?): Boolean {
        if (inTime.isNullOrBlank()) return false

        val formats = listOf(
            "dd/MM/yyyy hh:mm:ss a",
            "d/M/yyyy hh:mm:ss a",
            "MM/dd/yyyy hh:mm:ss a",
            "M/d/yyyy h:mm:ss a",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MMM-yyyy hh:mm:ss a",
        )

        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                    isLenient = false
                }

                val parsedDate = sdf.parse(inTime) ?: continue

                val apiCal = Calendar.getInstance().apply {
                    time = parsedDate
                }

                val todayCal = Calendar.getInstance()

                if (
                    apiCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                    apiCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
                ) {
                    return true
                }
            } catch (_: Exception) {
                // try next format
            }
        }

        return false
    }

    fun getTodayDateFormatted(inTime: String?): String? {
        if (inTime.isNullOrBlank()) return null

        val inputFormats = listOf(
            "dd/MM/yyyy hh:mm:ss a",
            "d/M/yyyy hh:mm:ss a",
            "MM/dd/yyyy hh:mm:ss a",
            "M/d/yyyy h:mm:ss a",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MMM-yyyy hh:mm:ss a",
            )

        val outputFormat = SimpleDateFormat(
            "dd/MM/yyyy hh:mm:ss a",
            Locale.ENGLISH
        )

        for (pattern in inputFormats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                    isLenient = false
                }

                val parsedDate = sdf.parse(inTime) ?: continue

                val apiCal = Calendar.getInstance().apply { time = parsedDate }
                val todayCal = Calendar.getInstance()

                val isToday =
                    apiCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                            apiCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

                if (isToday) {
                    return outputFormat.format(parsedDate)
                }

            } catch (_: Exception) {
                // try next format
            }
        }

        return null
    }




    fun isValidIp(ip: String): Boolean {
        when(BuildConfig.FLAVOR) {
            "unnati" -> return ip.trim().isNotEmpty()
            "duke" -> return ip.trim().isNotEmpty()
            "flotech","singla","algo","mascot" -> return ip.trim().isNotEmpty()
        }
        val regex = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$"

        if (!ip.matches(Regex(regex))) return false

        val parts = ip.split(".").map { it.toInt() }

        // 0.0.0.0
        if (parts.all { it == 0 }) return false

        // Loopback
        if (parts[0] == 127) return false

        // Network / broadcast address
        if (parts[3] == 0 || parts[3] == 255) return false

        return true
    }

    fun showConfirmDialog(
        context: Context,
        title: String,
        message: String? = null,
        okText: String = "OK",
        cancelText: String = "Cancel",
        onOk: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .apply {
                message?.let { setMessage(it) }
            }
            .setPositiveButton(okText) { dialog, _ ->
                dialog.dismiss()
                onOk()
            }
            .setNegativeButton(cancelText) { dialog, _ ->
                dialog.dismiss()
                onCancel?.invoke()
            }
            .setCancelable(false)
            .show()
    }

    fun getReplacedString(convertString: String?): String {
        return convertString
            ?.replace(KEY_U0027, KEY_AFOSTROPHE)
            ?.replace(KEY_U0026, KEY_EMPER)
            ?.replace(KEY_U005B, KEY_OB)
            ?.replace(KEY_U005D, KEY_CB)
            ?: ""
    }


}