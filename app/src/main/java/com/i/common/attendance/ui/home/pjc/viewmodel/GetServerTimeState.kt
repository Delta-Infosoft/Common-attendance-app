package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.ServerTime

sealed class GetServerTimeState {

    object Loading : GetServerTimeState()

    data class Success(val serverTime: ServerTime) : GetServerTimeState()

    data class Error(val message: String) : GetServerTimeState()
}