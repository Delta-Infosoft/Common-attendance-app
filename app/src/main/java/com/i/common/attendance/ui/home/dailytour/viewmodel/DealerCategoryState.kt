package com.i.common.attendance.ui.home.dailytour.viewmodel

import com.i.common.attendance.network.response.DailyTourDealerCategory

sealed class DealerCategoryState {
    object Idle : DealerCategoryState()
    object Loading : DealerCategoryState()
    data class Success(val data: List<DailyTourDealerCategory>) : DealerCategoryState()
    data class ApiError(val message: String) : DealerCategoryState()
    data class NetworkError(val message: String) : DealerCategoryState()
}