package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.response.TravelData
import com.i.common.attendance.network.response.TravelingByItem

sealed class GetTravellingByCarUiState {
    object Idle    : GetTravellingByCarUiState()
    object Loading : GetTravellingByCarUiState()
    data class Success(val list: List<TravelingByItem>) : GetTravellingByCarUiState()
    data class ApiError(val message: String)       : GetTravellingByCarUiState()
    data class NetworkError(val message: String)   : GetTravellingByCarUiState()
}