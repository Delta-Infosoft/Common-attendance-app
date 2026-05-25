package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.DistrictTourAgendaTracking

sealed class GetDistrictUiState {
    object Idle    : GetDistrictUiState()
    object Loading : GetDistrictUiState()
    data class Success(val list: List<DistrictTourAgendaTracking>) : GetDistrictUiState()
    data class ApiError(val message: String)                           : GetDistrictUiState()
    data class NetworkError(val message: String)                       : GetDistrictUiState()
}