package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.response.EmployeeDataDuke

sealed class GetEmpDataUiState {
    object Idle    : GetEmpDataUiState()
    object Loading : GetEmpDataUiState()
    data class Success(val list: List<EmployeeDataDuke>) : GetEmpDataUiState()
    data class ApiError(val message: String)             : GetEmpDataUiState()
    data class NetworkError(val message: String)         : GetEmpDataUiState()
}