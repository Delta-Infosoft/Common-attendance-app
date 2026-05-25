package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.InOutRecords

sealed class GetInOutDetailsUiState {
    object Idle    : GetInOutDetailsUiState()
    object Loading : GetInOutDetailsUiState()
    data class Success(val list: List<InOutRecords>) : GetInOutDetailsUiState()
    data class ApiError(val message: String)         : GetInOutDetailsUiState()
    data class NetworkError(val message: String)     : GetInOutDetailsUiState()
}