package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.response.TravelData

sealed class GetCityTypeUiState {
    object Idle    : GetCityTypeUiState()
    object Loading : GetCityTypeUiState()
    data class Success(val list: List<TravelData>) : GetCityTypeUiState()
    data class ApiError(val message: String)       : GetCityTypeUiState()
    data class NetworkError(val message: String)   : GetCityTypeUiState()
}