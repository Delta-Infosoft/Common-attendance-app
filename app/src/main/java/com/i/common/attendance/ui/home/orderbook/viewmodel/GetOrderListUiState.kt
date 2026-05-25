package com.i.common.attendance.ui.home.orderbook.viewmodel

import com.i.common.attendance.network.response.OrderItem

sealed class GetOrderListUiState {
    object Idle    : GetOrderListUiState()
    object Loading : GetOrderListUiState()
    data class Success(val list: List<OrderItem>) : GetOrderListUiState()
    data class ApiError(val message: String)      : GetOrderListUiState()
    data class NetworkError(val message: String)  : GetOrderListUiState()
}