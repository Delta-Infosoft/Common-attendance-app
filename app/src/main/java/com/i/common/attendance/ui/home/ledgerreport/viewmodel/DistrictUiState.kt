package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import com.i.common.attendance.network.response.GetDistrict

sealed class DistrictUiState {

    object Idle : DistrictUiState()

    object Loading : DistrictUiState()

    data class Success(
        val list: List<GetDistrict>
    ) : DistrictUiState()

    data class ApiError(
        val message: String
    ) : DistrictUiState()

    data class NetworkError(
        val message: String
    ) : DistrictUiState()
}