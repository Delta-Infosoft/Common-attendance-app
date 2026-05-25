package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.SundayRequestItem

sealed class GetSundayRequestListUiState {
    object Idle    : GetSundayRequestListUiState()
    object Loading : GetSundayRequestListUiState()
    data class Success(val list: List<SundayRequestItem>) : GetSundayRequestListUiState()
    data class ApiError(val message: String)              : GetSundayRequestListUiState()
    data class NetworkError(val message: String)          : GetSundayRequestListUiState()
}