package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import com.i.common.attendance.network.response.DealerDetailsAccount

sealed class DealerDetailsUiState {
    object Idle : DealerDetailsUiState()
    object Loading : DealerDetailsUiState()

    data class Success(
        val list: List<DealerDetailsAccount>
    ) : DealerDetailsUiState()

    data class ApiError(
        val message: String
    ) : DealerDetailsUiState()

    data class NetworkError(
        val message: String
    ) : DealerDetailsUiState()
}