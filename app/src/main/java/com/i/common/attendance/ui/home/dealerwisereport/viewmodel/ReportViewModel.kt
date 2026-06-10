package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.DealerDetailsAccountRequest
import com.i.common.attendance.network.request.FacetRequest
import com.i.common.attendance.network.response.DealerDetailsAccount
import com.i.common.attendance.network.response.FacetsItem
import com.i.common.attendance.ui.home.dealerwisereport.data.FacetType
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: FactRepository, @ApplicationContext private val context: Context) : ViewModel() {
    @Inject lateinit var sharedPref: EncryptedPrefHelper

    private val _facetState = MutableLiveData<FacetUiState>()
    val facetState: LiveData<FacetUiState> = _facetState

    fun loadReport(type: FacetType) {

        _facetState.value = FacetUiState.Loading(type)

        viewModelScope.launch {
            try {
                val response = repository.getFacetReport(
                    request = FacetRequest(type = type.apiValue)
                )

                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _facetState.value =
                        FacetUiState.ApiError(type, "Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {

                        if (body.result == null || !body.result.isJsonArray) {
                            _facetState.value = FacetUiState.ApiError(type, "Report link not available")
                            return@launch
                        }

                        val facetItem = Gson().fromJson(
                            body.result.asJsonArray.firstOrNull(),
                            FacetsItem::class.java
                        )

                        if (facetItem?.facetText.isNullOrBlank()) {
                            _facetState.value =
                                FacetUiState.ApiError(type, "Report link not available")
                            return@launch
                        }

                        val reportUrl = buildReportUrl(facetItem.facetText.orEmpty())
                        _facetState.value = FacetUiState.Success(type, reportUrl)
                    }

                    "209" -> {
                        _facetState.value = FacetUiState.ApiError(type, "No Record Found")
                    }

                    else -> {
                        _facetState.value = FacetUiState.ApiError(type, "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _facetState.value =
                    FacetUiState.NetworkError(
                        type,
                        "Please check your internet connection"
                    )

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)

                _facetState.value =
                    FacetUiState.ApiError(
                        type,
                        "Something went wrong"
                    )
            }
        }
    }
    /**
     * Same logic as Java but clean
     */
    private fun buildReportUrl(template: String): String {

        val currentDate = Constants.getCurrentTimestamp("dd-MMM-yyyy")

        val firstDayOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }.time.let {
            SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(it)
        }

        return template
            .replace("u0026", "&")
            .replace("[EmpName]", sharedPref.getUser()?.UsersName.orEmpty())
            .replace("[FromDt]", firstDayOfMonth)
            .replace("[ToDt]", currentDate)
    }

}