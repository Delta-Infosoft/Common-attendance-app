package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.DropdownItem
import com.i.common.attendance.ui.home.tourvoucher.data.DropdownType

sealed class DropdownUiState {
    object Idle : DropdownUiState()
    object Loading : DropdownUiState()

    data class Success(
        val group: DropdownType,
        val list: List<DropdownItem>
    ) : DropdownUiState()

    data class ApiError(
        val group: DropdownType,
        val message: String
    ) : DropdownUiState()

    data class NetworkError(
        val group: DropdownType,
        val message: String
    ) : DropdownUiState()
}