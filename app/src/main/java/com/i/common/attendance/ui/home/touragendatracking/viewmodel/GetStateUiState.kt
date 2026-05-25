package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.GetState

sealed class GetStateUiState {
    object Idle    : GetStateUiState()
    object Loading : GetStateUiState()
    data class Success(val list: List<GetState>) : GetStateUiState()
    data class ApiError(val message: String)     : GetStateUiState()
    data class NetworkError(val message: String) : GetStateUiState()
}