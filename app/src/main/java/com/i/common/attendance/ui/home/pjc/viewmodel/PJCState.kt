package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.EventsCalModel
import com.i.common.attendance.network.response.HolidayWeekOffModel


/*sealed class PJCState {
    object Loading : PJCState()
    data class Empty(val message: String) : PJCState()
    data class Success(val list: List<EventsCalModel>) : PJCState()
    data class Error(val message: String) : PJCState()
}*/

sealed class PJCState {
    object Loading : PJCState()
    data class Success(
        val pjcList: List<EventsCalModel>,
        val holidayList: List<HolidayWeekOffModel>
    ) : PJCState()

    data class Empty(val message: String) : PJCState()
    data class Error(val message: String) : PJCState()
}
