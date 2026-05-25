package com.i.common.attendance.ui.home.viewmodel

import com.i.common.attendance.network.response.AttendanceRecord

sealed class GetCheckInOutState {
    object Idle : GetCheckInOutState()
    object Loading : GetCheckInOutState()
    data class Success(val records: List<AttendanceRecord>) : GetCheckInOutState()
    data class Empty(val message: String) : GetCheckInOutState()
    data class Error(val message: String) : GetCheckInOutState()
}