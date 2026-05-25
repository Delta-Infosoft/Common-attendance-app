package com.i.common.attendance.ui.home.newcustomerdealer.viewmodel

sealed class UpdateVisitUiState {
    object Idle : UpdateVisitUiState()
    object Loading : UpdateVisitUiState()
    data class Success(val message: String) : UpdateVisitUiState()
    data class ApiError(val message: String) : UpdateVisitUiState()
    data class NetworkError(val message: String) : UpdateVisitUiState()
}