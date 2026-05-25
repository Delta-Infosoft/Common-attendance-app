package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import com.i.common.attendance.network.response.TourAgendaTrackingSubDealerName

sealed class GetSubDealerNameUiState {
    object Idle    : GetSubDealerNameUiState()
    object Loading : GetSubDealerNameUiState()
    data class Success(val list: List<TourAgendaTrackingSubDealerName>) : GetSubDealerNameUiState()
    data class ApiError(val message: String)                            : GetSubDealerNameUiState()
    data class NetworkError(val message: String)                        : GetSubDealerNameUiState()
}