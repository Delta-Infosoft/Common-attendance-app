package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.NameDropdownItem
import com.i.common.attendance.ui.home.tourvoucher.data.LocationDropdownType

sealed class LocationDropdownUiState {
    object Idle : LocationDropdownUiState()

    data class Loading(val type: LocationDropdownType) : LocationDropdownUiState()

    data class Success(
        val type: LocationDropdownType,
        val list: List<NameDropdownItem>
    ) : LocationDropdownUiState()

    data class ApiError(
        val type: LocationDropdownType,
        val message: String
    ) : LocationDropdownUiState()

    data class NetworkError(
        val type: LocationDropdownType,
        val message: String
    ) : LocationDropdownUiState()
}