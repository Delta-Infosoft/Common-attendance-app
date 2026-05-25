package com.i.common.attendance.ui.home.carairapproval.viewmodel

import com.i.common.attendance.network.response.CarAirApprovalItem

sealed class GetCarAirApprovalListUiState {
    object Idle    : GetCarAirApprovalListUiState()
    object Loading : GetCarAirApprovalListUiState()
    data class Success(val list: List<CarAirApprovalItem>) : GetCarAirApprovalListUiState()
    data class ApiError(val message: String)               : GetCarAirApprovalListUiState()
    data class NetworkError(val message: String)           : GetCarAirApprovalListUiState()
}