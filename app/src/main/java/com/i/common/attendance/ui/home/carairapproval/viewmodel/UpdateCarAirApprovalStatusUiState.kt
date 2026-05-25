package com.i.common.attendance.ui.home.carairapproval.viewmodel

sealed class UpdateCarAirApprovalStatusUiState {
    object Idle    : UpdateCarAirApprovalStatusUiState()
    object Loading : UpdateCarAirApprovalStatusUiState()
    data class Success(val message: String)      : UpdateCarAirApprovalStatusUiState()
    data class ApiError(val message: String)     : UpdateCarAirApprovalStatusUiState()
    data class NetworkError(val message: String) : UpdateCarAirApprovalStatusUiState()
}