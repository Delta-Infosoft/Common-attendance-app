package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.UserRightsItem

sealed class GetUserRightsUiState {
    object Idle    : GetUserRightsUiState()
    object Loading : GetUserRightsUiState()
    data class Success(val list: List<UserRightsItem>) : GetUserRightsUiState()
    data class ApiError(val message: String)           : GetUserRightsUiState()
    data class NetworkError(val message: String)       : GetUserRightsUiState()
}