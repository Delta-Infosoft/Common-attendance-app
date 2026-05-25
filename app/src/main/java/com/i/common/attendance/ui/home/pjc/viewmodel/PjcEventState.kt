package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.PjcEventFullData


sealed class PjcEventState {
    object Idle : PjcEventState()
    object Loading : PjcEventState()
    data class Success(val events: PjcEventFullData) : PjcEventState()
    data class Error(val message: String) : PjcEventState()
}