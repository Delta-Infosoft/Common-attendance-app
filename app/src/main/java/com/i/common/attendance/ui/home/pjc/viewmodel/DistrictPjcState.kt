package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.GetDistrictPjcList


sealed class DistrictPjcState {
    object Idle : DistrictPjcState()
    object Loading : DistrictPjcState()
    data class Success(val list: List<GetDistrictPjcList>) : DistrictPjcState()
    data class Empty(val message: String = "No district found") : DistrictPjcState()
    data class Error(val message: String) : DistrictPjcState()
}