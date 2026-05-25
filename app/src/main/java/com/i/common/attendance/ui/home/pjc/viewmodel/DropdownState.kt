package com.i.common.attendance.ui.home.pjc.viewmodel

import com.i.common.attendance.network.response.LoadDropDownList


sealed class DropdownState {
    object Idle : DropdownState()
    object Loading : DropdownState()
    data class Success(val list: List<LoadDropDownList>) : DropdownState()
    data class Empty(val message: String) : DropdownState()
    data class Error(val message: String) : DropdownState()
}