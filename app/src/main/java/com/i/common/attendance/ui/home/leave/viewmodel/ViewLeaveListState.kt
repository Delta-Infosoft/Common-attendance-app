package com.i.common.attendance.ui.home.leave.viewmodel

import com.i.common.attendance.network.response.ViewLeaveUnnatiList

sealed class ViewLeaveListState {

    object Idle : ViewLeaveListState()

    object Loading : ViewLeaveListState()

    data class Success(
        val list: List<ViewLeaveUnnatiList>
    ) : ViewLeaveListState()

    data class ApiError(
        val message: String
    ) : ViewLeaveListState()

    data class NetworkError(
        val message: String
    ) : ViewLeaveListState()
}