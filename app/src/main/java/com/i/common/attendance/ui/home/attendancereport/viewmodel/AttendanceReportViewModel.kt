package com.i.common.attendance.ui.home.attendancereport.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.network.response.MonthList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AttendanceReportViewModel @Inject constructor(
    private val repository: AttendanceReportRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    /*===============================================*/
    private val _monthListState = MutableLiveData<MonthUiState>(MonthUiState.Idle)
    val monthListState: LiveData<MonthUiState> = _monthListState
    private var cachedMonthList: List<MonthList>? = null

    fun loadMonthList() {

        // ✅ Prevent duplicate API calls
        if (cachedMonthList != null) {
            _monthListState.value = MonthUiState.Success(cachedMonthList!!)
            return
        }

        _monthListState.value = MonthUiState.Loading

        viewModelScope.launch {
            try {

                val response = repository.getMonthAPI()

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

}