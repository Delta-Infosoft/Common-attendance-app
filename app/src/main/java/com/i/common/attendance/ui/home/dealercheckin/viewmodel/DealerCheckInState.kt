package com.i.common.attendance.ui.home.dealercheckin.viewmodel

import com.i.common.attendance.network.response.FileUploadResponse

sealed class DealerCheckInState {

    object Idle : DealerCheckInState()

    object Loading : DealerCheckInState()

    data class Success(
        val response: FileUploadResponse
    ) : DealerCheckInState()

    data class ApiError(
        val message: String
    ) : DealerCheckInState()

    data class NetworkError(
        val message: String
    ) : DealerCheckInState()
}