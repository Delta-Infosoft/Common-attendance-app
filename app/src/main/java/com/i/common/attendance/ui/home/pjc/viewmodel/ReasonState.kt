package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.ReasonList


sealed class ReasonState {
    object Idle : ReasonState()
    object Loading : ReasonState()
    data class Success(val list: List<ReasonList>) : ReasonState()
    data class Empty(val message: String = "No reason found") : ReasonState()
    data class Error(val message: String) : ReasonState()
}
