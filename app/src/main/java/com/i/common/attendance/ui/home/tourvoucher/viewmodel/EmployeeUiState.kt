package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.EmployeeModel

sealed class EmployeeUiState {
    object Idle : EmployeeUiState()
    object Loading : EmployeeUiState()

    data class Success(
        val list: List<EmployeeModel>
    ) : EmployeeUiState()

    data class ApiError(
        val message: String
    ) : EmployeeUiState()

    data class NetworkError(
        val message: String
    ) : EmployeeUiState()

    data class UnknownError(
        val throwable: Throwable
    ) : EmployeeUiState()
}