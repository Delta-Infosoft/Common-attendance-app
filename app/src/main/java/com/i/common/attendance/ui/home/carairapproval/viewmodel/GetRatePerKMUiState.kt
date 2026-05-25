package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.response.GetRatePerKM

sealed class GetRatePerKMUiState {
    object Idle    : GetRatePerKMUiState()
    object Loading : GetRatePerKMUiState()
    data class Success(val list: List<GetRatePerKM>) : GetRatePerKMUiState()
    data class ApiError(val message: String)         : GetRatePerKMUiState()
    data class NetworkError(val message: String)     : GetRatePerKMUiState()
}