package com.i.common.attendance.ui.home.dealerwisereport.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.network.request.FacetRequest
import com.i.common.attendance.ui.home.dealerwisereport.data.FacetType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: FactRepository, @ApplicationContext private val context: Context) : ViewModel() {

    private val _facetState = MutableLiveData<FacetUiState>()
    val facetState: LiveData<FacetUiState> = _facetState

    fun loadReport(type: FacetType) {

        _facetState.value = FacetUiState.Loading(type)

        viewModelScope.launch {
            try {
                val response = repository.getFacetReport(request = FacetRequest(type = type.apiValue))
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _facetState.value = FacetUiState.ApiError(type, "Server error : ${response.code()}")
                    return@launch
                }

                if (body.status != "200") {
                    _facetState.value = FacetUiState.ApiError(type, "Something went wrong")
                    return@launch
                }

                val facetItem = body.result?.firstOrNull()
                if (facetItem == null) {
                    _facetState.value = FacetUiState.ApiError(type, "Report link not available")
                    return@launch
                }

                val reportUrl = facetItem.facetText
                    //buildReportUrl(facetItem.facetText)

                _facetState.value = FacetUiState.Success(type, reportUrl)

            } catch (e: IOException) {
                _facetState.value = FacetUiState.NetworkError(type, "Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _facetState.value = FacetUiState.ApiError(type, "Something went wrong")
            }
        }
    }

    /**
     * Same logic as Java but clean
     */
   /* private fun buildReportUrl(template: String): String {

        val prefValue = PreferenceHelper(context).LoadStringPref(AppConfig.RESULT_ARRAY, AppConfig.RESULT_ARRAY)

        val array = JSONArray(prefValue)
        val userName = array.getJSONObject(0).getString("UsersName")

        val currentDate = Cons.getCurrentDate()

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)

        val firstDayOfMonth = dateFormat.format(cal.time)

        return template
            .replace("u0026", "&")
            .replace("[EmpName]", userName)
            .replace("[FromDt]", firstDayOfMonth)
            .replace("[ToDt]", currentDate)
    }*/
}