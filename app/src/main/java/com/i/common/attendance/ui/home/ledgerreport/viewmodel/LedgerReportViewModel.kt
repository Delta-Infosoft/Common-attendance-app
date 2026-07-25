package com.i.common.attendance.ui.home.ledgerreport.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.GetCustomerRequest
import com.i.common.attendance.network.request.GetDistrictRequest
import com.i.common.attendance.network.request.GetDivisionRequest
import com.i.common.attendance.network.request.GetLedgerPdfRequest
import com.i.common.attendance.network.response.CustomerData
import com.i.common.attendance.network.response.GetDistrict
import com.i.common.attendance.network.response.GetDivision
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

                        val list = if (
                            body.result != null &&
                            body.result.isJsonArray
                        ) {
                            Gson().fromJson<List<CustomerData>>(
                                body.result.asJsonArray,
                                object : TypeToken<List<CustomerData>>() {}.type
                            )
                        } else {
                            emptyList()
                        }

                        if (list.isEmpty()) {
                            _customerState.value =
                                CustomerUiState.ApiError(body.message ?: "No customer found")
                        } else {
                            cachedCustomerList = list
                            _customerState.value = CustomerUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _customerState.value =
                            CustomerUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _customerState.value =
                            CustomerUiState.ApiError(body.message ?: "Something went wrong")
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

    /*=========================================================================================*/
    /*============================ Ledger Report Get District ======================================*/
    private val _districtState = MutableLiveData<DistrictUiState>(DistrictUiState.Idle)
    val districtState: LiveData<DistrictUiState> = _districtState

    private var cachedDistrictList: List<GetDistrict> = emptyList()
    fun getCachedDistrictList(): List<GetDistrict> = cachedDistrictList
    fun loadDistrictList(request: GetDistrictRequest) {

        _districtState.value = DistrictUiState.Loading

        viewModelScope.launch {

            try {

                val response = repository.ledgerDistrictList(request)

                if (!response.isSuccessful) {
                    _districtState.value =
                        DistrictUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _districtState.value =
                        DistrictUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {

                        val list: List<GetDistrict> = try {

                            if (body.result != null &&
                                body.result!!.isJsonArray &&
                                body.result!!.asJsonArray.size() > 0
                            ) {

                                Gson().fromJson(
                                    body.result!!.asJsonArray,
                                    object : TypeToken<List<GetDistrict>>() {}.type
                                ) ?: emptyList()

                            } else {
                                emptyList()
                            }

                        } catch (e: Exception) {
                            emptyList()
                        }

                        cachedDistrictList = list
                        _districtState.value =
                            DistrictUiState.Success(list)

                    }

                    "209" -> {

                        cachedDistrictList = emptyList()

                        _districtState.value =
                            DistrictUiState.ApiError(
                                body.message ?: "No Record Found"
                            )
                    }

                    else -> {

                        _districtState.value =
                            DistrictUiState.ApiError(
                                body.message ?: "Something went wrong"
                            )
                    }
                }

            } catch (e: IOException) {

                _districtState.value =
                    DistrictUiState.NetworkError(
                        "Please check your internet connection"
                    )

            } catch (e: Exception) {

                FirebaseCrashlytics.getInstance().recordException(e)

                _districtState.value =
                    DistrictUiState.ApiError(
                        e.message ?: "Something went wrong"
                    )
            }
        }
    }

    /*=========================================================================================*/
    /*============================ Ledger Report Get Division ======================================*/
    private val _divisionState = MutableLiveData<DivisionUiState>(DivisionUiState.Idle)
    val divisionState: LiveData<DivisionUiState> = _divisionState

    private var cachedDivisionList: List<GetDivision> = emptyList()

    fun getCachedDivisionList(): List<GetDivision> = cachedDivisionList
    fun loadDivisionList(request: GetDivisionRequest) {

        _divisionState.value = DivisionUiState.Loading

        viewModelScope.launch {

            try {

                val response = repository.ledgerDivisionList(request)

                if (!response.isSuccessful) {
                    _divisionState.value =
                        DivisionUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _divisionState.value =
                        DivisionUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {

                        val list: List<GetDivision> = try {

                            if (body.result != null &&
                                body.result!!.isJsonArray &&
                                body.result!!.asJsonArray.size() > 0
                            ) {

                                Gson().fromJson(
                                    body.result!!.asJsonArray,
                                    object : TypeToken<List<GetDivision>>() {}.type
                                ) ?: emptyList()

                            } else {
                                emptyList()
                            }

                        } catch (e: Exception) {
                            emptyList()
                        }

                        cachedDivisionList = list
                        _divisionState.value =
                            DivisionUiState.Success(list)

                    }

                    "209" -> {

                        cachedDivisionList = emptyList()

                        _divisionState.value =
                            DivisionUiState.ApiError(
                                body.message ?: "No Record Found"
                            )
                    }

                    else -> {

                        _divisionState.value =
                            DivisionUiState.ApiError(
                                body.message ?: "Something went wrong"
                            )
                    }
                }

            } catch (e: IOException) {

                _divisionState.value =
                    DivisionUiState.NetworkError(
                        "Please check your internet connection"
                    )

            } catch (e: Exception) {

                FirebaseCrashlytics.getInstance().recordException(e)

                _divisionState.value =
                    DivisionUiState.ApiError(
                        e.message ?: "Something went wrong"
                    )
            }
        }
    }

}