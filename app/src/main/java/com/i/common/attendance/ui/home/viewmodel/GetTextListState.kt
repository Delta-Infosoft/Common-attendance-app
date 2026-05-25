package com.i.common.attendance.ui.home.viewmodel

import com.i.common.attendance.network.response.StatusList

sealed class GetTextListState {
    object Idle : GetTextListState()
    object Loading : GetTextListState()
    data class Success(val list: List<StatusList>) : GetTextListState()
    data class Empty(val message: String) : GetTextListState()
    data class Error(val message: String) : GetTextListState()
}