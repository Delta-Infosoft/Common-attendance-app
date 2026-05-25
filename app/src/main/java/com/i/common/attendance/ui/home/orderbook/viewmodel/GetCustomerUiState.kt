package com.i.common.attendance.ui.home.orderbook.viewmodel

import com.i.common.attendance.network.response.CustomerModel

sealed class GetCustomerUiState {
    object Idle    : GetCustomerUiState()
    object Loading : GetCustomerUiState()
    data class Success(val list: List<CustomerModel>) : GetCustomerUiState()
    data class ApiError(val message: String)          : GetCustomerUiState()
    data class NetworkError(val message: String)      : GetCustomerUiState()
}