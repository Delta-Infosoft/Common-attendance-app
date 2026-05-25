package com.i.common.attendance.ui.authentication.viewmodel

import com.i.common.attendance.network.request.ForgotPasswordRequest
import com.i.common.attendance.network.request.GetUserValidRequest
import com.i.common.attendance.network.request.LoginRequest
import com.i.common.attendance.network.response.ForgotPasswordResponse
import com.i.common.attendance.network.response.LoginResponse
import com.i.common.attendance.network.service.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

class AuthenticationRepository @Inject constructor(@Named("DEFAULT")private val apiService: ApiService) {

    suspend fun checkUser(request: GetUserValidRequest): Response<LoginResponse> {
        return apiService.checkUser(request.toMultipartBody())
    }

    suspend fun loginWithFcmId(request: LoginRequest): Response<LoginResponse> {
        return apiService.loginWithFcmId(request.toMultipartBody())
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Response<ForgotPasswordResponse> {
        return apiService.forgotPassword(request.toMultipartBody())
    }
}