package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.TravelingByItem

sealed class TravelingByUiState {
    object Idle : TravelingByUiState()
    object Loading : TravelingByUiState()
    data class Success(val list: List<TravelingByItem>) : TravelingByUiState()
    data class ApiError(val message: String) : TravelingByUiState()
    data class NetworkError(val message: String) : TravelingByUiState()
}