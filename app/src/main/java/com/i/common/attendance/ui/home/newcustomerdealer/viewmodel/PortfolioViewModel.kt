package com.i.common.attendance.ui.home.newcustomerdealer.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.network.request.InsertVisitRequest
import com.i.common.attendance.network.request.SelectPortfolioRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: PortfolioRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectPortfolioState = MutableLiveData<SelectPortfolioUiState>(SelectPortfolioUiState.Idle)
    val selectPortfolioState: LiveData<SelectPortfolioUiState> = _selectPortfolioState

    fun getSelectPortfolio(id: String) {

        _selectPortfolioState.value = SelectPortfolioUiState.Loading

        viewModelScope.launch {
            try {

                val response = repository.getSelectPortFolio(SelectPortfolioRequest(id))

                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _selectPortfolioState.value = SelectPortfolioUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = body.result ?: emptyList()

                        if (list.isEmpty()) {
                            _selectPortfolioState.value = SelectPortfolioUiState.ApiError(body.message ?: "No data found")
                        } else {
                            _selectPortfolioState.value = SelectPortfolioUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _selectPortfolioState.value = SelectPortfolioUiState.ApiError(body.message ?: "No record found")
                    }

                    else -> {
                        _selectPortfolioState.value = SelectPortfolioUiState.ApiError(body.message ?: "Server error")
                    }
                }

            } catch (e: IOException) {
                _selectPortfolioState.value = SelectPortfolioUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _selectPortfolioState.value = SelectPortfolioUiState.ApiError("Something went wrong")
            }
        }
    }

    /*===============================================*/
    private val _insertVisitState = MutableLiveData<InsertVisitUiState>(InsertVisitUiState.Idle)
    val insertVisitState: LiveData<InsertVisitUiState> = _insertVisitState

    fun insertVisit(request: InsertVisitRequest) {

        _insertVisitState.value = InsertVisitUiState.Loading

        viewModelScope.launch {

            try {

                val response = repository.insertVisit(request, context = context)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _insertVisitState.value = InsertVisitUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        _insertVisitState.value = InsertVisitUiState.Success(body.message ?: "Data Saved")
                    }

                    "209" -> {
                        _insertVisitState.value = InsertVisitUiState.ApiError(body.message ?: "No record found")
                    }

                    else -> {
                        _insertVisitState.value = InsertVisitUiState.ApiError(body.message ?: "Server error")
                    }
                }

            } catch (e: IOException) {
                _insertVisitState.value = InsertVisitUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertVisitState.value = InsertVisitUiState.ApiError("Something went wrong")
            }
        }
    }
    /*===============================================*/
    private val _updateVisitState = MutableLiveData<UpdateVisitUiState>(UpdateVisitUiState.Idle)
    val updateVisitState: LiveData<UpdateVisitUiState> = _updateVisitState

    fun updateVisit(request: InsertVisitRequest) {

        _updateVisitState.value = UpdateVisitUiState.Loading

        viewModelScope.launch {

            try {

                val response = repository.updatePortFolioAPI(request, context = context)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _updateVisitState.value = UpdateVisitUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        _updateVisitState.value = UpdateVisitUiState.Success(body.message ?: "Data Updated")
                    }

                    "209" -> {
                        _updateVisitState.value = UpdateVisitUiState.ApiError(body.message ?: "No record found")
                    }

                    else -> {
                        _updateVisitState.value = UpdateVisitUiState.ApiError(body.message ?: "Server error")
                    }
                }

            } catch (e: IOException) {
                _updateVisitState.value = UpdateVisitUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _updateVisitState.value = UpdateVisitUiState.ApiError("Something went wrong")
            }
        }
    }

}
