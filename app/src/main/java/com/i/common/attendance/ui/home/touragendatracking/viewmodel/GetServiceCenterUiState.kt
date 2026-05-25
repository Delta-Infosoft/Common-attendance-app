package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.TourAgendaTrackingServiceCenter

sealed class GetServiceCenterUiState {
    object Idle    : GetServiceCenterUiState()
    object Loading : GetServiceCenterUiState()
    data class Success(val list: List<TourAgendaTrackingServiceCenter>) : GetServiceCenterUiState()
    data class ApiError(val message: String)                            : GetServiceCenterUiState()
    data class NetworkError(val message: String)                        : GetServiceCenterUiState()
}