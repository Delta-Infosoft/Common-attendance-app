package com.i.common.attendance.ui.home.leave.viewmodel

import com.i.common.attendance.network.response.FileUploadResponse

sealed class LeaveApprovalUpdateState {

    object Idle : LeaveApprovalUpdateState()

    object Loading : LeaveApprovalUpdateState()

    data class Success(
        val response: FileUploadResponse
    ) : LeaveApprovalUpdateState()

    data class ApiError(
        val message: String
    ) : LeaveApprovalUpdateState()

    data class NetworkError(
        val message: String
    ) : LeaveApprovalUpdateState()
}