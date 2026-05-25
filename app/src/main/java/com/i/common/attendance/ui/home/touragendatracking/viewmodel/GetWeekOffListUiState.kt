package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.WeekOffItem

sealed class GetWeekOffListUiState {
    object Idle    : GetWeekOffListUiState()
    object Loading : GetWeekOffListUiState()
    data class Success(val list: List<WeekOffItem>) : GetWeekOffListUiState()
    data class ApiError(val message: String)        : GetWeekOffListUiState()
    data class NetworkError(val message: String)    : GetWeekOffListUiState()
}