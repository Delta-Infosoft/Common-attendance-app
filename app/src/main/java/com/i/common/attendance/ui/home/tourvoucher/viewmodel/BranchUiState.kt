package com.i.common.attendance.ui.home.tourvoucher.viewmodel

sealed class BranchUiState {
    object Idle : BranchUiState()
    object Loading : BranchUiState()
    data class Success(val list: List<String>) : BranchUiState()
    data class ApiError(val message: String) : BranchUiState()
    data class NetworkError(val message: String) : BranchUiState()
}