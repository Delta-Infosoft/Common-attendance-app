package com.i.common.attendance.ui.home.carairapproval.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.i.common.attendance.network.request.GetCityTypeListDukeRequest
import com.i.common.attendance.network.request.GetRatePerKMRequest
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.GetTravelDukeRequest
import com.i.common.attendance.network.request.GetVoucherNoDukeRequest
import com.i.common.attendance.network.request.InsertCarAirApprovalRequest
import com.i.common.attendance.network.request.SubmitSundayApprovalRequest
import com.i.common.attendance.network.request.UpdateCarAirApprovalStatusRequest
import com.i.common.attendance.network.response.CarAirApprovalItem
import com.i.common.attendance.network.response.EmployeeDataDuke
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.GetCities
import com.i.common.attendance.network.response.GetRatePerKM
import com.i.common.attendance.network.response.TravelData
import com.i.common.attendance.network.response.TravelingByItem
import com.i.common.attendance.network.response.VoucherNoData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CarAirApprovalViewModel @Inject constructor(
    private val repository: CarAirApprovalRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _getEmpDataUiState = MutableLiveData<GetEmpDataUiState>()
    val getEmpDataUiState: LiveData<GetEmpDataUiState> = _getEmpDataUiState
    fun loadEmpData(request: GetStateRequest) {
        _getEmpDataUiState.value = GetEmpDataUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getEmpData(request)

                if (!response.isSuccessful) {
                    _getEmpDataUiState.value = GetEmpDataUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getEmpDataUiState.value = GetEmpDataUiState.ApiError("Empty server response")
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
                                            EmployeeDataDuke::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getEmpDataUiState.value = GetEmpDataUiState.ApiError(body.message ?: "No employee data found")
                        } else {
                            _getEmpDataUiState.value = GetEmpDataUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getEmpDataUiState.value = GetEmpDataUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getEmpDataUiState.value = GetEmpDataUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getEmpDataUiState.postValue(GetEmpDataUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getEmpDataUiState.postValue(GetEmpDataUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*===========================================================================================*/

    private val _getVoucherNoUiState = MutableLiveData<GetVoucherNoUiState>()
    val getVoucherNoUiState: LiveData<GetVoucherNoUiState> = _getVoucherNoUiState
    fun loadVoucherNo(request: GetVoucherNoDukeRequest) {
        _getVoucherNoUiState.value = GetVoucherNoUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getNewVoucherNo(request)

                if (!response.isSuccessful) {
                    _getVoucherNoUiState.value = GetVoucherNoUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getVoucherNoUiState.value = GetVoucherNoUiState.ApiError("Empty server response")
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
                                            VoucherNoData::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        val voucherNo = list.firstOrNull()?.no
                        if (voucherNo.isNullOrEmpty()) {
                            _getVoucherNoUiState.value = GetVoucherNoUiState.ApiError(body.message ?: "Voucher no not found")
                        } else {
                            _getVoucherNoUiState.value = GetVoucherNoUiState.Success(voucherNo)
                        }
                    }

                    "209" -> {
                        _getVoucherNoUiState.value = GetVoucherNoUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getVoucherNoUiState.value = GetVoucherNoUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getVoucherNoUiState.postValue(GetVoucherNoUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getVoucherNoUiState.postValue(GetVoucherNoUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*===========================================================================================*/

    private val _getTravellingByCarUiState = MutableLiveData<GetTravellingByCarUiState>()
    val getTravellingByCarUiState: LiveData<GetTravellingByCarUiState> = _getTravellingByCarUiState
    var cachedTravellingByCarList: List<TravelingByItem>? = null
    fun loadTravellingByCar(request: GetTravelDukeRequest) {
        cachedTravellingByCarList?.let {
            _getTravellingByCarUiState.value = GetTravellingByCarUiState.Success(it)
            return
        }

        _getTravellingByCarUiState.value = GetTravellingByCarUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getTravellingByCar(request)

                if (!response.isSuccessful) {
                    _getTravellingByCarUiState.value = GetTravellingByCarUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getTravellingByCarUiState.value = GetTravellingByCarUiState.ApiError("Empty server response")
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
                                            TravelingByItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getTravellingByCarUiState.value = GetTravellingByCarUiState.ApiError(body.message ?: "No travel data found")
                        } else {
                            cachedTravellingByCarList = list  // ✅ Save to cache
                            _getTravellingByCarUiState.value = GetTravellingByCarUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getTravellingByCarUiState.value = GetTravellingByCarUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getTravellingByCarUiState.value = GetTravellingByCarUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getTravellingByCarUiState.postValue(GetTravellingByCarUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getTravellingByCarUiState.postValue(GetTravellingByCarUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshTravellingByCar(request: GetTravelDukeRequest) {
        cachedTravellingByCarList = null
        loadTravellingByCar(request)
    }

    /*===========================================================================================*/

    private val _getRatePerKMUiState = MutableLiveData<GetRatePerKMUiState>()
    val getRatePerKMUiState: LiveData<GetRatePerKMUiState> = _getRatePerKMUiState
    var cachedRatePerKMList: List<GetRatePerKM>? = null
    fun loadRatePerKM(request: GetRatePerKMRequest) {
        cachedRatePerKMList?.let {
            _getRatePerKMUiState.value = GetRatePerKMUiState.Success(it)
            return
        }

        _getRatePerKMUiState.value = GetRatePerKMUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getRateForPerKmCarAirApproval(request)

                if (!response.isSuccessful) {
                    _getRatePerKMUiState.value = GetRatePerKMUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getRatePerKMUiState.value = GetRatePerKMUiState.ApiError("Empty server response")
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
                                            GetRatePerKM::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getRatePerKMUiState.value = GetRatePerKMUiState.ApiError(body.message ?: "No rate data found")
                        } else {
                            cachedRatePerKMList = list
                            _getRatePerKMUiState.value = GetRatePerKMUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getRatePerKMUiState.value = GetRatePerKMUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getRatePerKMUiState.value = GetRatePerKMUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getRatePerKMUiState.postValue(GetRatePerKMUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getRatePerKMUiState.postValue(GetRatePerKMUiState.ApiError("Something went wrong"))
            }
        }
    }

    fun refreshRatePerKM(request: GetRatePerKMRequest) {
        cachedRatePerKMList = null
        loadRatePerKM(request)
    }

    /*===========================================================================================*/

    private val _getCityTypeUiState = MutableLiveData<GetCityTypeUiState>()
    val getCityTypeUiState: LiveData<GetCityTypeUiState> = _getCityTypeUiState
    var cachedCityTypeList: List<TravelData>? = null
    fun loadCityTypeList(request: GetCityTypeListDukeRequest) {
        cachedCityTypeList?.let {
            _getCityTypeUiState.value = GetCityTypeUiState.Success(it)
            return
        }

        _getCityTypeUiState.value = GetCityTypeUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getCityType(request)

                if (!response.isSuccessful) {
                    _getCityTypeUiState.value = GetCityTypeUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getCityTypeUiState.value = GetCityTypeUiState.ApiError("Empty server response")
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
                                            TravelData::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getCityTypeUiState.value = GetCityTypeUiState.ApiError(body.message ?: "No city type found")
                        } else {
                            cachedCityTypeList = list  // ✅ Save to cache
                            _getCityTypeUiState.value = GetCityTypeUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getCityTypeUiState.value = GetCityTypeUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getCityTypeUiState.value = GetCityTypeUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getCityTypeUiState.postValue(GetCityTypeUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getCityTypeUiState.postValue(GetCityTypeUiState.ApiError("Something went wrong"))
            }
        }
    }

    fun refreshCityTypeList(request: GetCityTypeListDukeRequest) {
        cachedCityTypeList = null
        loadCityTypeList(request)
    }

    /*=========================================================================================*/

    private val _getCityUiState = MutableLiveData<GetCityUiState>()
    val getCityUiState: LiveData<GetCityUiState> = _getCityUiState
    var cachedCityList: List<GetCities>? = null
    fun loadCityList() {
        cachedCityList?.let {
            _getCityUiState.value = GetCityUiState.Success(it)
            return
        }

        _getCityUiState.value = GetCityUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getCity()

                if (!response.isSuccessful) {
                    _getCityUiState.value = GetCityUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getCityUiState.value = GetCityUiState.ApiError("Empty server response")
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
                                            GetCities::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getCityUiState.value = GetCityUiState.ApiError(body.message ?: "No city found")
                        } else {
                            cachedCityList = list
                            _getCityUiState.value = GetCityUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getCityUiState.value = GetCityUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getCityUiState.value = GetCityUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getCityUiState.postValue(GetCityUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getCityUiState.postValue(GetCityUiState.ApiError("Something went wrong"))
            }
        }
    }

    fun refreshCityList() {
        cachedCityList = null
        loadCityList()
    }

    /*========================================================================================*/

    private val _insertCarApprovalUiState = MutableLiveData<InsertCarApprovalUiState>()
    val insertCarApprovalUiState: LiveData<InsertCarApprovalUiState> = _insertCarApprovalUiState
    fun insertCarApproval(request: InsertCarAirApprovalRequest) {

        _insertCarApprovalUiState.value = InsertCarApprovalUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertCarApproval(request)

                if (!response.isSuccessful) {
                    _insertCarApprovalUiState.value = InsertCarApprovalUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _insertCarApprovalUiState.value = InsertCarApprovalUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertCarApprovalUiState.value = InsertCarApprovalUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _insertCarApprovalUiState.value = InsertCarApprovalUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _insertCarApprovalUiState.value = InsertCarApprovalUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertCarApprovalUiState.postValue(InsertCarApprovalUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertCarApprovalUiState.postValue(InsertCarApprovalUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*=======================================================================================*/

    private val _getCarAirApprovalListUiState = MutableLiveData<GetCarAirApprovalListUiState>()
    val getCarAirApprovalListUiState: LiveData<GetCarAirApprovalListUiState> = _getCarAirApprovalListUiState
    private var cachedCarAirApprovalList: List<CarAirApprovalItem>? = null
    fun loadCarAirApprovalList() {
        cachedCarAirApprovalList?.let {
            _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.Success(it)
            return
        }

        _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getCarAirApprovalList()

                if (!response.isSuccessful) {
                    _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.ApiError("Empty server response")
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
                                            CarAirApprovalItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.ApiError(body.message ?: "No approval list found")
                        } else {
                            cachedCarAirApprovalList = list
                            _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getCarAirApprovalListUiState.value = GetCarAirApprovalListUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getCarAirApprovalListUiState.postValue(GetCarAirApprovalListUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getCarAirApprovalListUiState.postValue(GetCarAirApprovalListUiState.ApiError("Something went wrong"))
            }
        }
    }

    fun refreshCarAirApprovalList() {
        cachedCarAirApprovalList = null
        loadCarAirApprovalList()
    }

    /*======================================================================================*/

    private val _updateCarAirApprovalStatusUiState = MutableLiveData<UpdateCarAirApprovalStatusUiState>()
    val updateCarAirApprovalStatusUiState: LiveData<UpdateCarAirApprovalStatusUiState> = _updateCarAirApprovalStatusUiState

    fun updateCarAirApprovalStatus(request: UpdateCarAirApprovalStatusRequest) {

        _updateCarAirApprovalStatusUiState.value = UpdateCarAirApprovalStatusUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.updateCarAirApprovalStatus(request)

                if (!response.isSuccessful) {
                    _updateCarAirApprovalStatusUiState.value = UpdateCarAirApprovalStatusUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _updateCarAirApprovalStatusUiState.value = UpdateCarAirApprovalStatusUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _updateCarAirApprovalStatusUiState.value = UpdateCarAirApprovalStatusUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _updateCarAirApprovalStatusUiState.value = UpdateCarAirApprovalStatusUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _updateCarAirApprovalStatusUiState.value = UpdateCarAirApprovalStatusUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _updateCarAirApprovalStatusUiState.postValue(UpdateCarAirApprovalStatusUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _updateCarAirApprovalStatusUiState.postValue(UpdateCarAirApprovalStatusUiState.ApiError("Something went wrong"))
            }
        }
    }

}