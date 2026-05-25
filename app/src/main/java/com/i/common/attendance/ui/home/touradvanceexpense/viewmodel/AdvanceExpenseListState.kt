package com.i.common.attendance.ui.home.touradvanceexpense.viewmodel

import com.i.common.attendance.network.response.TourAdvanceExpense

sealed class AdvanceExpenseListState {
    object Idle : AdvanceExpenseListState()
    object Loading : AdvanceExpenseListState()
    data class Success(val data: List<TourAdvanceExpense>) : AdvanceExpenseListState()
    data class Error(val message: String) : AdvanceExpenseListState()
    data class Empty(val message: String) : AdvanceExpenseListState()
}