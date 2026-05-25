package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.TourAgendaTrackingDealerName

sealed class GetDealerNameUiState {
    object Idle    : GetDealerNameUiState()
    object Loading : GetDealerNameUiState()
    data class Success(val list: List<TourAgendaTrackingDealerName>) : GetDealerNameUiState()
    data class ApiError(val message: String)                         : GetDealerNameUiState()
    data class NetworkError(val message: String)                     : GetDealerNameUiState()
}