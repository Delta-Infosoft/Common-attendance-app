package com.i.common.attendance.ui.home.orderbook.viewmodel

import com.i.common.attendance.network.response.RateModel

sealed class GetRateUiState {
    object Idle    : GetRateUiState()
    object Loading : GetRateUiState()
    data class Success(val list: List<RateModel>) : GetRateUiState()
    data class ApiError(val message: String)      : GetRateUiState()
    data class NetworkError(val message: String)  : GetRateUiState()
}