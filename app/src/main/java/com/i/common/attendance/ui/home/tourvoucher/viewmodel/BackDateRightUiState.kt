package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.PjcDateModel

sealed class BackDateRightUiState {

    object Idle : BackDateRightUiState()

    object Loading : BackDateRightUiState()

    data class Success(
        val data: List<PjcDateModel>
    ) : BackDateRightUiState()

    data class Empty(
        val message: String
    ) : BackDateRightUiState()

    data class ApiError(
        val message: String
    ) : BackDateRightUiState()

    data class NetworkError(
        val message: String
    ) : BackDateRightUiState()
}