package com.i.common.attendance.ui.home.dailytour.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.i.common.attendance.network.request.DailyTourAddDetailsDukeRequest
import com.i.common.attendance.network.request.DailyTourAddDetailsRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.DailyTourDealerNameRequest
import com.i.common.attendance.network.request.DailyTourDistrictRequest
import com.i.common.attendance.network.request.DailyTourFlotechRequest
import com.i.common.attendance.network.request.DailyTourListRequest
import com.i.common.attendance.network.request.EmployeeRequest
import com.i.common.attendance.network.response.DailTourList
import com.i.common.attendance.network.response.DailyTourDealerCategory
import com.i.common.attendance.network.response.DailyTourDealerName
import com.i.common.attendance.network.response.DailyTourDistrict
import com.i.common.attendance.network.response.TourVoucherItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DailyTourViewModel @Inject constructor(private val repository: DailyTourRepository, @ApplicationContext private val context: Context) : ViewModel() {

    private val _dailyDetailsState = MutableLiveData<DailyDetailsState>(DailyDetailsState.Idle)
    val dailyDetailsState: LiveData<DailyDetailsState> = _dailyDetailsState

    fun getDailyDetailsList(request: DailyTourListRequest) {
        _dailyDetailsState.value = DailyDetailsState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getDailyDetailsListAPI(request)
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _dailyDetailsState.value = DailyDetailsState.ApiError("Server error : ${response.code()}")
                    return@launch
                }
                when (body.status) {

                    "200" -> {
                        val list = when (val res = body.result) {

                            is List<*> -> {
                                res.mapNotNull { item ->
                                    (item as? LinkedTreeMap<*, *>)?.let {
                                        Gson().fromJson(
                                            Gson().toJson(it),
                                            DailTourList::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _dailyDetailsState.value = DailyDetailsState.Empty(body.message ?: "No record found")
                        } else {
                            _dailyDetailsState.value = DailyDetailsState.Success(list)
                        }
                    }

                    "209" -> {
                        _dailyDetailsState.value = DailyDetailsState.Empty(body.message ?: "No record found")
                    }
                    else -> {
                        _dailyDetailsState.value = DailyDetailsState.ApiError(body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _dailyDetailsState.value = DailyDetailsState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dailyDetailsState.value = DailyDetailsState.ApiError("Something went wrong")
            }
        }
    }

    /*===========================================================================================*/
    /*===========================================================================================*/

    private val _dealerCategoryState = MutableLiveData<DealerCategoryState>(DealerCategoryState.Idle)
    val dealerCategoryState: LiveData<DealerCategoryState> = _dealerCategoryState
    var cachedDealerCategoryList: List<DailyTourDealerCategory>? = null
    fun getDealerCategory(request: DailyTourDealerCategoryRequest) {
        cachedDealerCategoryList?.let {
            _dealerCategoryState.value = DealerCategoryState.Success(it)
            return
        }
        _dealerCategoryState.value = DealerCategoryState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getDealerCategoryAPI(request)
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _dealerCategoryState.value = DealerCategoryState.ApiError("Server error : ${response.code()}")
                    return@launch
                }
                when (body.status) {
                    "200" -> {
                        val list = body.result ?: emptyList()
                        if (list.isEmpty()) {
                            _dealerCategoryState.value = DealerCategoryState.ApiError("No record found")
                            return@launch
                        }

                        cachedDealerCategoryList = list
                        _dealerCategoryState.value = DealerCategoryState.Success(list)
                    }
                    "209" -> {
                        _dealerCategoryState.value = DealerCategoryState.ApiError(body.message ?: "No record found")
                    }
                    else -> {
                        _dealerCategoryState.value = DealerCategoryState.ApiError(body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _dealerCategoryState.value = DealerCategoryState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dealerCategoryState.value = DealerCategoryState.ApiError("Something went wrong")
            }
        }
    }

    /*==========================================================================================*/
    /*==========================================================================================*/
    private val _dealerNameState = MutableLiveData<DealerNameState>(DealerNameState.Idle)
    val dealerNameState: LiveData<DealerNameState> = _dealerNameState
    var cachedDealerNameList: List<DailyTourDealerName>? = null
    fun getDealerName(request: DailyTourDealerNameRequest) {
        cachedDealerNameList?.let {
            _dealerNameState.value = DealerNameState.Success(it)
            return
        }
        _dealerNameState.value = DealerNameState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getDealerNameAPI(request)
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _dealerNameState.value = DealerNameState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        val list = when (val res = body.result) {
                            is List<*> -> {
                                res.mapNotNull { item ->
                                    (item as? LinkedTreeMap<*, *>)?.let {
                                        Gson().fromJson(
                                            Gson().toJson(it),
                                            DailyTourDealerName::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _dealerNameState.value = DealerNameState.ApiError(body.message ?: "No approval list found")
                        } else {
                            cachedDealerNameList = list  // ✅ Save to cache
                            _dealerNameState.value = DealerNameState.Success(list)
                        }
                       /* val list = body.result
                            ?.map { item ->
                                item.copy(
                                    Name = item.Name
                                        ?.replace("u0026", "&")
                                        ?.replace("u0027", "'")
                                )
                            }
                            .orEmpty()
                        if (list.isEmpty()) {
                            _dealerNameState.value = DealerNameState.ApiError("No record found")
                            return@launch
                        }
                        cachedDealerNameList = list
                        _dealerNameState.value = DealerNameState.Success(list)*/
                    }

                    "209" -> {
                        _dealerNameState.value = DealerNameState.ApiError(body.message ?: "No record found")
                    }
                    else -> {
                        _dealerNameState.value = DealerNameState.ApiError(body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _dealerNameState.value = DealerNameState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dealerNameState.value = DealerNameState.ApiError("Something went wrong")
            }
        }
    }

    /*======================================================================================*/
    /*======================================================================================*/

    private val _districtState = MutableLiveData<DistrictState>(DistrictState.Idle)
    val districtState: LiveData<DistrictState> = _districtState
    var cachedDistrictList: List<DailyTourDistrict>? = null
    fun getDistrictList(request: DailyTourDistrictRequest) {
        cachedDistrictList?.let {
            _districtState.value = DistrictState.Success(it)
            return
        }
        _districtState.value = DistrictState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getDistrictAPI(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _districtState.value = DistrictState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        val list = body.result ?: emptyList()
                        if (list.isEmpty()) {
                            _districtState.value = DistrictState.ApiError("No record found")
                            return@launch
                        }
                        cachedDistrictList = list
                        _districtState.value = DistrictState.Success(list)
                    }
                    "209" -> {
                        _districtState.value = DistrictState.ApiError(body.message ?: "No record found")
                    }
                    else -> {
                        _districtState.value = DistrictState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _districtState.value = DistrictState.NetworkError("Something went wrong")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _districtState.value = DistrictState.ApiError("Something went wrong")
            }
        }
    }

    /*==================================================================================*/
    /*==================================================================================*/
    private val _insertDailyDetailsState = MutableLiveData<InsertDailyDetailsState>(InsertDailyDetailsState.Idle)
    val insertDailyDetailsState: LiveData<InsertDailyDetailsState> = _insertDailyDetailsState

    fun insertDailyDetails(request: DailyTourAddDetailsRequest) {
        _insertDailyDetailsState.value = InsertDailyDetailsState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertDailyDetailsAPI(request,context)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertDailyDetailsState.value = InsertDailyDetailsState.Success(body)
                    }
                    "209" -> {
                        _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError(body.message ?: "No record found")
                    }
                    else -> {
                        _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertDailyDetailsState.value = InsertDailyDetailsState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError("Something went wrong")
            }
        }
    }

    fun insertDailyDukeDetails(request: DailyTourAddDetailsDukeRequest) {
        _insertDailyDetailsState.value = InsertDailyDetailsState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertDailyDetailsDukeAPI(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertDailyDetailsState.value = InsertDailyDetailsState.Success(body)
                    }
                    "209" -> {
                        _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError(body.message ?: "No record found")
                    }
                    else -> {
                        _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertDailyDetailsState.value = InsertDailyDetailsState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertDailyDetailsState.value = InsertDailyDetailsState.ApiError("Something went wrong")
            }
        }
    }

    /*================================== Flotech ==========================================*/
    /*============================= Daily Tour Details ======================================*/
    private val _insertDailyTourFlotechUiState = MutableLiveData<InsertDailyTourFlotechUiState>()
    val insertDailyTourFlotechUiState: LiveData<InsertDailyTourFlotechUiState> = _insertDailyTourFlotechUiState
    fun insertDailyTourDetailsFlotech(request: DailyTourFlotechRequest) {
        _insertDailyTourFlotechUiState.value = InsertDailyTourFlotechUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertDailyTourDetailsFlotech(request)

                if (!response.isSuccessful) {
                    _insertDailyTourFlotechUiState.value = InsertDailyTourFlotechUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _insertDailyTourFlotechUiState.value = InsertDailyTourFlotechUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertDailyTourFlotechUiState.value = InsertDailyTourFlotechUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _insertDailyTourFlotechUiState.value = InsertDailyTourFlotechUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _insertDailyTourFlotechUiState.value = InsertDailyTourFlotechUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertDailyTourFlotechUiState.postValue(InsertDailyTourFlotechUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertDailyTourFlotechUiState.postValue(InsertDailyTourFlotechUiState.ApiError("Something went wrong"))
            }
        }
    }

}