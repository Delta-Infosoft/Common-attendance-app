package com.i.common.attendance.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.CheckInCheckOutRequest
import com.i.common.attendance.network.request.DeviceTrackingRequest
import com.i.common.attendance.network.request.GetAttendanceInOutRequest
import com.i.common.attendance.network.request.GetRecordsRequest
import com.i.common.attendance.network.request.LogoutRequest
import com.i.common.attendance.network.request.TextListRequest
import com.i.common.attendance.network.response.AttendanceRecord
import com.i.common.attendance.network.response.Records
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.delta.attendanceappv2.ui.home.viewmodel.LogoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val prefHelper: EncryptedPrefHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // =========================================================================================
    // ========================== Dashboard last 5 day records =================================
    private val _recordsState = MutableLiveData<GetRecordsState>(GetRecordsState.Idle)
    val recordsState: LiveData<GetRecordsState> = _recordsState
    // =========================================================================================
    // ========================== Dashboard first checkInOut Api ===============================
    private val _checkInOutState = MutableLiveData<GetCheckInOutState>(GetCheckInOutState.Idle)
    val checkInOutState: LiveData<GetCheckInOutState> = _checkInOutState
    // =========================================================================================
    // ========================== Background Location Tracking =================================
    private val _latLongState = MutableLiveData<ApiState>(ApiState.Idle)
    val latLongState: LiveData<ApiState> = _latLongState
    // =========================================================================================
    // ========================== Attendance checkInOut API =================================
    private val _attendanceCheckInOutState = MutableLiveData<ApiState>(ApiState.Idle)
    val attendanceCheckInOutState: LiveData<ApiState> = _attendanceCheckInOutState
    // =========================================================================================
    // ========================== Get Text List API =============================================
    private val _textListState = MutableLiveData<GetTextListState>(GetTextListState.Idle)
    val textListState: LiveData<GetTextListState> = _textListState
    // =========================================================================================
    // ========================== Log out call back to fragment =================================
    private val _logoutEvent = MutableLiveData<Unit>()
    val logoutEvent: LiveData<Unit> = _logoutEvent

    fun requestLogout() {
        _logoutEvent.value = Unit
    }
    // =========================================================================================
    // ========================== Log out API =============================================
    private val _logoutState = MutableLiveData<LogoutState>(LogoutState.Idle)
    val logoutState: LiveData<LogoutState> = _logoutState

    fun getRecords(mobileNo: String, month: String) {
        viewModelScope.launch {
            _recordsState.value = GetRecordsState.Loading
            try {
                val response = repository.getRecordAPI(GetRecordsRequest(mobileNo, month))

                // HTTP failure
                if (!response.isSuccessful) {
                    _recordsState.value = GetRecordsState.Error("Server error (${response.code()})")
                    return@launch
                }

                val body = response.body()

                // Null body check
                if (body == null) {
                    _recordsState.value = GetRecordsState.Error("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        // ✅ Handles both result: [...] and result: "" safely
                        val records = body.result.toRecordsList()

                        if (records.isEmpty()) {
                            _recordsState.value = GetRecordsState.Empty(body.message ?: "No records found")
                            _recordsState.value = GetRecordsState.Idle
                        } else {
                            _recordsState.value = GetRecordsState.Success(records)
                            _recordsState.value = GetRecordsState.Idle
                        }
                    }

                    "209" -> {
                        _recordsState.value = GetRecordsState.Empty(body.message ?: "No Record Found")
                        _recordsState.value = GetRecordsState.Idle
                    }

                    else -> {
                        _recordsState.value = GetRecordsState.Error(body.message ?: "Something went wrong")
                        _recordsState.value = GetRecordsState.Idle
                    }
                }

            } catch (e: IOException) {
                _recordsState.value = GetRecordsState.Error("Please check your internet connection")
                _recordsState.value = GetRecordsState.Idle
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _recordsState.value = GetRecordsState.Error("Something went wrong")
                _recordsState.value = GetRecordsState.Idle
            }
        }
    }

    fun Any?.toRecordsList(): List<Records> {
        // Case 1 — result is "" (empty string) → return empty list
        if (this == null || this is String) return emptyList()

        // Case 2 — result is a List → safely cast each item using Gson
        return try {
            val gson = Gson()
            val json = gson.toJson(this)           // converts LinkedTreeMap → JSON string
            val type = object : TypeToken<List<Records>>() {}.type
            gson.fromJson(json, type) ?: emptyList() // JSON string → List<Records> ✅
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun Any?.toAttendanceRecordList(): List<AttendanceRecord> {
        return try {
            when (this) {
                is List<*> -> this.filterIsInstance<Map<String, Any?>>().map {
                    Gson().fromJson(
                        Gson().toJson(it),
                        AttendanceRecord::class.java
                    )
                }

                is String -> emptyList() // when API returns ""
                null -> emptyList()

                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getCheckInCheckOutStatusAPI(mobileNo: String) {
        viewModelScope.launch {
            _checkInOutState.value = GetCheckInOutState.Loading

            try {
                val response = repository.getCheckInCheckOutStatusAPI(
                    CheckInCheckOutRequest(mobileNo)
                )

                // HTTP failure
                if (!response.isSuccessful) {
                    _checkInOutState.value =
                        GetCheckInOutState.Error("Server error (${response.code()})")
                    return@launch
                }

                val body = response.body()

                // Null body check
                if (body == null) {
                    _checkInOutState.value =
                        GetCheckInOutState.Error("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        // Handles result: [...] OR result: ""
                        val records = body.result.toAttendanceRecordList()

                        if (records.isEmpty()) {
                            _checkInOutState.value =
                                GetCheckInOutState.Empty(
                                    body.message ?: "No records found"
                                )
                            _checkInOutState.value = GetCheckInOutState.Idle
                        } else {
                            _checkInOutState.value =
                                GetCheckInOutState.Success(records)
                            _checkInOutState.value = GetCheckInOutState.Idle

                        }
                    }

                    "209" -> {
                        _checkInOutState.value =
                            GetCheckInOutState.Empty(
                                body.message ?: "No Record Found"
                            )
                        _checkInOutState.value = GetCheckInOutState.Idle

                    }


                    else -> {
                        _checkInOutState.value =
                            GetCheckInOutState.Error(
                                body.message ?: "Something went wrong"
                            )
                        _checkInOutState.value = GetCheckInOutState.Idle

                    }
                }

            } catch (e: IOException) {
                _checkInOutState.value =
                    GetCheckInOutState.Error("Please check your internet connection")
                _checkInOutState.value = GetCheckInOutState.Idle

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _checkInOutState.value =
                    GetCheckInOutState.Error("Something went wrong")
                _checkInOutState.value = GetCheckInOutState.Idle
            }
        }
    }

    fun insertLatLong(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _latLongState.value = ApiState.Loading

            try {
                val user = prefHelper.getUser()
                if (user == null) {
                    _latLongState.value = ApiState.Error("User not found")
                    return@launch
                }

                val request = DeviceTrackingRequest(
                    mobileNo = user.MobileNo ?: "",
                    latitude = latitude,
                    longitude = longitude,
                    batteryStatus = Constants.getBatteryLevel(context).toString(),
                    gpsStatus = Constants.isGpsEnabled(context).toString(),
                    netStatus = Constants.isNetworkAvailable(context).toString(),
                    appVersion = Constants.getAppVersion(context),
                    insertedOn = Constants.getCurrentTimestamp("dd-MMM-yyyy hh:mm:ss a"),
                    modelName = Constants.getDeviceName(),
                    androidVersion = Constants.getAndroidVersion()
                )

                val response = repository.insertLatLongAPI(request)

                if (!response.isSuccessful) {
                    _latLongState.value = ApiState.Error("Server error")
                    return@launch
                }

                val body = response.body()
                if (body == null || body.status != "200") {
                    _latLongState.value =
                        ApiState.Error(body?.message ?: "Something went wrong")
                    return@launch
                }

                _latLongState.value = ApiState.Success("Location sent successfully")

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _latLongState.value = ApiState.Error("API failed")
            }
        }
    }

    fun getAttendanceInOutAPI(latitude: Double, longitude: Double,inTime: String,outTime: String,status : String,remark: String) {
        viewModelScope.launch {
            _attendanceCheckInOutState.value = ApiState.Loading

            try {
                val user = prefHelper.getUser()
                if (user == null) {
                    _attendanceCheckInOutState.value = ApiState.Error("User not found")
                    return@launch
                }

                val request = GetAttendanceInOutRequest(
                    mobileNo = user.MobileNo ?: "",
                    latitude = latitude.toString(),
                    longitude = longitude.toString(),
                    gpsStatus = Constants.isGpsEnabled(context).toString(),
                    netStatus = Constants.isNetworkAvailable(context).toString(),
                    inTime = inTime,
                    outTime = outTime,
                    status = status,
                    remarks = remark,
                    batteryStatus = Constants.getBatteryLevel(context).toString(),
                    )

                val response = repository.getAttendanceInOutAPI(request)

                if (!response.isSuccessful) {
                    _attendanceCheckInOutState.value = ApiState.Error("Server error")
                    return@launch
                }

                val body = response.body()
                if (body == null || body.status != "200") {
                    _attendanceCheckInOutState.value = ApiState.Error(body?.message ?: "Something went wrong")
                    return@launch
                }

                _attendanceCheckInOutState.value = ApiState.Success("API successfully")
                _attendanceCheckInOutState.value = ApiState.Idle

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _attendanceCheckInOutState.value = ApiState.Error("API failed")
                _attendanceCheckInOutState.value = ApiState.Idle
            }
        }
    }

    fun getTextListAPI() {
        viewModelScope.launch {
            _textListState.value = GetTextListState.Loading

            try {
                val response = repository.getTextListAPI(TextListRequest(type = "AttendanceStatus"))

                if (!response.isSuccessful) {
                    _textListState.value =
                        GetTextListState.Error("Server error")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _textListState.value =
                        GetTextListState.Error("Empty response")
                    return@launch
                }

                if (body.status != "200") {
                    _textListState.value =
                        GetTextListState.Error(body.message ?: "Unknown error")
                    return@launch
                }

                val list = body.result ?: emptyList()

                if (list.isEmpty()) {
                    _textListState.value =
                        GetTextListState.Empty("No data found")
                    return@launch
                }

                _textListState.value =
                    GetTextListState.Success(list)

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _textListState.value =
                    GetTextListState.Error("Something went wrong")
            }
        }
    }

    fun logout(userName: String, imei: String) {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading

            try {
                val request = LogoutRequest(userName = userName, imei = imei)

                val response = repository.logOutWithFCMIdAPI(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.status == "200" || body.status == "209") {
                        _logoutState.value = LogoutState.Success(body.message)
                    } else {
                        _logoutState.value = LogoutState.Error(body.message ?: "Logout failed")
                    }
                } else {
                    _logoutState.value =
                        LogoutState.Error("Unable to connect server")
                }

            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error("Something went wrong")
            }
        }
    }
}