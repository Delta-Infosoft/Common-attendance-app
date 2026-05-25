package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.PjcPermissionResult

sealed class PjcPermissionUiState {

    object Idle : PjcPermissionUiState()
    object Loading : PjcPermissionUiState()
    data class Success(val data: PjcPermissionResult) : PjcPermissionUiState()
    data class Empty(val message: String) : PjcPermissionUiState()
    data class ApiError(val message: String) : PjcPermissionUiState()
    data class NetworkError(val message: String) : PjcPermissionUiState()
}