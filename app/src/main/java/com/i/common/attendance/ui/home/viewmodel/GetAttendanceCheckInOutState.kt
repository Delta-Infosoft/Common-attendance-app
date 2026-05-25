package com.i.common.attendance.ui.home.viewmodel

import com.i.common.attendance.network.response.AttendanceInOutReport

sealed class GetAttendanceCheckInOutState {
    object Idle : GetAttendanceCheckInOutState()
    object Loading : GetAttendanceCheckInOutState()
    data class Success(val records: List<AttendanceInOutReport>) : GetAttendanceCheckInOutState()
    data class Empty(val message: String) : GetAttendanceCheckInOutState()
    data class Error(val message: String) : GetAttendanceCheckInOutState()
}