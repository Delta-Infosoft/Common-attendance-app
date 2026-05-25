package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.TourAgendaTrackingObjectiveItem

sealed class GetObjectiveUiState {
    object Idle    : GetObjectiveUiState()
    object Loading : GetObjectiveUiState()
    data class Success(val list: List<TourAgendaTrackingObjectiveItem>) : GetObjectiveUiState()
    data class ApiError(val message: String)                            : GetObjectiveUiState()
    data class NetworkError(val message: String)                        : GetObjectiveUiState()
}