package com.i.common.attendance.ui.home.dailytour.viewmodel

import com.i.common.attendance.network.response.DailTourList

sealed class DailyDetailsState {
    object Idle : DailyDetailsState()
    object Loading : DailyDetailsState()
    data class Success(val data: List<DailTourList>) : DailyDetailsState()
    data class ApiError(val message: String) : DailyDetailsState()
    data class NetworkError(val message: String) : DailyDetailsState()
    data class Empty(val message: String) : DailyDetailsState()
}