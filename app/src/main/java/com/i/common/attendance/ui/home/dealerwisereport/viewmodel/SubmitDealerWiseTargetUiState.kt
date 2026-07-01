package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

sealed class SubmitDealerWiseTargetUiState {
    object Idle : SubmitDealerWiseTargetUiState()
    object Loading : SubmitDealerWiseTargetUiState()
    data class Success(val message: String) : SubmitDealerWiseTargetUiState()
    data class ApiError(val message: String) : SubmitDealerWiseTargetUiState()
    data class NetworkError(val message: String) : SubmitDealerWiseTargetUiState()
}