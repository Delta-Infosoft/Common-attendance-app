package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.DealerDetailsAccountRequest
import com.i.common.attendance.network.request.GetMonthForTargetRequest
import com.i.common.attendance.network.response.DealerDetailsAccount
import com.i.common.attendance.network.response.DealerWiseTarget
import com.i.common.attendance.network.response.MonthList
import com.i.common.attendance.ui.home.attendancereport.viewmodel.MonthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DealerWiseTargetViewModel @Inject constructor(private val repository: DealerWiseTargetEntryRepository, @ApplicationContext private val context: Context) : ViewModel() {

    private val _dealerState = MutableLiveData<DealerDetailsUiState>(DealerDetailsUiState.Idle)
    val dealerState: LiveData<DealerDetailsUiState> = _dealerState
    private var cachedDealerList: List<DealerDetailsAccount>? = null
    fun getCachedDealerList(): List<DealerDetailsAccount>? = cachedDealerList
    fun loadDealerList(request: DealerDetailsAccountRequest) {

        _dealerState.value = DealerDetailsUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getDealerDetailsAccount(request)
                if (!response.isSuccessful) {
                    _dealerState.value = DealerDetailsUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _dealerState.value = DealerDetailsUiState.ApiError("Empty server response")
                    return@launch
                }
                when (body.status) {

                    "200" -> {

                        val list = if (body.result != null && body.result!!.isJsonArray) {
                            Gson().fromJson<List<DealerDetailsAccount>>(
                                body.result!!.asJsonArray,
                                object : TypeToken<List<DealerDetailsAccount>>() {}.type
                            )
                        } else {
                            emptyList()
                        }

                        if (list.isEmpty()) {
                            _dealerState.value = DealerDetailsUiState.ApiError(body.message ?: "No dealer found")
                        } else {
                            cachedDealerList = list
                            _dealerState.value = DealerDetailsUiState.Success(list)
                        }
                    }

                    "209" -> {
                        cachedDealerList = emptyList()
                        _dealerState.value = DealerDetailsUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _dealerState.value = DealerDetailsUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _dealerState.value = DealerDetailsUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dealerState.value = DealerDetailsUiState.ApiError("Something went wrong")
            }
        }
    }

    /*==========================================================================================*/
    private val _monthListState = MutableLiveData<MonthUiState>(MonthUiState.Idle)
    val monthListState: LiveData<MonthUiState> = _monthListState
    private var cachedMonthList: List<MonthList>? = null
    fun loadMonthList(request: GetMonthForTargetRequest) {
        // ✅ Prevent duplicate API calls
        if (cachedMonthList != null) {
            _monthListState.value = MonthUiState.Success(cachedMonthList!!)
            return
        }
        _monthListState.value = MonthUiState.Loading
        viewModelScope.launch {
            try {

                val response = repository.getMonthAPI(request)

                if (!response.isSuccessful) {
                    _monthListState.value =
                        MonthUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _monthListState.value = MonthUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = body.result.filterNotNull()

                        if (list.isEmpty()) {
                            _monthListState.value =
                                MonthUiState.ApiError(body.message ?: "No months found")
                            return@launch
                        }

                        // ✅ Cache result
                        cachedMonthList = list
                        _monthListState.value = MonthUiState.Success(list)
                    }

                    "209" -> {
                        _monthListState.value =
                            MonthUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _monthListState.value =
                            MonthUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _monthListState.value =
                    MonthUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _monthListState.value = MonthUiState.ApiError("Something went wrong")
            }
        }
    }
    fun getCachedMonthList(): List<MonthList>? = cachedMonthList

    /*==========================================================================================*/
    private val _dealerWiseTargetState = MutableLiveData<DealerWiseTargetUiState>(DealerWiseTargetUiState.Idle)
    val dealerWiseTargetState: LiveData<DealerWiseTargetUiState> = _dealerWiseTargetState
    fun loadDealerWiseTargetType() {
        _dealerWiseTargetState.value = DealerWiseTargetUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.dealerWiseTargetType()

                if (!response.isSuccessful) {
                    _dealerWiseTargetState.value = DealerWiseTargetUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _dealerWiseTargetState.value = DealerWiseTargetUiState.ApiError("Empty server response")
                    return@launch
                }
                when (body.status) {

                    "200" -> {
                        val list = if (body.result != null && body.result!!.isJsonArray) {
                            Gson().fromJson<List<DealerWiseTarget>>(body.result!!.asJsonArray, object : TypeToken<List<DealerWiseTarget>>() {}.type)
                        } else {
                            emptyList()
                        }

                        if (list.isEmpty()) {
                            _dealerWiseTargetState.value = DealerWiseTargetUiState.ApiError(body.message ?: "No Record Found")
                        } else {
                            _dealerWiseTargetState.value = DealerWiseTargetUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _dealerWiseTargetState.value = DealerWiseTargetUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _dealerWiseTargetState.value = DealerWiseTargetUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _dealerWiseTargetState.value = DealerWiseTargetUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dealerWiseTargetState.value = DealerWiseTargetUiState.ApiError("Something went wrong")
            }
        }
    }

}