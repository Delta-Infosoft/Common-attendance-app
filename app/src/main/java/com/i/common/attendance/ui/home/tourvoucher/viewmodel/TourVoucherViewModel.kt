package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.i.common.attendance.network.request.CheckPJCEntryRequest
import com.i.common.attendance.network.request.DropdownRequest
import com.i.common.attendance.network.request.EmployeeRequest
import com.i.common.attendance.network.request.FieldVisitRequest
import com.i.common.attendance.network.request.PjcDateRequest
import com.i.common.attendance.network.request.SaveTourVoucherRequest
import com.i.common.attendance.network.request.TourVoucherEditDataRequest
import com.i.common.attendance.network.request.TourVoucherRequest
import com.i.common.attendance.network.request.TravelAttachmentDeleteRequest
import com.i.common.attendance.network.request.TravelingByRequest
import com.i.common.attendance.network.response.DropdownItem
import com.i.common.attendance.network.response.EmployeeModel
import com.i.common.attendance.network.response.GetAttechmentTourVoucherRequest
import com.i.common.attendance.network.response.NameDropdownItem
import com.i.common.attendance.network.response.PJCEntryItem
import com.i.common.attendance.network.response.TourVoucherItem
import com.i.common.attendance.network.response.TravelingByItem
import com.i.common.attendance.network.response.UploadAttachmentItem
import com.i.common.attendance.ui.home.tourvoucher.data.DropdownType
import com.i.common.attendance.ui.home.tourvoucher.data.LocationDropdownType
import com.i.common.attendance.ui.home.viewmodel.TravelAttachmentDeleteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TourVoucherViewModel @Inject constructor(
    private val repository: TourVoucherRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _employeeState = MutableLiveData<EmployeeUiState>(EmployeeUiState.Idle)
    val employeeState: LiveData<EmployeeUiState> = _employeeState
    private var cachedEmployeeList: List<EmployeeModel>? = null

    // 🔹 TravelingBy state
    private val _travelingByState =
        MutableLiveData<TravelingByUiState>(TravelingByUiState.Idle)
    val travelingByState: LiveData<TravelingByUiState> = _travelingByState

    // 🔹 Cache
    private var cachedTravelingByList: List<TravelingByItem>? = null

    private val _tourVoucherState = MutableLiveData<TourVoucherUiState>(TourVoucherUiState.Idle)
    val tourVoucherState: LiveData<TourVoucherUiState> = _tourVoucherState

    private val _saveTourVoucherState = MutableLiveData<SaveTourVoucherUiState>(
        SaveTourVoucherUiState.Idle)

    val saveTourVoucherState: LiveData<SaveTourVoucherUiState> = _saveTourVoucherState

    private val _saveTourVoucherEditState = MutableLiveData<SaveTourVoucherUiEditState>(
        SaveTourVoucherUiEditState.Idle)

    val saveTourVoucherEditState: LiveData<SaveTourVoucherUiEditState> = _saveTourVoucherEditState

    private val _checkPJCEntryState = MutableLiveData<CheckPJCEntryUiState>(CheckPJCEntryUiState.Idle)

    val checkPJCEntryState: LiveData<CheckPJCEntryUiState> = _checkPJCEntryState

    fun loadEmployeeList(request: EmployeeRequest) {
        // ✅ Prevent duplicate API calls
        if (cachedEmployeeList != null) {
            _employeeState.value = EmployeeUiState.Success(cachedEmployeeList!!)
            return
        }
        _employeeState.value = EmployeeUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getEmployeeParam(request)
                if (!response.isSuccessful) {
                    _employeeState.value = EmployeeUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _employeeState.value = EmployeeUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        val list = if (body.result is List<*>) {
                            try {
                                body.result.filterIsInstance<EmployeeModel>()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } else {
                            emptyList()
                        }

                        if (list.isEmpty()) {
                            _employeeState.value = EmployeeUiState.ApiError(body.message ?: "No employee found")
                            return@launch
                        }

                        // ✅ Cache result
                        cachedEmployeeList = list
                        _employeeState.value = EmployeeUiState.Success(list)
                    }

                    "209" -> {
                        _employeeState.value = EmployeeUiState.ApiError(body.message ?: "No record found")
                    }

                    else -> {
                        _employeeState.value = EmployeeUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _employeeState.value = EmployeeUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _employeeState.value = EmployeeUiState.ApiError("Something went wrong")
            }
        }
    }

    // ✅ Optional getter (same as your reference)
    fun getCachedEmployeeList(): List<EmployeeModel>? = cachedEmployeeList

    fun loadTravelingByList(request: TravelingByRequest) {

        // ✅ Prevent duplicate API calls
        if (cachedTravelingByList != null) {
            _travelingByState.value = TravelingByUiState.Success(cachedTravelingByList!!)
            return
        }

        _travelingByState.value = TravelingByUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getTravelingByParam(request)
                val body = response.body()

                // ❌ HTTP / Server error
                if (!response.isSuccessful || body == null) {
                    _travelingByState.value = TravelingByUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                // ❌ API status handling
                when (body.status) {

                    "200" -> {

                        val list = if (
                            body.result != null &&
                            body.result.isJsonArray
                        ) {
                            Gson().fromJson<List<TravelingByItem>>(
                                body.result,
                                object : TypeToken<List<TravelingByItem>>() {}.type
                            )
                        } else {
                            emptyList<TravelingByItem>()
                        }

                        if (list.isEmpty()) {
                            _travelingByState.value = TravelingByUiState.ApiError("No traveling data found")
                            return@launch
                        }

                        cachedTravelingByList = list
                        _travelingByState.value =
                            TravelingByUiState.Success(list)
                    }

                    "209" -> {
                        _travelingByState.value = TravelingByUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _travelingByState.value = TravelingByUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _travelingByState.value = TravelingByUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _travelingByState.value = TravelingByUiState.ApiError("Something went wrong")
            }
        }
    }

    // ✅ Optional cache getter
    fun getCachedTravelingByList(): List<TravelingByItem>? = cachedTravelingByList

    /*===========================================================================================*/
    fun loadTourVoucherList(request: TourVoucherRequest) {
        _tourVoucherState.value = TourVoucherUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getTourVoucherList(request)
                if (!response.isSuccessful) {
                    _tourVoucherState.value = TourVoucherUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _tourVoucherState.value = TourVoucherUiState.ApiError("Empty server response")
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
                                            TourVoucherItem::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }
                        if (list.isEmpty()) {
                            _tourVoucherState.value = TourVoucherUiState.ApiError(body.message ?: "No tour voucher found")
                        } else {
                            _tourVoucherState.value = TourVoucherUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _tourVoucherState.value = TourVoucherUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _tourVoucherState.value = TourVoucherUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _tourVoucherState.value = TourVoucherUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _tourVoucherState.value = TourVoucherUiState.ApiError("Something went wrong")
            }
        }
    }

    fun saveTourVoucher(request: SaveTourVoucherRequest, ) {

        _saveTourVoucherState.value = SaveTourVoucherUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.insertTourVoucher(request, context)
                val body = response.body()

                // ❌ HTTP / server error
                if (!response.isSuccessful || body == null) {
                    _saveTourVoucherState.value = SaveTourVoucherUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                // ❌ API status handling
                when (body.status) {

                    "200" -> {
                        _saveTourVoucherState.value = SaveTourVoucherUiState.Success(body.message ?: "Saved successfully")
                    }

                    "209" -> {
                        _saveTourVoucherState.value = SaveTourVoucherUiState.ApiError(body.message ?: "Unable to save")
                    }

                    else -> {
                        _saveTourVoucherState.value = SaveTourVoucherUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _saveTourVoucherState.value = SaveTourVoucherUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _saveTourVoucherState.value = SaveTourVoucherUiState.ApiError("Something went wrong")
            }
        }
    }
    /*===========================================================================================*/
    fun saveTourEditVoucher(request: SaveTourVoucherRequest, ) {
        _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertTourEditVoucher(request, context)
                val body = response.body()

                // ❌ HTTP / server error
                if (!response.isSuccessful || body == null) {
                    _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                // ❌ API status handling
                when (body.status) {

                    "200" -> {
                        _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.Success(body.message ?: "Saved successfully")
                    }

                    "209" -> {
                        _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.ApiError(body.message ?: "Unable to save")
                    }

                    else -> {
                        _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _saveTourVoucherEditState.value = SaveTourVoucherUiEditState.ApiError("Something went wrong")
            }
        }
    }

    fun checkPJCEntry(request: CheckPJCEntryRequest) {
        _checkPJCEntryState.value = CheckPJCEntryUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.checkPJCEntry(request)
                if (!response.isSuccessful) {
                    _checkPJCEntryState.value = CheckPJCEntryUiState.Error
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _checkPJCEntryState.value = CheckPJCEntryUiState.Error
                    return@launch
                }
                when (body.status) {
                    "200" -> {
                        val gson = Gson()
                        val resultList: List<PJCEntryItem> =
                            try {
                                val json = gson.toJson(body.result)
                                gson.fromJson(json, object : TypeToken<List<PJCEntryItem>>() {}.type)
                            } catch (e: Exception) {
                                emptyList()
                            }

                        // =========================
                        // NEW TYPE VALIDATION
                        // =========================

                        val uncoverItem = resultList.firstOrNull {
                            it.type.equals("Uncover", ignoreCase = true)
                        }
                        if (uncoverItem != null) {
                            _checkPJCEntryState.value = CheckPJCEntryUiState.UncoverType("You are not eligible to fill this form for Uncover type")
                            return@launch
                        }

                        // =========================
                        // EXISTING LOGIC
                        // =========================
                        var isAllowed = true
                        val notFiledDates = mutableListOf<String>()
                        resultList.forEach { item ->
                            if (item.empId.isNullOrEmpty()) {
                                isAllowed = false
                                item.calendarDate?.let { notFiledDates.add(it) }
                            }
                        }

                        if (isAllowed) {
                            _checkPJCEntryState.value = CheckPJCEntryUiState.Allowed

                        } else {
                            _checkPJCEntryState.value = CheckPJCEntryUiState.NotAllowed(notFiledDates.joinToString(", "))
                        }
                    }

                    "209" -> {
                        _checkPJCEntryState.value = CheckPJCEntryUiState.NotAllowed(body.message ?: "")
                    }

                    else -> {
                        _checkPJCEntryState.value = CheckPJCEntryUiState.Error
                    }
                }

            } catch (e: IOException) {
                _checkPJCEntryState.value = CheckPJCEntryUiState.Error
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _checkPJCEntryState.value = CheckPJCEntryUiState.Error
            }
        }
    }

    fun resetCheckPJCState() {
        _checkPJCEntryState.value = CheckPJCEntryUiState.Idle
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private val _dropdownState = MutableLiveData<DropdownUiState>(DropdownUiState.Idle)
    val dropdownState: LiveData<DropdownUiState> = _dropdownState

    val dropdownCache = mutableMapOf<DropdownType, List<DropdownItem>>()

    fun loadDropdown(group: DropdownType) {

        // ✅ Use cache if available
        dropdownCache[group]?.let { cachedList ->
            _dropdownState.value = DropdownUiState.Success(group, cachedList)
            return
        }

        _dropdownState.value = DropdownUiState.Loading

        viewModelScope.launch {
            try {
                val request = DropdownRequest(Group = group.groupName)
                val response = repository.getCommonDropDownData(request)
                val body = response.body()

                // ❌ HTTP error
                if (!response.isSuccessful || body == null) {
                    _dropdownState.value =
                        DropdownUiState.ApiError(group, "Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = body.result
                            .filter { it.text.isNotBlank() }

                        if (list.isEmpty()) {
                            _dropdownState.value =
                                DropdownUiState.ApiError(group, "No data found")
                            return@launch
                        }

                        // ✅ Cache per group
                        dropdownCache[group] = list
                        _dropdownState.value =
                            DropdownUiState.Success(group, list)
                    }

                    "209" -> {
                        _dropdownState.value =
                            DropdownUiState.ApiError(group, body.message)
                    }

                    else -> {
                        _dropdownState.value =
                            DropdownUiState.ApiError(group, body.message)
                    }
                }

            } catch (e: IOException) {
                _dropdownState.value =
                    DropdownUiState.NetworkError(group, "Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dropdownState.value =
                    DropdownUiState.ApiError(group, "Something went wrong")
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private val _locationDropdownState = MutableLiveData<LocationDropdownUiState>(
        LocationDropdownUiState.Idle)
    val locationDropdownState: LiveData<LocationDropdownUiState> = _locationDropdownState
    val locationCache = mutableMapOf<LocationDropdownType, List<NameDropdownItem>>()

    fun loadLocationDropdown(type: LocationDropdownType) {

        // ✅ Cache hit
        locationCache[type]?.let { _locationDropdownState.value = LocationDropdownUiState.Success(type, it)
            return
        }
        // ✅ Type-aware loading
        _locationDropdownState.value = LocationDropdownUiState.Loading(type)
        viewModelScope.launch {
            try {
                val response = when (type) {
                    LocationDropdownType.DISTRICT -> repository.getCommonDropDownDistrict()
                    LocationDropdownType.CITY -> repository.getCommonDropDownCity()
                }

                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _locationDropdownState.value = LocationDropdownUiState.ApiError(type, "Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        val list = body.result.filter { it.name.isNotBlank() }.distinct()
                        if (list.isEmpty()) {
                            _locationDropdownState.value = LocationDropdownUiState.ApiError(type, "No data found")
                            return@launch
                        }

                        locationCache[type] = list
                        _locationDropdownState.value = LocationDropdownUiState.Success(type, list)
                    }

                    else -> {
                        _locationDropdownState.value = LocationDropdownUiState.ApiError(type, body.message)
                    }
                }

            } catch (e: IOException) {
                _locationDropdownState.value = LocationDropdownUiState.NetworkError(type, "Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _locationDropdownState.value = LocationDropdownUiState.ApiError(type, "Something went wrong")
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private val _employeeDataState = MutableLiveData<EmployeeUiState>(EmployeeUiState.Idle)
    val employeeDataState: LiveData<EmployeeUiState> = _employeeDataState
    private var cachedEmployeeDataList: List<EmployeeModel>? = null

    fun loadEmployeeDataList() {

        // ✅ Prevent duplicate API calls
        if (cachedEmployeeDataList != null) {
            _employeeState.value = EmployeeUiState.Success(cachedEmployeeDataList!!)
            return
        }
        _employeeState.value = EmployeeUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.apiGetEmployeeData()
                if (!response.isSuccessful) {
                    _employeeState.value = EmployeeUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _employeeState.value = EmployeeUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        val list = if (body.result is List<*>) {
                            try {
                                body.result.filterIsInstance<EmployeeModel>()
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } else {
                            emptyList()
                        }

                        if (list.isEmpty()) {
                            _employeeState.value = EmployeeUiState.ApiError(body.message ?: "No employee found")
                            return@launch
                        }

                        val distinctList = list.distinct()
                        // ✅ Cache result
                        cachedEmployeeDataList = distinctList
                        _employeeState.value = EmployeeUiState.Success(distinctList)
                    }

                    "209" -> {

                        _employeeState.value =
                            EmployeeUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _employeeState.value = EmployeeUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _employeeState.value = EmployeeUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _employeeState.value = EmployeeUiState.ApiError("Something went wrong")
            }
        }
    }

    fun getCachedEmployeeDataList(): List<EmployeeModel>? = cachedEmployeeDataList


    //////////////////////////////////////////////////////////////////////////////////////////////

    private val _fieldVisitState = MutableLiveData<FieldVisitUiState>(FieldVisitUiState.Idle)
    val fieldVisitState: LiveData<FieldVisitUiState> = _fieldVisitState

    fun saveFieldVisit(request: FieldVisitRequest) {

        _fieldVisitState.value = FieldVisitUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.insertFieldVisitData(request)
                val body = response.body()

                // ❌ HTTP error
                if (!response.isSuccessful || body == null) {
                    _fieldVisitState.value = FieldVisitUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        _fieldVisitState.value = FieldVisitUiState.Success(body.message?:"Successfully save")
                    }

                    "209" -> {
                        _fieldVisitState.value = FieldVisitUiState.ApiError(body.message?:"No record found")
                    }

                    else -> {
                        _fieldVisitState.value = FieldVisitUiState.ApiError(body.message?:"Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _fieldVisitState.value = FieldVisitUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _fieldVisitState.value = FieldVisitUiState.ApiError("Something went wrong")
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private val _backDateRightState = MutableLiveData<BackDateRightUiState>(BackDateRightUiState.Idle)

    val backDateRightState: LiveData<BackDateRightUiState> = _backDateRightState

    fun getBackDatedRight(request: PjcDateRequest) {
        _backDateRightState.value = BackDateRightUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getBackDatedRightAPI(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _backDateRightState.value = BackDateRightUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        val list = body.result ?: emptyList()
                        if (list.isEmpty()) {
                            _backDateRightState.value = BackDateRightUiState.Empty("No data found")
                            return@launch
                        }
                        _backDateRightState.value = BackDateRightUiState.Success(list)
                    }

                    "209" -> {
                        _backDateRightState.value = BackDateRightUiState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _backDateRightState.value = BackDateRightUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _backDateRightState.value = BackDateRightUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _backDateRightState.value = BackDateRightUiState.ApiError("Something went wrong")
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private val _pjcPermissionState = MutableLiveData<PjcPermissionUiState>(PjcPermissionUiState.Idle)

    val pjcPermissionState: LiveData<PjcPermissionUiState> = _pjcPermissionState

    fun getWithoutPJCTourRights(request: PjcDateRequest) {

        _pjcPermissionState.value = PjcPermissionUiState.Loading

        viewModelScope.launch {
            try {

                val response = repository.getWithoutPJCTourRightsAPI(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _pjcPermissionState.value = PjcPermissionUiState.ApiError("Server error : ${response.code()}")
                    return@launch
                }

                when (body.status) {

                    "200" -> {

                        val list = body.result ?: emptyList()
                        if (list.isEmpty()) {
                            _pjcPermissionState.value = PjcPermissionUiState.Empty("No permission data found")
                            return@launch
                        }

                        val permission = list.first()

                        _pjcPermissionState.value = PjcPermissionUiState.Success(permission)
                    }

                    "209" -> {
                        _pjcPermissionState.value =
                            PjcPermissionUiState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _pjcPermissionState.value = PjcPermissionUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _pjcPermissionState.value = PjcPermissionUiState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _pjcPermissionState.value = PjcPermissionUiState.ApiError("Something went wrong")
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private val _tourDetailsState = MutableLiveData<TourDetailsState>(TourDetailsState.Idle)
    val tourDetailsState: LiveData<TourDetailsState> = _tourDetailsState

    fun getTourDetails(request: TourVoucherEditDataRequest) {
        _tourDetailsState.value = TourDetailsState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getTourDetailsAPI(request)
                if (!response.isSuccessful) {
                    _tourDetailsState.value = TourDetailsState.Error("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _tourDetailsState.value = TourDetailsState.Error("Empty server response")
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
                                            TourVoucherItem::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _tourDetailsState.value = TourDetailsState.Empty(body.message ?: "No tour details found")
                            return@launch
                        }

                        val item = list.first()
                        _tourDetailsState.value = TourDetailsState.Success(item)
                    }

                    "209" -> {
                        _tourDetailsState.value = TourDetailsState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _tourDetailsState.value = TourDetailsState.Error(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _tourDetailsState.value = TourDetailsState.NetworkError("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _tourDetailsState.value = TourDetailsState.Error("Something went wrong")
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private val _attachmentState = MutableLiveData<AttachmentState>()
    val attachmentState: LiveData<AttachmentState> = _attachmentState

    fun getAttachmentFileParam(request: GetAttechmentTourVoucherRequest, type: AttachmentType) {
        viewModelScope.launch {
            _attachmentState.value = AttachmentState.Loading
            try {
                val response = repository.getAttachmentFileParam(request)
                if (!response.isSuccessful) {
                    _attachmentState.value = AttachmentState.Error(type, "Server error (${response.code()})")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _attachmentState.value = AttachmentState.Error(type, "Empty server response")
                    return@launch
                }
                when (body.status) {
                    "200" -> {
                        val list = body.result as? List<UploadAttachmentItem> ?: emptyList()
                        if (list.isEmpty()) {
                            _attachmentState.value = AttachmentState.Empty(type, "No attachment found")
                        } else {
                            _attachmentState.value = AttachmentState.Success(type, list)
                        }
                    }
                    "209" -> {
                        _attachmentState.value = AttachmentState.Empty(type, body.message ?: "No Record Found")
                    }
                    else -> {
                        _attachmentState.value = AttachmentState.Error(type, body.message ?: "Something went wrong")
                    }
                }
            } catch (e: IOException) {
                _attachmentState.value = AttachmentState.Error(type, "No internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _attachmentState.value = AttachmentState.Error(type, "Unexpected error occurred")
            }
        }
    }
    /*========================== File Delete ============================= */
    private val _deleteState = MutableLiveData<TravelAttachmentDeleteUiState>(TravelAttachmentDeleteUiState.Idle)
    val deleteState: LiveData<TravelAttachmentDeleteUiState> = _deleteState
    fun deleteTravelAttachment(request: TravelAttachmentDeleteRequest) {

        viewModelScope.launch {

            _deleteState.value = TravelAttachmentDeleteUiState.Loading

            try {
                val response = repository.deleteTravelAttachment(request)

                if (!response.isSuccessful) {
                    _deleteState.value = TravelAttachmentDeleteUiState.Error("Server error (${response.code()})")
                    return@launch
                }

                val body = response.body()

                if (body == null) {
                    _deleteState.value = TravelAttachmentDeleteUiState.Error("Empty server response")
                    return@launch
                }

                when (body.status) {

                    "200" -> {
                        _deleteState.value =
                            TravelAttachmentDeleteUiState.Success(
                                message = body.message
                            )
                    }

                    "209" -> {
                        _deleteState.value = TravelAttachmentDeleteUiState.Error(body.message)
                    }

                    else -> {
                        _deleteState.value = TravelAttachmentDeleteUiState.Error(body.message)
                    }
                }

            } catch (e: IOException) {
                _deleteState.value = TravelAttachmentDeleteUiState.Error("No internet connection")

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _deleteState.value = TravelAttachmentDeleteUiState.Error("Unexpected error occurred")
            }
        }
    }


}
