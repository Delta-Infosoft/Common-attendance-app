package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.response.GetDivision

sealed class DivisionUiState {

    object Idle : DivisionUiState()

    object Loading : DivisionUiState()

    data class Success(
        val list: List<GetDivision>
    ) : DivisionUiState()

    data class ApiError(
        val message: String
    ) : DivisionUiState()

    data class NetworkError(
        val message: String
    ) : DivisionUiState()
}