package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.TourAgendaTrackingFacets


sealed class GetFacetsUiState {
    object Idle    : GetFacetsUiState()
    data class Loading(val type: FacetType)                              : GetFacetsUiState()
    data class Success(val type: FacetType, val list: List<TourAgendaTrackingFacets>) : GetFacetsUiState()
    data class ApiError(val type: FacetType, val message: String)        : GetFacetsUiState()
    data class NetworkError(val type: FacetType, val message: String)    : GetFacetsUiState()
}