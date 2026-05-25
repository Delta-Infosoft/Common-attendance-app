package com.i.common.attendance.ui.home.newcustomerdealer.viewmodel

import com.i.common.attendance.network.response.SelectPortfolioModel

sealed class SelectPortfolioUiState {
    object Idle : SelectPortfolioUiState()
    object Loading : SelectPortfolioUiState()
    data class Success(val data: List<SelectPortfolioModel>) : SelectPortfolioUiState()
    data class ApiError(val message: String) : SelectPortfolioUiState()
    data class NetworkError(val message: String) : SelectPortfolioUiState()
}