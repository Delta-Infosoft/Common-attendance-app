package com.i.common.attendance.ui.authentication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.network.request.ForgotPasswordRequest
import com.i.common.attendance.network.request.GetUserValidRequest
import com.i.common.attendance.network.request.LoginRequest
import com.i.common.attendance.network.response.getPassword
import com.i.common.attendance.network.response.getUserList
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.delta.attendanceappv2.ui.authentication.viewmodel.ForgotPasswordState
import com.i.delta.attendanceappv2.ui.authentication.viewmodel.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthenticationRepository,
    private val prefHelper: EncryptedPrefHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    private val _forgotState = MutableLiveData<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotState: LiveData<ForgotPasswordState> = _forgotState


    fun login(userName: String) {
        viewModelScope.launch { _loginState.value = LoginState.Loading

            try {
                // ---------------------------
                // 1️⃣ CHECK USER API
                // ---------------------------
                val checkResponse = repository.checkUser(GetUserValidRequest(userName))

                if (!checkResponse.isSuccessful || checkResponse.body() == null) {
                    _loginState.value = LoginState.Error("Server error")
                    return@launch
                }

                val checkBody = checkResponse.body()!!

                // 🔥 BUSINESS STATUS CHECK
                if (checkBody.status != "200") {
                    _loginState.value = LoginState.Error(checkBody.message)
                    return@launch
                }

                // ---------------------------
                // 2️⃣ LOGIN API
                // ---------------------------
                // ---------------------------
                // LOGIN API
                // ---------------------------
                val response = repository.loginWithFcmId(
                    LoginRequest(
                        userName = userName,
                        imei = Constants.getDeviceId(context),
                        fcmId = prefHelper.getFCMToken()
                    )
                )

                if (!response.isSuccessful || response.body() == null) {
                    _loginState.value = LoginState.Error("Server error")
                    return@launch
                }

                val body = response.body()!!

                // ---------------------------
                // STATUS HANDLING
                // ---------------------------
                when (body.status) {

                    // ✅ SUCCESS
                    "200" -> {
                        val users = body.getUserList()

                        if (users.isEmpty()) {
                            _loginState.value = LoginState.Error(body.message)
                            return@launch
                        }

                        val user = users.first()
                        prefHelper.saveUser(user)

                        _loginState.value = LoginState.Success(user)
                    }

                    // ⚠️ APPROVAL REQUIRED
                    "209" -> {
                        _loginState.value =
                            LoginState.ApprovalRequired(body.message)
                    }

                    // ❌ OTHER ERRORS
                    else -> {
                        _loginState.value =
                            LoginState.Error(body.message)
                    }
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _loginState.value =
                    LoginState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    fun forgotPassword(mobileNo: String) {
        viewModelScope.launch {
            _forgotState.value = ForgotPasswordState.Loading

            try {
                val response = repository.forgotPassword(
                    ForgotPasswordRequest(
                        userName = mobileNo,
                        imei = Constants.getDeviceId(context)
                    )
                )

                if (!response.isSuccessful || response.body() == null) {
                    _forgotState.value = ForgotPasswordState.Error("Server error")
                    return@launch
                }

                val body = response.body()!!

                when (body.status) {
                    "209" -> {
                        _forgotState.value = ForgotPasswordState.Message(body.message ?: "")
                    }

                    "200" -> {
                        val password = body.getPassword()

                        if (password.isNullOrBlank()) {
                            _forgotState.value = ForgotPasswordState.Error("Password not received")
                        } else {
                            _forgotState.value = ForgotPasswordState.Success(password)
                        }
                    }

                    else -> {
                        _forgotState.value = ForgotPasswordState.Error(body.message ?: "Error")
                    }
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _forgotState.value =
                    ForgotPasswordState.Error("Unable to connect server")
            }
        }
    }
}
