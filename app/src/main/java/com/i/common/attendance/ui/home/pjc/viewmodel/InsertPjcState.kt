package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.InsertPjcEvent


sealed class InsertPjcState {
    object Idle : InsertPjcState()
    object Loading : InsertPjcState()
    data class Success(val data: InsertPjcEvent) : InsertPjcState()
    data class Error(val message: String) : InsertPjcState()
}