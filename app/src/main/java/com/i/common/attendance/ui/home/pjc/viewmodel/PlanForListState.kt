package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.PlanForList


sealed class PlanForListState {
    object Idle : PlanForListState()
    object Loading : PlanForListState()
    data class Success(val list: List<PlanForList>) : PlanForListState()
    data class Empty(val message: String = "No data found") : PlanForListState()
    data class Error(val message: String) : PlanForListState()
}