package com.i.common.attendance.ui.home.dailytour.viewmodel

import com.i.common.attendance.network.response.DailyTourDistrict

sealed class DistrictState {
    object Idle : DistrictState()
    object Loading : DistrictState()
    data class Success(val data: List<DailyTourDistrict>) : DistrictState()
    data class ApiError(val message: String) : DistrictState()
    data class NetworkError(val message: String) : DistrictState()
}