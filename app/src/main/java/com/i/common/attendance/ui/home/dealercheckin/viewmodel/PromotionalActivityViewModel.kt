package com.i.common.attendance.ui.home.dealercheckin.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.AddLeaveUnnatiRequest
import com.i.common.attendance.network.request.CheckDealerInOutStatusRequest
import com.i.common.attendance.network.request.GetTargetOutStandingRequest
import com.i.common.attendance.network.request.SaveDealerCheckInRequest
import com.i.common.attendance.network.request.SavePromotionalActivityRequest
import com.i.common.attendance.network.request.ViewLeaveApprovalUpdateUnnatiRequest
import com.i.common.attendance.network.request.ViewLeaveListUnnatiRequest
import com.i.common.attendance.network.response.CheckDealerInOutStatusData
import com.i.common.attendance.network.response.DailyTourDistrict
import com.i.common.attendance.network.response.TargetOutstandingData
import com.i.common.attendance.network.response.ViewLeaveUnnatiList
import com.i.common.attendance.ui.home.dailytour.viewmodel.DistrictState
import com.i.common.attendance.ui.home.leave.viewmodel.LeaveApprovalUpdateState
import com.i.common.attendance.ui.home.leave.viewmodel.LeaveRequestState
import com.i.common.attendance.ui.home.leave.viewmodel.ViewLeaveListState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PromotionalActivityViewModel @Inject constructor(private val repository: PromotionalActivityRepository,
                                                       @ApplicationContext private val context: Context) : ViewModel() {

    /*======================================================================================*/
    /* Promotional Activity */
    /*======================================================================================*/
    private val _savePromotionalActivityState = MutableLiveData<PromotionalActivityUiState>(PromotionalActivityUiState.Idle)
    val savePromotionalActivityState: LiveData<PromotionalActivityUiState> = _savePromotionalActivityState
    fun insertPromotionalActivity(request: SavePromotionalActivityRequest) {

        _savePromotionalActivityState.value = PromotionalActivityUiState.Loading

        viewModelScope.launch {

            try {

                val response = repository.insertPromotionalActivity(request, context)

                if (!response.isSuccessful) {
                    _savePromotionalActivityState.value = PromotionalActivityUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _savePromotionalActivityState.value = PromotionalActivityUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        _savePromotionalActivityState.value = PromotionalActivityUiState.Success(body)
                    }

                    "209" -> {
                        _savePromotionalActivityState.value = PromotionalActivityUiState.ApiError(body.message.ifEmpty { "No Record Found" })
                    }

                    else -> {
                        _savePromotionalActivityState.value = PromotionalActivityUiState.ApiError(body.message.ifEmpty { "Something went wrong" })
                    }
                }

            } catch (e: IOException) {
                _savePromotionalActivityState.value = PromotionalActivityUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _savePromotionalActivityState.value = PromotionalActivityUiState.ApiError(e.message ?: "Something went wrong")
            }
        }
    }

    /*======================================================================================*/

    private val _districtState = MutableLiveData<DistrictState>(DistrictState.Idle)
    val districtState: LiveData<DistrictState> = _districtState
    var cachedDistrictList: List<DailyTourDistrict>? = null
    fun getDistrictList() {
        cachedDistrictList?.let { _districtState.value = DistrictState.Success(it)
            return
        }
        _districtState.value = DistrictState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getDistricts()
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _districtState.value = DistrictState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        val list = body.result ?: emptyList()
                        if (list.isEmpty()) {
                            _districtState.value = DistrictState.ApiError("No record found")
                            return@launch
                        }
                        cachedDistrictList = list
                        _districtState.value = DistrictState.Success(list)
                    }
                    "209" -> {
                        _districtState.value = DistrictState.ApiError(body.message ?: "No record found")
                    }
                    else -> {
                        _districtState.value = DistrictState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _districtState.value = DistrictState.NetworkError("Something went wrong")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _districtState.value = DistrictState.ApiError("Something went wrong")
            }
        }
    }

    /*======================================================================================*/
    /* Target Outstanding -> Check In Time Show */
    /*======================================================================================*/
    private val _targetOutstandingState = MutableLiveData<TargetOutstandingState>(TargetOutstandingState.Idle)
    val targetOutstandingState: LiveData<TargetOutstandingState> = _targetOutstandingState

    fun getTargetOutstanding(empId: String) {

        _targetOutstandingState.value = TargetOutstandingState.Loading

        viewModelScope.launch {

            try {

                val request = GetTargetOutStandingRequest(empId = empId)

                val response = repository.getTargetOutstanding(request)

                if (!response.isSuccessful) {

                    _targetOutstandingState.value =
                        TargetOutstandingState.ApiError(
                            "Server error : ${response.code()}"
                        )

                    return@launch
                }

                val body = response.body()

                if (body == null) {

                    _targetOutstandingState.value =
                        TargetOutstandingState.ApiError(
                            "Empty server response"
                        )

                    return@launch
                }

                when (body.status) {

                    "200" -> {

                        try {

                            val list: List<TargetOutstandingData> =

                                if (body.result != null && body.result.isJsonArray) {
                                    Gson().fromJson(body.result, object : TypeToken<List<TargetOutstandingData>>() {}.type)
                                } else {
                                    emptyList()
                                }

                            if (list.isNotEmpty()) {

                                _targetOutstandingState.value = TargetOutstandingState.Success(list)

                            } else {

                                _targetOutstandingState.value =
                                    TargetOutstandingState.ApiError(
                                        "No record found"
                                    )
                            }

                        } catch (e: Exception) {

                            _targetOutstandingState.value =
                                TargetOutstandingState.ApiError(
                                    "Data parsing error"
                                )
                        }
                    }

                    "209" -> {

                        _targetOutstandingState.value =
                            TargetOutstandingState.ApiError(
                                body.message ?: "No record found"
                            )
                    }

                    else -> {

                        _targetOutstandingState.value =
                            TargetOutstandingState.ApiError(
                                body.message ?: "Something went wrong"
                            )
                    }
                }

            } catch (e: IOException) {

                _targetOutstandingState.value =
                    TargetOutstandingState.NetworkError(
                        "Please check your internet connection"
                    )

            } catch (e: Exception) {

                FirebaseCrashlytics.getInstance().recordException(e)

                _targetOutstandingState.value =
                    TargetOutstandingState.ApiError(
                        e.message ?: "Something went wrong"
                    )
            }
        }
    }

    /*======================================================================================*/
    /* Dealer Check-In API */
    /*======================================================================================*/

    private val _dealerCheckInState = MutableLiveData<DealerCheckInState>(DealerCheckInState.Idle)
    val dealerCheckInState: LiveData<DealerCheckInState> = _dealerCheckInState
    fun insertDealerCheckIn(request: SaveDealerCheckInRequest) {
        _dealerCheckInState.value = DealerCheckInState.Loading
        viewModelScope.launch {
            try {
                val response =
                    repository.insertDealerCheckIn(request = request, context = context)
                if (!response.isSuccessful) {
                    _dealerCheckInState.value = DealerCheckInState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _dealerCheckInState.value = DealerCheckInState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _dealerCheckInState.value = DealerCheckInState.Success(body)
                    }

                    "209" -> {
                        _dealerCheckInState.value =
                            DealerCheckInState.ApiError(body.message.ifEmpty { "No record found" })
                    }

                    else -> {
                        _dealerCheckInState.value =
                            DealerCheckInState.ApiError(body.message.ifEmpty { "Something went wrong" })
                    }
                }

            } catch (e: IOException) {
                _dealerCheckInState.value = DealerCheckInState.NetworkError("Something went wrong")

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dealerCheckInState.value =
                    DealerCheckInState.ApiError(e.message ?: "Something went wrong")
            }
        }
    }
    /*======================================================================================*/
    /* Check Dealer In-Out Status API */
    /*======================================================================================*/

    private val _checkDealerInOutStatusState = MutableLiveData<CheckDealerInOutStatusState>(CheckDealerInOutStatusState.Idle)
    val checkDealerInOutStatusState: LiveData<CheckDealerInOutStatusState> = _checkDealerInOutStatusState
    fun checkDealerInOutStatus(request: CheckDealerInOutStatusRequest) {

        _checkDealerInOutStatusState.value =
            CheckDealerInOutStatusState.Loading

        viewModelScope.launch {
            try {
                val response = repository.checkDealerInOutStatus(request)
                if (!response.isSuccessful) {
                    _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        try {
                            val list: List<CheckDealerInOutStatusData> =
                                if (body.result != null && body.result.isJsonArray) {
                                    Gson().fromJson(body.result, object : TypeToken<List<CheckDealerInOutStatusData>>() {}.type)
                                } else {
                                    emptyList()
                                }

                            if (list.isNotEmpty()) {
                                _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.Success(list)
                            } else {
                                _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.ApiError("No record found")
                            }

                        } catch (e: Exception) {
                            _checkDealerInOutStatusState.value =
                                CheckDealerInOutStatusState.ApiError("Data parsing error")
                        }
                    }

                    "209" -> {
                        _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.ApiError(body.message ?: "No record found")
                    }

                    else -> {
                        _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.NetworkError(e.message ?: "Something went wrong")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _checkDealerInOutStatusState.value = CheckDealerInOutStatusState.ApiError(e.message ?: "Something went wrong")
            }
        }
    }

    /*======================================================================================*/
    /* Leave Request Unnati */
    /*======================================================================================*/

    private val _leaveRequestState = MutableLiveData<LeaveRequestState>(LeaveRequestState.Idle)

    val leaveRequestState: LiveData<LeaveRequestState> = _leaveRequestState

    fun insertLeaveRequestUnnati(request: AddLeaveUnnatiRequest) {
        _leaveRequestState.value = LeaveRequestState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertLeaveRequestUnnati(request)
                // =========================
                // API FAILURE
                // =========================

                if (!response.isSuccessful) {
                    _leaveRequestState.value = LeaveRequestState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                // =========================
                // EMPTY BODY
                // =========================

                if (body == null) {
                    _leaveRequestState.value = LeaveRequestState.ApiError("Empty server response")
                    return@launch
                }

                // =========================
                // STATUS HANDLE
                // =========================

                when (body.status) {

                    // SUCCESS
                    "200" -> {
                        _leaveRequestState.value = LeaveRequestState.Success(body)
                    }

                    // BUSINESS ERROR
                    "209" -> {
                        _leaveRequestState.value = LeaveRequestState.ApiError(body.message ?: "No record found")
                    }

                    // OTHER ERROR
                    else -> {
                        _leaveRequestState.value = LeaveRequestState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _leaveRequestState.value = LeaveRequestState.NetworkError(e.message ?: "Something went wrong")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _leaveRequestState.value = LeaveRequestState.ApiError(e.message ?: "Something went wrong")
            }
        }
    }

    /*======================================================================================*/
    /* View Leave List Unnati */
    /*======================================================================================*/

    private val _viewLeaveListState = MutableLiveData<ViewLeaveListState>(ViewLeaveListState.Idle)
    val viewLeaveListState: LiveData<ViewLeaveListState> = _viewLeaveListState
    fun viewLeaveListUnnati(request: ViewLeaveListUnnatiRequest) {

        _viewLeaveListState.value = ViewLeaveListState.Loading

        viewModelScope.launch {

            try {

                val response = repository.viewLeaveListUnnati(request)

                // =========================
                // API FAILURE
                // =========================

                if (!response.isSuccessful) {

                    _viewLeaveListState.value =
                        ViewLeaveListState.ApiError(
                            "Server error : ${response.code()}"
                        )

                    return@launch
                }

                val body = response.body()

                // =========================
                // EMPTY BODY
                // =========================

                if (body == null) {

                    _viewLeaveListState.value =
                        ViewLeaveListState.ApiError(
                            "Empty server response"
                        )

                    return@launch
                }

                // =========================
                // STATUS HANDLE
                // =========================

                when (body.status) {

                    // =====================
                    // SUCCESS
                    // =====================

                    "200" -> {

                        try {

                            val list: List<ViewLeaveUnnatiList> =
                                if (body.result != null && body.result!!.isJsonArray) {
                                    Gson().fromJson(
                                        body.result,
                                        object :
                                            TypeToken<List<ViewLeaveUnnatiList>>() {}.type
                                    )

                                } else {

                                    emptyList()
                                }

                            if (list.isNotEmpty()) {

                                _viewLeaveListState.value =
                                    ViewLeaveListState.Success(
                                        list
                                    )

                            } else {

                                _viewLeaveListState.value =
                                    ViewLeaveListState.ApiError(
                                        "No record found"
                                    )
                            }

                        } catch (e: Exception) {

                            _viewLeaveListState.value =
                                ViewLeaveListState.ApiError(
                                    "Data parsing error"
                                )
                        }
                    }

                    // =====================
                    // BUSINESS ERROR
                    // =====================

                    "209" -> {
                        _viewLeaveListState.value =
                            ViewLeaveListState.ApiError(
                                body.message ?: "No record found"
                            )
                    }

                    // =====================
                    // OTHER ERROR
                    // =====================

                    else -> {

                        _viewLeaveListState.value =
                            ViewLeaveListState.ApiError(
                                body.message
                                    ?: "Something went wrong"
                            )
                    }
                }

            } catch (e: IOException) {

                _viewLeaveListState.value =
                    ViewLeaveListState.NetworkError(
                        e.message
                            ?: "Please check your internet connection"
                    )

            } catch (e: Exception) {

                FirebaseCrashlytics
                    .getInstance()
                    .recordException(e)

                _viewLeaveListState.value =
                    ViewLeaveListState.ApiError(
                        e.message
                            ?: "Something went wrong"
                    )
            }
        }
    }

    /*======================================================================================*/
    /* View Leave List Unnati */
    /*======================================================================================*/

    private val _viewLeaveListStateApproval = MutableLiveData<ViewLeaveListState>(ViewLeaveListState.Idle)
    val viewLeaveListStateApproval: LiveData<ViewLeaveListState> = _viewLeaveListStateApproval
    fun viewLeaveListApprovalUnnati(request: ViewLeaveListUnnatiRequest) {

        _viewLeaveListState.value = ViewLeaveListState.Loading

        viewModelScope.launch {

            try {

                val response = repository.viewLeaveListApprovalUnnati(request)

                // =========================
                // API FAILURE
                // =========================

                if (!response.isSuccessful) {

                    _viewLeaveListStateApproval.value =
                        ViewLeaveListState.ApiError(
                            "Server error : ${response.code()}"
                        )

                    return@launch
                }

                val body = response.body()

                // =========================
                // EMPTY BODY
                // =========================

                if (body == null) {

                    _viewLeaveListStateApproval.value =
                        ViewLeaveListState.ApiError(
                            "Empty server response"
                        )

                    return@launch
                }

                // =========================
                // STATUS HANDLE
                // =========================

                when (body.status) {

                    // =====================
                    // SUCCESS
                    // =====================

                    "200" -> {

                        try {

                            val list: List<ViewLeaveUnnatiList> =
                                if (body.result != null && body.result!!.isJsonArray) {
                                    Gson().fromJson(
                                        body.result,
                                        object :
                                            TypeToken<List<ViewLeaveUnnatiList>>() {}.type
                                    )

                                } else {

                                    emptyList()
                                }

                            if (list.isNotEmpty()) {

                                _viewLeaveListStateApproval.value =
                                    ViewLeaveListState.Success(
                                        list
                                    )

                            } else {

                                _viewLeaveListStateApproval.value =
                                    ViewLeaveListState.ApiError(
                                        "No record found"
                                    )
                            }

                        } catch (e: Exception) {

                            _viewLeaveListStateApproval.value =
                                ViewLeaveListState.ApiError(
                                    "Data parsing error"
                                )
                        }
                    }

                    // =====================
                    // BUSINESS ERROR
                    // =====================

                    "209" -> {
                        _viewLeaveListStateApproval.value = ViewLeaveListState.ApiError( body.message ?: "No record found")
                    }

                    // =====================
                    // OTHER ERROR
                    // =====================

                    else -> {

                        _viewLeaveListStateApproval.value =
                            ViewLeaveListState.ApiError(
                                body.message
                                    ?: "Something went wrong"
                            )
                    }
                }

            } catch (e: IOException) {

                _viewLeaveListStateApproval.value =
                    ViewLeaveListState.NetworkError(
                        e.message
                            ?: "Please check your internet connection"
                    )

            } catch (e: Exception) {

                FirebaseCrashlytics
                    .getInstance()
                    .recordException(e)

                _viewLeaveListStateApproval.value =
                    ViewLeaveListState.ApiError(
                        e.message
                            ?: "Something went wrong"
                    )
            }
        }
    }

    /*======================================================================================*/
    /* Leave Approval Update Unnati */
    /*======================================================================================*/

    private val _leaveApprovalUpdateState =
        MutableLiveData<LeaveApprovalUpdateState>(
            LeaveApprovalUpdateState.Idle
        )

    val leaveApprovalUpdateState:
             LiveData<LeaveApprovalUpdateState>
            = _leaveApprovalUpdateState

    fun viewLeaveListApprovalUpdateUnnati(
        request: ViewLeaveApprovalUpdateUnnatiRequest
    ) {

        _leaveApprovalUpdateState.value =
            LeaveApprovalUpdateState.Loading

        viewModelScope.launch {

            try {

                val response =
                    repository.viewLeaveListApprovalUpdateUnnati(
                        request
                    )

                // =========================
                // API FAILURE
                // =========================

                if (!response.isSuccessful) {

                    _leaveApprovalUpdateState.value =
                        LeaveApprovalUpdateState.ApiError(
                            "Server error : ${response.code()}"
                        )

                    return@launch
                }

                val body = response.body()

                // =========================
                // EMPTY BODY
                // =========================

                if (body == null) {

                    _leaveApprovalUpdateState.value =
                        LeaveApprovalUpdateState.ApiError(
                            "Empty server response"
                        )

                    return@launch
                }

                // =========================
                // STATUS HANDLE
                // =========================

                when (body.status) {

                    // SUCCESS
                    "200" -> {

                        _leaveApprovalUpdateState.value =
                            LeaveApprovalUpdateState.Success(
                                body
                            )
                    }

                    // BUSINESS ERROR
                    "209" -> {

                        _leaveApprovalUpdateState.value =
                            LeaveApprovalUpdateState.ApiError(
                                body.message
                                    ?: "No record found"
                            )
                    }

                    // OTHER ERROR
                    else -> {

                        _leaveApprovalUpdateState.value =
                            LeaveApprovalUpdateState.ApiError(
                                body.message
                                    ?: "Something went wrong"
                            )
                    }
                }

            } catch (e: IOException) {

                _leaveApprovalUpdateState.value =
                    LeaveApprovalUpdateState.NetworkError(
                        e.message
                            ?: "Something went wrong"
                    )

            } catch (e: Exception) {

                FirebaseCrashlytics
                    .getInstance()
                    .recordException(e)

                _leaveApprovalUpdateState.value =
                    LeaveApprovalUpdateState.ApiError(
                        e.message
                            ?: "Something went wrong"
                    )
            }
        }
    }

}

