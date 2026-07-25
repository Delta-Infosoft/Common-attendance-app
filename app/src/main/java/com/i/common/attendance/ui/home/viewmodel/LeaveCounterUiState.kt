package com.i.common.attendance.ui.home.viewmodel

import com.i.common.attendance.network.response.LeaveCounter

sealed class LeaveCounterUiState {

    object Idle : LeaveCounterUiState()

    object Loading : LeaveCounterUiState()

    data class Success(
        val list: List<LeaveCounter>
    ) : LeaveCounterUiState()

    data class ApiError(
        val message: String
    ) : LeaveCounterUiState()

    data class NetworkError(
        val message: String
    ) : LeaveCounterUiState()
}