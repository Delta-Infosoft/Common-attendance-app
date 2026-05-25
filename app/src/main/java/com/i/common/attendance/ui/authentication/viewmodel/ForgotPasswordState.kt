package com.i.delta.attendanceappv2.ui.authentication.viewmodel

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val password: String) : ForgotPasswordState()
    data class Message(val message: String) : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}