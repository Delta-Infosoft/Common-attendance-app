package com.i.common.attendance.ui.home.leave.viewmodel

import com.i.common.attendance.network.response.FileUploadResponse

sealed class LeaveRequestState {

    object Idle : LeaveRequestState()

    object Loading : LeaveRequestState()

    data class Success(
        val response: FileUploadResponse
    ) : LeaveRequestState()

    data class ApiError(
        val message: String
    ) : LeaveRequestState()

    data class NetworkError(
        val message: String
    ) : LeaveRequestState()
}