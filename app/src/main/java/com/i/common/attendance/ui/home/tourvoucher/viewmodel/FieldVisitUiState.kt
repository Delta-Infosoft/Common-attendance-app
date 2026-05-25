package com.i.common.attendance.ui.home.tourvoucher.viewmodel

sealed class FieldVisitUiState {
    object Idle : FieldVisitUiState()
    object Loading : FieldVisitUiState()
    data class Success(val message: String) : FieldVisitUiState()
    data class ApiError(val message: String) : FieldVisitUiState()
    data class NetworkError(val message: String) : FieldVisitUiState()
}