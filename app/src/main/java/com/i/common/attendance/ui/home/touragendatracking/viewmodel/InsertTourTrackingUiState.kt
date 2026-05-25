package com.i.common.attendance.ui.home.touragendatracking.viewmodel

sealed class InsertTourTrackingUiState {
    object Idle    : InsertTourTrackingUiState()
    object Loading : InsertTourTrackingUiState()
    data class Success(val message: String)      : InsertTourTrackingUiState()
    data class ApiError(val message: String)     : InsertTourTrackingUiState()
    data class NetworkError(val message: String) : InsertTourTrackingUiState()
}