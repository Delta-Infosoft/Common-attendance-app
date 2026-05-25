package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.GetCustomerRequest
import com.i.common.attendance.network.request.GetLedgerPdfRequest
import com.i.common.attendance.network.response.CustomerData
import com.i.common.attendance.network.response.LedgerPdfData
import com.i.common.attendance.network.response.LedgerPdfDataShow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LedgerReportViewModel @Inject constructor(
    private val repository: LedgerReportRepository
) : ViewModel() {

    private val _customerState = MutableLiveData<CustomerUiState>(CustomerUiState.Idle)
    val customerState: LiveData<CustomerUiState> = _customerState
    private var cachedCustomerList: List<CustomerData>? = null
    fun getCachedCustomerList(): List<CustomerData>? = cachedCustomerList
    fun loadCustomerList(request: GetCustomerRequest) {
        _customerState.value = CustomerUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getCustomer(request)
                if (!response.isSuccessful) {
                    _customerState.value = CustomerUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _customerState.value = CustomerUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = body.result ?: emptyList()

                        if (list.isEmpty()) {
                            _customerState.value = CustomerUiState.ApiError(body.message ?: "No customer found")
                        } else {
                            cachedCustomerList = list
                            _customerState.value = CustomerUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _customerState.value = CustomerUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _customerState.value = CustomerUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _customerState.value = CustomerUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _customerState.value = CustomerUiState.ApiError("Something went wrong")
            }
        }
    }

    /*=========================================================================================*/
    /*============================ Ledger Report List ======================================*/
    private val _ledgerPdfState = MutableLiveData<LedgerPdfUiState>(LedgerPdfUiState.Idle)
    val ledgerPdfState: LiveData<LedgerPdfUiState> = _ledgerPdfState
    private var cachedLedgerPdf: List<LedgerPdfData> = emptyList()
    fun getCachedLedgerPdf(): List<LedgerPdfData> = cachedLedgerPdf

    fun loadLedgerPdf(request: GetLedgerPdfRequest) {

        _ledgerPdfState.value = LedgerPdfUiState.Loading

        viewModelScope.launch {

            try {
                val response = repository.getLedgerReport(request)

                if (!response.isSuccessful) {
                    _ledgerPdfState.value = LedgerPdfUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _ledgerPdfState.value = LedgerPdfUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list: List<LedgerPdfData> = try {
                            if (body.result != null && body.result!!.isJsonArray && body.result!!.asJsonArray.size() > 0) {
                                Gson().fromJson<List<LedgerPdfData>>(
                                    body.result!!.asJsonArray,
                                    object : TypeToken<List<LedgerPdfData>>() {}.type
                                ) ?: emptyList()

                            } else {
                                emptyList()
                            }

                        } catch (e: Exception) {
                            emptyList()
                        }
                        cachedLedgerPdf = list
                        _ledgerPdfState.value = LedgerPdfUiState.Success(list)
                    }

                    "209" -> {
                        cachedLedgerPdf = emptyList()
                        _ledgerPdfState.value = LedgerPdfUiState.Empty(body.message ?: "No Record Found"
                        )
                    }

                    else -> {
                        _ledgerPdfState.value = LedgerPdfUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _ledgerPdfState.value = LedgerPdfUiState.NetworkError("Please check internet connection")
            } catch (e: Exception) {
                _ledgerPdfState.value = LedgerPdfUiState.ApiError(e.message ?: "Something went wrong")
            }
        }
    }

    /*=========================================================================================*/
    /*============================ Ledger Report show pdf ======================================*/

    private val _ledgerPdfShowState = MutableLiveData<LedgerPdfShowUiState>(LedgerPdfShowUiState.Idle)
    val ledgerPdfShowState: LiveData<LedgerPdfShowUiState> = _ledgerPdfShowState
    private var cachedLedgerPdfShow: List<LedgerPdfDataShow> = emptyList()
    fun getCachedLedgerPdfShow(): List<LedgerPdfDataShow> = cachedLedgerPdfShow

    fun loadLedgerPdfShow(request: GetLedgerPdfRequest) {

        _ledgerPdfShowState.value = LedgerPdfShowUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.ledgerReportShowPdf(request)
                if (!response.isSuccessful) {
                    _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        if (body.result != null && body.result!!.isJsonArray) {
                            val type = object : TypeToken<List<LedgerPdfDataShow>>() {}.type
                            val list: List<LedgerPdfDataShow> = Gson().fromJson(body.result, type)
                            if (list.isNotEmpty()) {
                                cachedLedgerPdfShow = list
                                val pdfUrl = getReplacedString(list.first().ShowPDF)
                                _ledgerPdfShowState.value = LedgerPdfShowUiState.Success(list = list, pdfUrl = pdfUrl)
                            } else {
                                _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError("No PDF Data Found !!!")
                            }

                        } else {
                            _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError("Invalid response format")
                        }
                    }

                    "209" -> {
                        val errorMessage =
                            if (body.result != null && body.result!!.isJsonPrimitive) {
                                body.result!!.asString
                            } else {
                                body.message ?: "Data Get Issue"
                            }

                        _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError(errorMessage)
                    }

                    else -> {
                        _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _ledgerPdfShowState.value = LedgerPdfShowUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _ledgerPdfShowState.value = LedgerPdfShowUiState.ApiError("Something went wrong")
            }
        }
    }
    private fun getReplacedString(convertString: String?): String {
        return convertString?.replace("u0027", "'")?.replace("u0026", "&")?.replace("u005B", "[")
            ?.replace("u005D", "]")
            ?: ""
    }

}