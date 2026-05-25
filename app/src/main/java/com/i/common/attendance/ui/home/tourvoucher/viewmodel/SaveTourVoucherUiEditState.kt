package com.i.common.attendance.ui.home.tourvoucher.viewmodel

sealed class SaveTourVoucherUiEditState {
    object Idle : SaveTourVoucherUiEditState()
    object Loading : SaveTourVoucherUiEditState()
    data class Success(val message: String) : SaveTourVoucherUiEditState()
    data class ApiError(val message: String) : SaveTourVoucherUiEditState()
    data class NetworkError(val message: String) : SaveTourVoucherUiEditState()
}