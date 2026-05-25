package com.i.common.attendance.ui.home.touradvanceexpense.viewmodel

sealed class AdvanceExpenseState {
    object Idle : AdvanceExpenseState()
    object Loading : AdvanceExpenseState()
    data class Success(
        val message: String,
        val id: String?
    ) : AdvanceExpenseState()
    data class Error(val message: String) : AdvanceExpenseState()
    data class Empty(val message: String) : AdvanceExpenseState()
}