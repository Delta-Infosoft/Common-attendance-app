package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.ValidateSundayResult

sealed class ValidateSundayUiState {
    object Idle    : ValidateSundayUiState()
    object Loading : ValidateSundayUiState()
    data class Success(val list: List<ValidateSundayResult>) : ValidateSundayUiState()
    data class ApiError(val message: String)                 : ValidateSundayUiState()
    data class NetworkError(val message: String)             : ValidateSundayUiState()
}