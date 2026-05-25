package com.i.common.attendance.ui.home.myportfolio.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.network.request.EmployeeRequest
import com.i.common.attendance.network.request.ViewPortFolioRequest
import com.i.common.attendance.network.response.EmployeeModel
import com.i.common.attendance.network.response.ViewPortFolioModel
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.EmployeeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MyPortfolioViewModel @Inject constructor(
    private val repository: MyPortfolioRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _employeeState = MutableLiveData<EmployeeUiState>(EmployeeUiState.Idle)
    val employeeState: LiveData<EmployeeUiState> = _employeeState
    private var cachedEmployeeList: List<EmployeeModel>? = null

    fun loadEmployeeList(request: EmployeeRequest) {

        // ✅ Prevent duplicate API calls
        if (cachedEmployeeList != null) {
            _employeeState.value = EmployeeUiState.Success(cachedEmployeeList!!)
            return
        }

        _employeeState.value = EmployeeUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getEmployeeParam(request)
                val body = response.body()

                // ❌ HTTP / Server error
                if (!response.isSuccessful || body == null) {
                    _employeeState.value = EmployeeUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                // ❌ API status handling
                when (body.status) {

                    "200" -> {
                        val list = if (body.result is List<*>) {
                            try {
                                body.result.filterIsInstance<EmployeeModel>()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } else {
                            emptyList()
                        }

                        if (list.isEmpty()) {
                            _employeeState.value = EmployeeUiState.ApiError(body.message ?: "No employee found")
                            return@launch
                        }

                        // ✅ Cache result
                        cachedEmployeeList = list
                        _employeeState.value = EmployeeUiState.Success(list)
                    }

                    "209" -> {
                        _employeeState.value = EmployeeUiState.ApiError(body.message ?: "No record found")
                    }

                    else -> {
                        _employeeState.value = EmployeeUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _employeeState.value = EmployeeUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _employeeState.value = EmployeeUiState.ApiError("Something went wrong")
            }
        }
    }
    fun getCachedEmployeeList(): List<EmployeeModel>? = cachedEmployeeList
/*===============================================================================================*/
    private val _viewPortFolioState = MutableLiveData<GetViewPortFolioState>(GetViewPortFolioState.Idle)
    val viewPortFolioState: LiveData<GetViewPortFolioState> = _viewPortFolioState
    private var cachedPortfolioList: List<ViewPortFolioModel>? = null

    fun getViewPortFolioAPI(mobileNo: String) {

        // ✅ Prevent duplicate API call
        if (cachedPortfolioList != null) {
            _viewPortFolioState.value =
                GetViewPortFolioState.Success(cachedPortfolioList!!)
            return
        }

        _viewPortFolioState.value =
            GetViewPortFolioState.Loading

        viewModelScope.launch {

            try {

                val response = repository.getViewPortFolio(
                    ViewPortFolioRequest(
                        mobileNo = mobileNo
                    )
                )

                val body = response.body()

                // ❌ HTTP Error
                if (!response.isSuccessful || body == null) {
                    _viewPortFolioState.value =
                        GetViewPortFolioState.Error(
                            "Server error : ${response.code()}"
                        )
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = body.result ?: emptyList()

                        if (list.isEmpty()) {
                            _viewPortFolioState.value = GetViewPortFolioState.Empty(body.message ?: "No customer found")
                            return@launch
                        }

                        // ✅ Cache result
                        cachedPortfolioList = list
                        _viewPortFolioState.value = GetViewPortFolioState.Success(list)
                    }

                    "209" -> {
                        _viewPortFolioState.value = GetViewPortFolioState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _viewPortFolioState.value = GetViewPortFolioState.Error(body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _viewPortFolioState.value = GetViewPortFolioState.Error("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _viewPortFolioState.value = GetViewPortFolioState.Error("Something went wrong")
            }
        }
    }
}
