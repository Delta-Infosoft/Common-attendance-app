package com.i.common.attendance.ui.home.touragendatracking.viewmodel

sealed class InsertJtdDetailsUiState {
    object Idle    : InsertJtdDetailsUiState()
    object Loading : InsertJtdDetailsUiState()
    data class Success(val message: String)      : InsertJtdDetailsUiState()
    data class ApiError(val message: String)     : InsertJtdDetailsUiState()
    data class NetworkError(val message: String) : InsertJtdDetailsUiState()
}