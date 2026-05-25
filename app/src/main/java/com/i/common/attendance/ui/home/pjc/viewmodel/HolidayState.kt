package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.HolidayWeekOffModel


sealed class HolidayState {
    object Loading : HolidayState()
    data class Success(val list: List<HolidayWeekOffModel>) : HolidayState()
    data class Empty(val message: String) : HolidayState()
    data class Error(val message: String) : HolidayState()
}