package com.i.common.attendance.ui.home.tourvoucherapproval.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.i.common.attendance.network.request.TourVoucherApprovalListUpdateStatusRequest
import com.i.common.attendance.network.request.TourVoucherRequest
import com.i.common.attendance.network.response.TourVoucherItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TourVoucherApprovalViewModel @Inject constructor(private val repository: TourVoucherApprovalRepository, @ApplicationContext private val context: Context) : ViewModel() {
    private val _tourVoucherState = MutableLiveData<TourVoucherUiState>(TourVoucherUiState.Idle)
    val tourVoucherState: LiveData<TourVoucherUiState> = _tourVoucherState
    fun loadTourVoucherList(request: TourVoucherRequest) {
        _tourVoucherState.value = TourVoucherUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getTourVoucherApprovalListAPI(request)
                if (!response.isSuccessful) {
                    _tourVoucherState.value = TourVoucherUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _tourVoucherState.value = TourVoucherUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = when (val res = body.result) {

                            is List<*> -> {
                                res.mapNotNull { item ->
                                    (item as? LinkedTreeMap<*, *>)?.let {
                                        Gson().fromJson(
                                            Gson().toJson(it),
                                            TourVoucherItem::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }
                        if (list.isEmpty()) {
                            _tourVoucherState.value = TourVoucherUiState.ApiError(body.message ?: "No tour voucher found")
                        } else {
                            _tourVoucherState.value = TourVoucherUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _tourVoucherState.value = TourVoucherUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _tourVoucherState.value = TourVoucherUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _tourVoucherState.value = TourVoucherUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _tourVoucherState.value = TourVoucherUiState.ApiError("Something went wrong")
            }
        }
    }
    /*========================================================================================*/
    private val _updateExpenseStatusState = MutableLiveData<UpdateExpenseStatusUiState>(UpdateExpenseStatusUiState.Idle)
    val updateExpenseStatusState: LiveData<UpdateExpenseStatusUiState> = _updateExpenseStatusState
    fun updateExpenseStatus(request: TourVoucherApprovalListUpdateStatusRequest) {
        _updateExpenseStatusState.value = UpdateExpenseStatusUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.updateExpenseStatus(request)
                if (!response.isSuccessful) {
                    _updateExpenseStatusState.value = UpdateExpenseStatusUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _updateExpenseStatusState.value = UpdateExpenseStatusUiState.ApiError("Empty server response")
                    return@launch
                }
                when (body.status) {
                    "200" -> {
                        _updateExpenseStatusState.value = UpdateExpenseStatusUiState.Success(body.message ?: "Status updated successfully")
                    }

                    "209" -> {
                        _updateExpenseStatusState.value = UpdateExpenseStatusUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _updateExpenseStatusState.value = UpdateExpenseStatusUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _updateExpenseStatusState.value = UpdateExpenseStatusUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _updateExpenseStatusState.value = UpdateExpenseStatusUiState.ApiError("Something went wrong")
            }
        }
    }

}