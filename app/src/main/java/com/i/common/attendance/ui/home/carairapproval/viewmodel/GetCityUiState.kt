package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.response.GetCities

sealed class GetCityUiState {
    object Idle    : GetCityUiState()
    object Loading : GetCityUiState()
    data class Success(val list: List<GetCities>) : GetCityUiState()
    data class ApiError(val message: String)      : GetCityUiState()
    data class NetworkError(val message: String)  : GetCityUiState()
}