package com.i.common.attendance.ui.home.carairapproval.viewmodel

sealed class GetVoucherNoUiState {
    object Idle    : GetVoucherNoUiState()
    object Loading : GetVoucherNoUiState()
    data class Success(val voucherNo: String)    : GetVoucherNoUiState()
    data class ApiError(val message: String)     : GetVoucherNoUiState()
    data class NetworkError(val message: String) : GetVoucherNoUiState()
}