package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.response.CustomerData

sealed class CustomerUiState {

    object Idle : CustomerUiState()

    object Loading : CustomerUiState()

    data class Success(
        val list: List<CustomerData>
    ) : CustomerUiState()

    data class ApiError(
        val message: String
    ) : CustomerUiState()

    data class NetworkError(
        val message: String
    ) : CustomerUiState()
}