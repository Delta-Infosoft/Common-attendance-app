package com.i.common.attendance.ui.home.carairapproval.viewmodel

sealed class InsertCarApprovalUiState {
    object Idle    : InsertCarApprovalUiState()
    object Loading : InsertCarApprovalUiState()
    data class Success(val message: String)      : InsertCarApprovalUiState()
    data class ApiError(val message: String)     : InsertCarApprovalUiState()
    data class NetworkError(val message: String) : InsertCarApprovalUiState()
}