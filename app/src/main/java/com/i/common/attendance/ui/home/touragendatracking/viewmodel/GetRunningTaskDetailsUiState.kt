package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.TourAgendaTrackingRunningTaskDetails

sealed class GetRunningTaskDetailsUiState {
    object Idle    : GetRunningTaskDetailsUiState()
    object Loading : GetRunningTaskDetailsUiState()
    data class Success(val list: List<TourAgendaTrackingRunningTaskDetails>) : GetRunningTaskDetailsUiState()
    data class ApiError(val message: String)            : GetRunningTaskDetailsUiState()
    data class NetworkError(val message: String)        : GetRunningTaskDetailsUiState()
}