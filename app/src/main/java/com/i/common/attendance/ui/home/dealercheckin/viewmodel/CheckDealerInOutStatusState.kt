package com.i.common.attendance.ui.home.dealercheckin.viewmodel

import com.i.common.attendance.network.response.CheckDealerInOutStatusData
import com.i.common.attendance.network.response.FileUploadResponse

sealed class CheckDealerInOutStatusState {

    object Idle : CheckDealerInOutStatusState()

    object Loading : CheckDealerInOutStatusState()

    data class Success(
        val list: List<CheckDealerInOutStatusData>
    ) : CheckDealerInOutStatusState()

    data class ApiError(
        val message: String
    ) : CheckDealerInOutStatusState()

    data class NetworkError(
        val message: String
    ) : CheckDealerInOutStatusState()
}