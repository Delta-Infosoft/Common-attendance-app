package com.i.common.attendance.ui.home.touragendatracking.viewmodel

sealed class SubmitWeekOffUiState {
    object Idle    : SubmitWeekOffUiState()
    object Loading : SubmitWeekOffUiState()
    data class Success(val message: String)      : SubmitWeekOffUiState()
    data class ApiError(val message: String)     : SubmitWeekOffUiState()
    data class NetworkError(val message: String) : SubmitWeekOffUiState()
}