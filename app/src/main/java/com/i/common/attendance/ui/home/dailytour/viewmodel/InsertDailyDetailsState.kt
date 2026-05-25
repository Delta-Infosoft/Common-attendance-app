package com.i.common.attendance.ui.home.dailytour.viewmodel

import com.i.common.attendance.network.response.FileUploadResponse

sealed class InsertDailyDetailsState {
    object Idle : InsertDailyDetailsState()
    object Loading : InsertDailyDetailsState()
    data class Success(val data: FileUploadResponse) : InsertDailyDetailsState()
    data class ApiError(val message: String) : InsertDailyDetailsState()
    data class NetworkError(val message: String) : InsertDailyDetailsState()
}