package com.i.common.attendance.ui.home.orderbook.viewmodel

import com.i.common.attendance.network.response.ProductModel

sealed class GetProductUiState {
    object Idle    : GetProductUiState()
    object Loading : GetProductUiState()
    data class Success(val list: List<ProductModel>) : GetProductUiState()
    data class ApiError(val message: String)         : GetProductUiState()
    data class NetworkError(val message: String)     : GetProductUiState()
}