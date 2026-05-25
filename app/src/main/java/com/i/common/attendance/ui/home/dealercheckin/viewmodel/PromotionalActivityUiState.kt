package com.i.common.attendance.ui.home.dealercheckin.viewmodel

import com.i.common.attendance.network.response.FileUploadResponse

sealed class PromotionalActivityUiState {

    object Idle : PromotionalActivityUiState()

    object Loading : PromotionalActivityUiState()

    data class Success(
        val response: FileUploadResponse
    ) : PromotionalActivityUiState()

    data class ApiError(
        val message: String
    ) : PromotionalActivityUiState()

    data class NetworkError(
        val message: String
    ) : PromotionalActivityUiState()
}