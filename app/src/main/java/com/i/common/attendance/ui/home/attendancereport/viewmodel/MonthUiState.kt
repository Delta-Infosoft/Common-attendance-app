package com.i.common.attendance.ui.home.attendancereport.viewmodel

import com.i.common.attendance.network.response.MonthList

sealed class MonthUiState {
    object Idle : MonthUiState()
    object Loading : MonthUiState()
    data class Success(val data: List<MonthList>) : MonthUiState()
    data class ApiError(val message: String) : MonthUiState()
    data class NetworkError(val message: String) : MonthUiState()
}