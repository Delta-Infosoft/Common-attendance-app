package com.i.common.attendance.ui.home.tourvoucher.viewmodel

sealed class SaveTourVoucherUiState {
    object Idle : SaveTourVoucherUiState()
    object Loading : SaveTourVoucherUiState()
    data class Success(val message: String) : SaveTourVoucherUiState()
    data class ApiError(val message: String) : SaveTourVoucherUiState()
    data class NetworkError(val message: String) : SaveTourVoucherUiState()
}