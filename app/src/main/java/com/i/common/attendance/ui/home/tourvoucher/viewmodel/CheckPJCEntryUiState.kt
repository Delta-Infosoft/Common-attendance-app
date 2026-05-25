package com.i.common.attendance.ui.home.tourvoucher.viewmodel

sealed class CheckPJCEntryUiState {
    object Idle : CheckPJCEntryUiState()
    object Loading : CheckPJCEntryUiState()
    object Allowed : CheckPJCEntryUiState()
    data class NotAllowed(val notFiledDates: String) : CheckPJCEntryUiState()
    // NEW STATE
    data class UncoverType(val message: String) : CheckPJCEntryUiState()
    object Error : CheckPJCEntryUiState()
}