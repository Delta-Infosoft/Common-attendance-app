package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import com.i.common.attendance.ui.home.dealerwisereport.data.FacetType

sealed class FacetUiState {

    data class Loading(val type: FacetType) : FacetUiState()

    data class Success(
        val type: FacetType,
        val reportUrl: String
    ) : FacetUiState()

    data class ApiError(
        val type: FacetType,
        val message: String
    ) : FacetUiState()

    data class NetworkError(
        val type: FacetType,
        val message: String
    ) : FacetUiState()
}
