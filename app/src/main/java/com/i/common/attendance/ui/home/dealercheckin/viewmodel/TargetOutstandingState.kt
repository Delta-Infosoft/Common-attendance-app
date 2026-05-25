package com.i.common.attendance.ui.home.dealercheckin.viewmodel

import com.i.common.attendance.network.response.TargetOutstandingData

sealed class TargetOutstandingState {

    object Idle : TargetOutstandingState()

    object Loading : TargetOutstandingState()

    data class Success(
        val list: List<TargetOutstandingData>
    ) : TargetOutstandingState()

    data class ApiError(
        val message: String
    ) : TargetOutstandingState()

    data class NetworkError(
        val message: String
    ) : TargetOutstandingState()
}