package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.TourVoucherItem

sealed class TourDetailsState {

    object Idle : TourDetailsState()

    object Loading : TourDetailsState()

    data class Success(
        val data: TourVoucherItem
    ) : TourDetailsState()

    data class Empty(
        val message: String
    ) : TourDetailsState()

    data class Error(
        val message: String
    ) : TourDetailsState()

    data class NetworkError(
        val message: String
    ) : TourDetailsState()
}