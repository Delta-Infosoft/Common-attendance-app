package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.TourVoucherItem

sealed class TourVoucherUiState {
    object Idle : TourVoucherUiState()
    object Loading : TourVoucherUiState()
    data class Success(val list: List<TourVoucherItem>) : TourVoucherUiState()
    data class ApiError(val message: String) : TourVoucherUiState()
    data class NetworkError(val message: String) : TourVoucherUiState()
}