package com.i.common.attendance.ui.home.viewmodel


sealed class TravelAttachmentDeleteUiState {
    object Idle : TravelAttachmentDeleteUiState()
    object Loading : TravelAttachmentDeleteUiState()
    data class Success(val message: String) : TravelAttachmentDeleteUiState()

    data class Error(val message: String) : TravelAttachmentDeleteUiState()
}