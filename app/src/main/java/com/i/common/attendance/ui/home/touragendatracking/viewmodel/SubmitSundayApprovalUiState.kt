package com.i.common.attendance.ui.home.touragendatracking.viewmodel

sealed class SubmitSundayApprovalUiState {
    object Idle    : SubmitSundayApprovalUiState()
    object Loading : SubmitSundayApprovalUiState()
    data class Success(val message: String)      : SubmitSundayApprovalUiState()
    data class ApiError(val message: String)     : SubmitSundayApprovalUiState()
    data class NetworkError(val message: String) : SubmitSundayApprovalUiState()
}