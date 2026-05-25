package com.i.common.attendance.ui.home.touragendatracking.viewmodel

sealed class InsertObjectiveUiState {
    object Idle    : InsertObjectiveUiState()
    object Loading : InsertObjectiveUiState()
    data class Success(val message: String)      : InsertObjectiveUiState()
    data class ApiError(val message: String)     : InsertObjectiveUiState()
    data class NetworkError(val message: String) : InsertObjectiveUiState()
}