package com.i.common.attendance.ui.home.dailytour.viewmodel

sealed class InsertDailyTourFlotechUiState {
    object Idle    : InsertDailyTourFlotechUiState()
    object Loading : InsertDailyTourFlotechUiState()
    data class Success(val message: String)      : InsertDailyTourFlotechUiState()
    data class ApiError(val message: String)     : InsertDailyTourFlotechUiState()
    data class NetworkError(val message: String) : InsertDailyTourFlotechUiState()
}