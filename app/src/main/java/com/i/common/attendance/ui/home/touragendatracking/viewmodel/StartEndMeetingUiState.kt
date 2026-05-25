package com.i.common.attendance.ui.home.touragendatracking.viewmodel
enum class MeetingType {
    START,
    END
}
sealed class StartEndMeetingUiState {
    object Idle    : StartEndMeetingUiState()
    data class Loading(val type: MeetingType)                : StartEndMeetingUiState()
    data class Success(val type: MeetingType, val message: String) : StartEndMeetingUiState()
    data class ApiError(val type: MeetingType, val message: String)     : StartEndMeetingUiState()
    data class NetworkError(val type: MeetingType, val message: String) : StartEndMeetingUiState()
}