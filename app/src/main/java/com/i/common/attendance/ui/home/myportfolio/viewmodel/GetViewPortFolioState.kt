package com.i.common.attendance.ui.home.myportfolio.viewmodel

import com.i.common.attendance.network.response.ViewPortFolioModel

sealed class GetViewPortFolioState {

    object Idle : GetViewPortFolioState()

    object Loading : GetViewPortFolioState()

    data class Success(val data: List<ViewPortFolioModel>) : GetViewPortFolioState()

    data class Error(val message: String) : GetViewPortFolioState()

    data class Empty(val message: String) : GetViewPortFolioState()
}