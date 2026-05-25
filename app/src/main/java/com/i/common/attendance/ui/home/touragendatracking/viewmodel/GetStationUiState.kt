package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.BusinessCenterName

sealed class GetStationUiState {
    object Idle    : GetStationUiState()
    object Loading : GetStationUiState()
    data class Success(val list: List<BusinessCenterName>) : GetStationUiState()
    data class ApiError(val message: String)                   : GetStationUiState()
    data class NetworkError(val message: String)               : GetStationUiState()
}