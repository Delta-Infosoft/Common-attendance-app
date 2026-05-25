package com.i.common.attendance.ui.home.dailytour.viewmodel

import com.i.common.attendance.network.response.DailyTourDealerName

sealed class DealerNameState {
    object Idle : DealerNameState()
    object Loading : DealerNameState()
    data class Success(val data: List<DailyTourDealerName>) : DealerNameState()
    data class ApiError(val message: String) : DealerNameState()
    data class NetworkError(val message: String) : DealerNameState()
}