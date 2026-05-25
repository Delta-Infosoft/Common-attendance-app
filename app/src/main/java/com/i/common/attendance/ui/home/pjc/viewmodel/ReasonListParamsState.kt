package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.ReasonListParamsList


sealed class ReasonListParamsState {
    object Idle : ReasonListParamsState()
    object Loading : ReasonListParamsState()
    data class Success(val list: List<ReasonListParamsList>) : ReasonListParamsState()
    data class Empty(val message: String = "No parameters found") : ReasonListParamsState()
    data class Error(val message: String) : ReasonListParamsState()
}