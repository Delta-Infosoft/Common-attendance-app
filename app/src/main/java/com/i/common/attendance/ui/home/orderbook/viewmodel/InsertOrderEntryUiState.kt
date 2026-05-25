package com.i.common.attendance.ui.home.orderbook.viewmodel

sealed class InsertOrderEntryUiState {
    object Idle    : InsertOrderEntryUiState()
    object Loading : InsertOrderEntryUiState()
    data class Success(val message: String)      : InsertOrderEntryUiState()
    data class ApiError(val message: String)     : InsertOrderEntryUiState()
    data class NetworkError(val message: String) : InsertOrderEntryUiState()
}