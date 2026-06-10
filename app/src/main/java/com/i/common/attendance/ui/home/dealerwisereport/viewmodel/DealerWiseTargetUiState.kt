package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import com.i.common.attendance.network.response.DealerWiseTarget

sealed class DealerWiseTargetUiState {

    object Idle : DealerWiseTargetUiState()

    object Loading : DealerWiseTargetUiState()

    data class Success(
        val list: List<DealerWiseTarget>
    ) : DealerWiseTargetUiState()

    data class ApiError(
        val message: String
    ) : DealerWiseTargetUiState()

    data class NetworkError(
        val message: String
    ) : DealerWiseTargetUiState()
}