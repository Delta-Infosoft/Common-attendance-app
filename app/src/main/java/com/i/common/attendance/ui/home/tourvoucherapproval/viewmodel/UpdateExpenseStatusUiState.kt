package com.i.common.attendance.ui.home.tourvoucherapproval.viewmodel

sealed class UpdateExpenseStatusUiState {
    object Idle : UpdateExpenseStatusUiState()
    object Loading : UpdateExpenseStatusUiState()
    data class Success(val message: String) : UpdateExpenseStatusUiState()
    data class ApiError(val message: String) : UpdateExpenseStatusUiState()
    data class Error(val message: String) : UpdateExpenseStatusUiState()
    data class NetworkError(val message: String) : UpdateExpenseStatusUiState()
}