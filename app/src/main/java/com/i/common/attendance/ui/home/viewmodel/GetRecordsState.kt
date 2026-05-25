package com.i.common.attendance.ui.home.viewmodel

import com.i.common.attendance.network.response.Records

sealed class GetRecordsState {
    object Idle : GetRecordsState()
    object Loading : GetRecordsState()
    data class Success(val records: List<Records>) : GetRecordsState()
    data class Empty(val message: String) : GetRecordsState()
    data class Error(val message: String) : GetRecordsState()
}