package com.i.common.attendance.ui.home.newcustomerdealer.viewmodel

sealed class InsertVisitUiState {

    object Idle : InsertVisitUiState()
    object Loading : InsertVisitUiState()

    data class Success(val message: String) : InsertVisitUiState()

    data class ApiError(val message: String) : InsertVisitUiState()

    data class NetworkError(val message: String) : InsertVisitUiState()
}