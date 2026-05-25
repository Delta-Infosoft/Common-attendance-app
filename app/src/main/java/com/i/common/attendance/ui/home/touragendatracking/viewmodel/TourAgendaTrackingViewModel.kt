package com.i.common.attendance.ui.home.touragendatracking.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.i.common.attendance.network.request.BusinessCenterNameRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.InsertJtdDetailsRequest
import com.i.common.attendance.network.request.InsertObjectiveRequest
import com.i.common.attendance.network.request.SubmitSundayApprovalRequest
import com.i.common.attendance.network.request.TourAgendaDealerNameRequest
import com.i.common.attendance.network.request.TourAgendaTrackingAddMeetingRequest
import com.i.common.attendance.network.request.TourAgendaTrackingDistrictRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetFactRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetObjectRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetRunningTaskDetailsRequest
import com.i.common.attendance.network.request.TourAgendaTrackingInOutDetailsRequest
import com.i.common.attendance.network.request.TourAgendaTrackingServiceCenterRequest
import com.i.common.attendance.network.request.TourAgendaTrackingStartEndMeetingRequest
import com.i.common.attendance.network.request.TourAgendaTrackingSubDealerNameRequest
import com.i.common.attendance.network.request.ValidateSundayRequest
import com.i.common.attendance.network.request.WeekOffRequest
import com.i.common.attendance.network.response.BusinessCenterName
import com.i.common.attendance.network.response.DailyTourDealerCategory
import com.i.common.attendance.network.response.DistrictTourAgendaTracking
import com.i.common.attendance.network.response.GetState
import com.i.common.attendance.network.response.InOutRecords
import com.i.common.attendance.network.response.SundayRequestItem
import com.i.common.attendance.network.response.TourAgendaTrackingDealerName
import com.i.common.attendance.network.response.TourAgendaTrackingFacets
import com.i.common.attendance.network.response.TourAgendaTrackingObjectiveItem
import com.i.common.attendance.network.response.TourAgendaTrackingRunningTaskDetails
import com.i.common.attendance.network.response.TourAgendaTrackingServiceCenter
import com.i.common.attendance.network.response.TourAgendaTrackingSubDealerName
import com.i.common.attendance.network.response.UserRightsItem
import com.i.common.attendance.network.response.ValidateSundayResult
import com.i.common.attendance.network.response.WeekOffItem
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourRepository
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TourAgendaTrackingViewModel  @Inject constructor(
    private val repositoryWithViewBaseUrl: TourAgendaTrackingWithBaseViewUrlRepository,
    private val repositoryWithDefaultBaseUrl: TourAgendaTrackingWithDefaultBaseUrlRepository,
    private val repoDailyTour : DailyTourRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _getStateUiState = MutableLiveData<GetStateUiState>()
    val getStateUiState: LiveData<GetStateUiState> = _getStateUiState
    var cachedStateList: List<GetState>? = null

    fun loadStateList(request: GetStateRequest) {
        cachedStateList?.let {
            _getStateUiState.value = GetStateUiState.Success(it)
            return
        }

        _getStateUiState.value = GetStateUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getState(request)

                if (!response.isSuccessful) {
                    _getStateUiState.value = GetStateUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getStateUiState.value = GetStateUiState.ApiError("Empty server response")
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
                                            GetState::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getStateUiState.value = GetStateUiState.ApiError(body.message ?: "No state found")
                        } else {
                            cachedStateList = list
                            _getStateUiState.value = GetStateUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getStateUiState.value = GetStateUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getStateUiState.value = GetStateUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getStateUiState.postValue(GetStateUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getStateUiState.postValue(GetStateUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*==========================================================================================*/
    private val _getDistrictUiState = MutableLiveData<GetDistrictUiState>()
    val getDistrictUiState: LiveData<GetDistrictUiState> = _getDistrictUiState
    var cachedDistrictList: List<DistrictTourAgendaTracking>? = null
    fun loadDistrictList(request: TourAgendaTrackingDistrictRequest) {

        // ✅ Return cached data, skip API call
        cachedDistrictList?.let {
            _getDistrictUiState.value = GetDistrictUiState.Success(it)
            return
        }

        _getDistrictUiState.value = GetDistrictUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getTourAgendaTrackingDistrictAPI(request)

                if (!response.isSuccessful) {
                    _getDistrictUiState.value = GetDistrictUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getDistrictUiState.value = GetDistrictUiState.ApiError("Empty server response")
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
                                            DistrictTourAgendaTracking::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getDistrictUiState.value = GetDistrictUiState.ApiError(body.message ?: "No district found")
                        } else {
                            cachedDistrictList = list  // ✅ Save to cache
                            _getDistrictUiState.value = GetDistrictUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getDistrictUiState.value = GetDistrictUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getDistrictUiState.value = GetDistrictUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getDistrictUiState.postValue(GetDistrictUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getDistrictUiState.postValue(GetDistrictUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshDistrictList(request: TourAgendaTrackingDistrictRequest) {
        cachedDistrictList = null
        loadDistrictList(request)
    }

    /*===========================================================================================*/
    private val _getStationUiState = MutableLiveData<GetStationUiState>()
    val getStationUiState: LiveData<GetStationUiState> = _getStationUiState
    var cachedStationList: List<BusinessCenterName>? = null
    fun loadStationList(request: BusinessCenterNameRequest) {

        // ✅ Return cached data, skip API call
        cachedStationList?.let {
            _getStationUiState.value = GetStationUiState.Success(it)
            return
        }

        _getStationUiState.value = GetStationUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getStation(request)

                if (!response.isSuccessful) {
                    _getStationUiState.value = GetStationUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getStationUiState.value = GetStationUiState.ApiError("Empty server response")
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
                                            BusinessCenterName::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getStationUiState.value = GetStationUiState.ApiError(body.message ?: "No station found")
                        } else {
                            cachedStationList = list  // ✅ Save to cache
                            _getStationUiState.value = GetStationUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getStationUiState.value = GetStationUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getStationUiState.value = GetStationUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getStationUiState.postValue(GetStationUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getStationUiState.postValue(GetStationUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshStationList(request: BusinessCenterNameRequest) {
        cachedStationList = null
        loadStationList(request)
    }
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
                val response = repoDailyTour.getDealerCategoryAPI(request)
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

    fun refreshDealerCategory(request: DailyTourDealerCategoryRequest) {
        cachedDealerCategoryList = null
        getDealerCategory(request)
    }

    /*=========================================================================================*/
    private val _getDealerNameUiState = MutableLiveData<GetDealerNameUiState>()
    val getDealerNameUiState: LiveData<GetDealerNameUiState> = _getDealerNameUiState
    var cachedDealerNameList: List<TourAgendaTrackingDealerName>? = null
    fun loadDealerNameList(request: TourAgendaDealerNameRequest) {
        cachedDealerNameList?.let {
            _getDealerNameUiState.value = GetDealerNameUiState.Success(it)
            return
        }

        _getDealerNameUiState.value = GetDealerNameUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getDealerName(request)

                if (!response.isSuccessful) {
                    _getDealerNameUiState.value = GetDealerNameUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getDealerNameUiState.value = GetDealerNameUiState.ApiError("Empty server response")
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
                                            TourAgendaTrackingDealerName::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getDealerNameUiState.value = GetDealerNameUiState.ApiError(body.message ?: "No dealer found")
                        } else {
                            cachedDealerNameList = list  // ✅ Save to cache
                            _getDealerNameUiState.value = GetDealerNameUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getDealerNameUiState.value = GetDealerNameUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getDealerNameUiState.value = GetDealerNameUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getDealerNameUiState.postValue(GetDealerNameUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getDealerNameUiState.postValue(GetDealerNameUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshDealerNameList(request: TourAgendaDealerNameRequest) {
        cachedDealerNameList = null
        loadDealerNameList(request)
    }

    /*========================================================================================*/
    private val _getSubDealerNameUiState = MutableLiveData<GetSubDealerNameUiState>()
    val getSubDealerNameUiState: LiveData<GetSubDealerNameUiState> = _getSubDealerNameUiState
    var cachedSubDealerNameList: List<TourAgendaTrackingSubDealerName>? = null
    fun loadSubDealerNameList(request: TourAgendaTrackingSubDealerNameRequest) {

        // ✅ Return cached data, skip API call
        cachedSubDealerNameList?.let {
            _getSubDealerNameUiState.value = GetSubDealerNameUiState.Success(it)
            return
        }

        _getSubDealerNameUiState.value = GetSubDealerNameUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getSubDealerName(request)

                if (!response.isSuccessful) {
                    _getSubDealerNameUiState.value = GetSubDealerNameUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getSubDealerNameUiState.value = GetSubDealerNameUiState.ApiError("Empty server response")
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
                                            TourAgendaTrackingSubDealerName::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getSubDealerNameUiState.value = GetSubDealerNameUiState.ApiError(body.message ?: "No sub dealer found")
                        } else {
                            cachedSubDealerNameList = list  // ✅ Save to cache
                            _getSubDealerNameUiState.value = GetSubDealerNameUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getSubDealerNameUiState.value = GetSubDealerNameUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getSubDealerNameUiState.value = GetSubDealerNameUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getSubDealerNameUiState.postValue(GetSubDealerNameUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getSubDealerNameUiState.postValue(GetSubDealerNameUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshSubDealerNameList(request: TourAgendaTrackingSubDealerNameRequest) {
        cachedSubDealerNameList = null
        loadSubDealerNameList(request)
    }

    /*==========================================================================================*/
    private val _getServiceCenterUiState = MutableLiveData<GetServiceCenterUiState>()
    val getServiceCenterUiState: LiveData<GetServiceCenterUiState> = _getServiceCenterUiState
    var cachedServiceCenterList: List<TourAgendaTrackingServiceCenter>? = null
    fun loadServiceCenterList(request: TourAgendaTrackingServiceCenterRequest) {

        // ✅ Return cached data, skip API call
        cachedServiceCenterList?.let {
            _getServiceCenterUiState.value = GetServiceCenterUiState.Success(it)
            return
        }

        _getServiceCenterUiState.value = GetServiceCenterUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getServiceCenters(request)

                if (!response.isSuccessful) {
                    _getServiceCenterUiState.value = GetServiceCenterUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getServiceCenterUiState.value = GetServiceCenterUiState.ApiError("Empty server response")
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
                                            TourAgendaTrackingServiceCenter::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getServiceCenterUiState.value = GetServiceCenterUiState.ApiError(body.message ?: "No service center found")
                        } else {
                            cachedServiceCenterList = list  // ✅ Save to cache
                            _getServiceCenterUiState.value = GetServiceCenterUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getServiceCenterUiState.value = GetServiceCenterUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getServiceCenterUiState.value = GetServiceCenterUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getServiceCenterUiState.postValue(GetServiceCenterUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getServiceCenterUiState.postValue(GetServiceCenterUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshServiceCenterList(request: TourAgendaTrackingServiceCenterRequest) {
        cachedServiceCenterList = null
        loadServiceCenterList(request)
    }

    /*==========================================================================================*/
    private val _getRunningTaskDetailsUiState = MutableLiveData<GetRunningTaskDetailsUiState>()
    val getRunningTaskDetailsUiState: LiveData<GetRunningTaskDetailsUiState> = _getRunningTaskDetailsUiState
    //var cachedRunningTaskDetailsList: List<TourAgendaTrackingRunningTaskDetails>? = null
    fun loadRunningTaskDetails(request: TourAgendaTrackingGetRunningTaskDetailsRequest) {
       /* cachedRunningTaskDetailsList?.let {
            _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.Success(it)
            return
        }*/

        _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getRunningTaskDetails(request)

                if (!response.isSuccessful) {
                    _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.ApiError("Empty server response")
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
                                            TourAgendaTrackingRunningTaskDetails::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.ApiError(body.message ?: "No running task found")
                        } else {
                            //cachedRunningTaskDetailsList = list  // ✅ Save to cache
                            _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getRunningTaskDetailsUiState.value = GetRunningTaskDetailsUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getRunningTaskDetailsUiState.postValue(GetRunningTaskDetailsUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getRunningTaskDetailsUiState.postValue(GetRunningTaskDetailsUiState.ApiError("Something went wrong"))
            }
        }
    }
    /*fun refreshRunningTaskDetails(request: TourAgendaTrackingGetRunningTaskDetailsRequest) {
        cachedRunningTaskDetailsList = null
        loadRunningTaskDetails(request)
    }*/

    /*===========================================================================================*/
    private val _getFacetsUiState = MutableLiveData<GetFacetsUiState>()
    val getFacetsUiState: LiveData<GetFacetsUiState> = _getFacetsUiState
    fun loadFacets(request: TourAgendaTrackingGetFactRequest) {

        val facetType = when (request.parameter) {
            "MENU_DealerEntry"         -> FacetType.DEALER_ENTRY
            "ActivityMenuWithAddList"  -> FacetType.ACTIVITY_MENU
            "Menu_TeamAttendance"      -> FacetType.TEAM_ATTENDACE
            else                       -> FacetType.DEALER_ENTRY
        }
        _getFacetsUiState.value = GetFacetsUiState.Loading(facetType)
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.getFacets(request)

                if (!response.isSuccessful) {
                    _getFacetsUiState.value = GetFacetsUiState.ApiError(facetType, "Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getFacetsUiState.value = GetFacetsUiState.ApiError(facetType, "Empty server response")
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
                                            TourAgendaTrackingFacets::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getFacetsUiState.value = GetFacetsUiState.ApiError(facetType, body.message ?: "No facets found")
                        } else {
                            _getFacetsUiState.value = GetFacetsUiState.Success(facetType, list)
                        }
                    }

                    "209" -> {
                        _getFacetsUiState.value = GetFacetsUiState.ApiError(facetType, body.message ?: "No Record Found")
                    }

                    else -> {
                        _getFacetsUiState.value = GetFacetsUiState.ApiError(facetType, body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getFacetsUiState.postValue(GetFacetsUiState.NetworkError(facetType, "Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getFacetsUiState.postValue(GetFacetsUiState.ApiError(facetType, "Something went wrong"))
            }
        }
    }

    /*=========================================================================================*/
    private val _getObjectiveUiState = MutableLiveData<GetObjectiveUiState>()
    val getObjectiveUiState: LiveData<GetObjectiveUiState> = _getObjectiveUiState
    private var cachedObjectiveList: List<TourAgendaTrackingObjectiveItem>? = null
    fun loadObjectiveList(request: TourAgendaTrackingGetObjectRequest) {
        cachedObjectiveList?.let {
            _getObjectiveUiState.value = GetObjectiveUiState.Success(it)
            return
        }

        _getObjectiveUiState.value = GetObjectiveUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getObjective(request)

                if (!response.isSuccessful) {
                    _getObjectiveUiState.value = GetObjectiveUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getObjectiveUiState.value = GetObjectiveUiState.ApiError("Empty server response")
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
                                            TourAgendaTrackingObjectiveItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getObjectiveUiState.value = GetObjectiveUiState.ApiError(body.message ?: "No objective found")
                        } else {
                            cachedObjectiveList = list
                            _getObjectiveUiState.value = GetObjectiveUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getObjectiveUiState.value = GetObjectiveUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getObjectiveUiState.value = GetObjectiveUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getObjectiveUiState.postValue(GetObjectiveUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getObjectiveUiState.postValue(GetObjectiveUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshObjectiveList(request: TourAgendaTrackingGetObjectRequest) {
        cachedObjectiveList = null
        loadObjectiveList(request)
    }

    /*========================================================================================*/
    private val _startEndMeetingUiState = MutableLiveData<StartEndMeetingUiState>()
    val startEndMeetingUiState: LiveData<StartEndMeetingUiState> = _startEndMeetingUiState
    fun startEndMeeting(request: TourAgendaTrackingStartEndMeetingRequest, meetingType: MeetingType) {
        _startEndMeetingUiState.value = StartEndMeetingUiState.Loading(meetingType)
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.startEndMeeting(request)

                if (!response.isSuccessful) {
                    _startEndMeetingUiState.value = StartEndMeetingUiState.ApiError(meetingType, "Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _startEndMeetingUiState.value = StartEndMeetingUiState.ApiError(meetingType, "Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _startEndMeetingUiState.value = StartEndMeetingUiState.Success(meetingType, body.message ?: "Success")
                    }
                    "209" -> {
                        _startEndMeetingUiState.value = StartEndMeetingUiState.ApiError(meetingType, body.message ?: "No Record Found")
                    }
                    else -> {
                        _startEndMeetingUiState.value = StartEndMeetingUiState.ApiError(meetingType, body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _startEndMeetingUiState.postValue(StartEndMeetingUiState.NetworkError(meetingType, "Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _startEndMeetingUiState.postValue(StartEndMeetingUiState.ApiError(meetingType, "Something went wrong"))
            }
        }
    }

    /*=========================================================================================*/
    private val _insertTourTrackingUiState = MutableLiveData<InsertTourTrackingUiState>()
    val insertTourTrackingUiState: LiveData<InsertTourTrackingUiState> = _insertTourTrackingUiState
    fun insertTourTrackingDetails(request: TourAgendaTrackingAddMeetingRequest) {

        _insertTourTrackingUiState.value = InsertTourTrackingUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.insertTourTrackingDetails(request)

                if (!response.isSuccessful) {
                    _insertTourTrackingUiState.value = InsertTourTrackingUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _insertTourTrackingUiState.value = InsertTourTrackingUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertTourTrackingUiState.value = InsertTourTrackingUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _insertTourTrackingUiState.value = InsertTourTrackingUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _insertTourTrackingUiState.value = InsertTourTrackingUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertTourTrackingUiState.postValue(InsertTourTrackingUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertTourTrackingUiState.postValue(InsertTourTrackingUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*=====================================================================================*/
    private val _getInOutDetailsUiState = MutableLiveData<GetInOutDetailsUiState>()
    val getInOutDetailsUiState: LiveData<GetInOutDetailsUiState> = _getInOutDetailsUiState
    var cachedInOutDetailsList: List<InOutRecords>? = null
    fun loadInOutDetails(request: TourAgendaTrackingInOutDetailsRequest) {
        cachedInOutDetailsList?.let {
            _getInOutDetailsUiState.value = GetInOutDetailsUiState.Success(it)
            return
        }
        _getInOutDetailsUiState.value = GetInOutDetailsUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.getInOutDetails(request)
                if (!response.isSuccessful) {
                    _getInOutDetailsUiState.value = GetInOutDetailsUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _getInOutDetailsUiState.value = GetInOutDetailsUiState.ApiError("Empty server response")
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
                                            InOutRecords::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getInOutDetailsUiState.value = GetInOutDetailsUiState.ApiError(body.message ?: "No records found")
                        } else {
                            cachedInOutDetailsList = list
                            _getInOutDetailsUiState.value = GetInOutDetailsUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getInOutDetailsUiState.value = GetInOutDetailsUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getInOutDetailsUiState.value = GetInOutDetailsUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getInOutDetailsUiState.postValue(GetInOutDetailsUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getInOutDetailsUiState.postValue(GetInOutDetailsUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshInOutDetails(request: TourAgendaTrackingInOutDetailsRequest) {
        cachedInOutDetailsList = null
        loadInOutDetails(request)
    }

    /*========================================================================================*/
    private val _insertObjectiveUiState = MutableLiveData<InsertObjectiveUiState>()
    val insertObjectiveUiState: LiveData<InsertObjectiveUiState> = _insertObjectiveUiState
    fun insertObjective(request: InsertObjectiveRequest) {
        _insertObjectiveUiState.value = InsertObjectiveUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.insertObjective(request)

                if (!response.isSuccessful) {
                    _insertObjectiveUiState.value = InsertObjectiveUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _insertObjectiveUiState.value = InsertObjectiveUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertObjectiveUiState.value = InsertObjectiveUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _insertObjectiveUiState.value = InsertObjectiveUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _insertObjectiveUiState.value = InsertObjectiveUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertObjectiveUiState.postValue(InsertObjectiveUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertObjectiveUiState.postValue(InsertObjectiveUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*===========================================================================================*/
    private val _getUserRightsUiState = MutableLiveData<GetUserRightsUiState>()
    val getUserRightsUiState: LiveData<GetUserRightsUiState> = _getUserRightsUiState
    var cachedUserRightsList: List<UserRightsItem>? = null
    fun loadUserRights(request: GetStateRequest) {
        cachedUserRightsList?.let {
            _getUserRightsUiState.value = GetUserRightsUiState.Success(it)
            return
        }
        _getUserRightsUiState.value = GetUserRightsUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.getUserRights(request)

                if (!response.isSuccessful) {
                    _getUserRightsUiState.value = GetUserRightsUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getUserRightsUiState.value = GetUserRightsUiState.ApiError("Empty server response")
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
                                            UserRightsItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getUserRightsUiState.value = GetUserRightsUiState.ApiError(body.message ?: "No user rights found")
                        } else {
                            cachedUserRightsList = list
                            _getUserRightsUiState.value = GetUserRightsUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getUserRightsUiState.value = GetUserRightsUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getUserRightsUiState.value = GetUserRightsUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getUserRightsUiState.postValue(GetUserRightsUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getUserRightsUiState.postValue(GetUserRightsUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshUserRights(request: GetStateRequest) {
        cachedUserRightsList = null
        loadUserRights(request)
    }

    /*=========================================================================================*/
    private val _insertJtdDetailsUiState = MutableLiveData<InsertJtdDetailsUiState>()
    val insertJtdDetailsUiState: LiveData<InsertJtdDetailsUiState> = _insertJtdDetailsUiState
    fun insertJtdDetails(request: InsertJtdDetailsRequest) {
        _insertJtdDetailsUiState.value = InsertJtdDetailsUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithViewBaseUrl.insertJtdDetails(request)
                if (!response.isSuccessful) {
                    _insertJtdDetailsUiState.value = InsertJtdDetailsUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }
                val body = response.body()
                if (body == null) {
                    _insertJtdDetailsUiState.value = InsertJtdDetailsUiState.ApiError("Empty server response")
                    return@launch
                }
                when (body.status) {
                    "200" -> {
                        _insertJtdDetailsUiState.value = InsertJtdDetailsUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _insertJtdDetailsUiState.value = InsertJtdDetailsUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _insertJtdDetailsUiState.value = InsertJtdDetailsUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertJtdDetailsUiState.postValue(InsertJtdDetailsUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertJtdDetailsUiState.postValue(InsertJtdDetailsUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*-------------------------------------------------------------------------------------*/
    /*-------------------------------------------------------------------------------------*/
    /*-------------------------------   Week OFF DAY   ------------------------------------*/
    private val _validateSundayUiState = MutableLiveData<ValidateSundayUiState>()
    val validateSundayUiState: LiveData<ValidateSundayUiState> = _validateSundayUiState
    fun validateSunday(request: ValidateSundayRequest) {

        _validateSundayUiState.value = ValidateSundayUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.validateSunday(request)

                if (!response.isSuccessful) {
                    _validateSundayUiState.value = ValidateSundayUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _validateSundayUiState.value = ValidateSundayUiState.ApiError("Empty server response")
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
                                            ValidateSundayResult::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _validateSundayUiState.value = ValidateSundayUiState.ApiError(body.message ?: "Validation failed")
                        } else {
                            _validateSundayUiState.value = ValidateSundayUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _validateSundayUiState.value = ValidateSundayUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _validateSundayUiState.value = ValidateSundayUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _validateSundayUiState.postValue(ValidateSundayUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _validateSundayUiState.postValue(ValidateSundayUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*============================================================================================*/
    private val _submitWeekOffUiState = MutableLiveData<SubmitWeekOffUiState>()
    val submitWeekOffUiState: LiveData<SubmitWeekOffUiState> = _submitWeekOffUiState
    fun submitWeekOff(request: WeekOffRequest) {
        _submitWeekOffUiState.value = SubmitWeekOffUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.submitWeekOff(request)

                if (!response.isSuccessful) {
                    _submitWeekOffUiState.value = SubmitWeekOffUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _submitWeekOffUiState.value = SubmitWeekOffUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _submitWeekOffUiState.value = SubmitWeekOffUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _submitWeekOffUiState.value = SubmitWeekOffUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _submitWeekOffUiState.value = SubmitWeekOffUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _submitWeekOffUiState.postValue(SubmitWeekOffUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _submitWeekOffUiState.postValue(SubmitWeekOffUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*============================================================================================*/
    private val _getWeekOffListUiState = MutableLiveData<GetWeekOffListUiState>()
    val getWeekOffListUiState: LiveData<GetWeekOffListUiState> = _getWeekOffListUiState
    fun loadWeekOffList(request: ValidateSundayRequest) {
        _getWeekOffListUiState.value = GetWeekOffListUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.getSundayRequestList(request)

                if (!response.isSuccessful) {
                    _getWeekOffListUiState.value = GetWeekOffListUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getWeekOffListUiState.value = GetWeekOffListUiState.ApiError("Empty server response")
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
                                            WeekOffItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getWeekOffListUiState.value = GetWeekOffListUiState.ApiError(body.message ?: "No week off records found")
                        } else {
                            _getWeekOffListUiState.value = GetWeekOffListUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getWeekOffListUiState.value = GetWeekOffListUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getWeekOffListUiState.value = GetWeekOffListUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getWeekOffListUiState.postValue(GetWeekOffListUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getWeekOffListUiState.postValue(GetWeekOffListUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*===========================================================================================*/
    private val _getSundayRequestListUiState = MutableLiveData<GetSundayRequestListUiState>()
    val getSundayRequestListUiState: LiveData<GetSundayRequestListUiState> = _getSundayRequestListUiState
    fun loadSundayRequestList() {
        _getSundayRequestListUiState.value = GetSundayRequestListUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.getApprovalListSundayRequest()

                if (!response.isSuccessful) {
                    _getSundayRequestListUiState.value = GetSundayRequestListUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getSundayRequestListUiState.value = GetSundayRequestListUiState.ApiError("Empty server response")
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
                                            SundayRequestItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getSundayRequestListUiState.value = GetSundayRequestListUiState.ApiError(body.message ?: "No sunday request found")
                        } else {
                            _getSundayRequestListUiState.value = GetSundayRequestListUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getSundayRequestListUiState.value = GetSundayRequestListUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getSundayRequestListUiState.value = GetSundayRequestListUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getSundayRequestListUiState.postValue(GetSundayRequestListUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getSundayRequestListUiState.postValue(GetSundayRequestListUiState.ApiError("Something went wrong"))
            }
        }
    }

    /*==========================================================================================*/
    private val _submitSundayApprovalUiState = MutableLiveData<SubmitSundayApprovalUiState>()
    val submitSundayApprovalUiState: LiveData<SubmitSundayApprovalUiState> = _submitSundayApprovalUiState
    fun submitSundayApproval(request: SubmitSundayApprovalRequest) {
        _submitSundayApprovalUiState.value = SubmitSundayApprovalUiState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryWithDefaultBaseUrl.submitSundayApproval(request)

                if (!response.isSuccessful) {
                    _submitSundayApprovalUiState.value = SubmitSundayApprovalUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _submitSundayApprovalUiState.value = SubmitSundayApprovalUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _submitSundayApprovalUiState.value = SubmitSundayApprovalUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _submitSundayApprovalUiState.value = SubmitSundayApprovalUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _submitSundayApprovalUiState.value = SubmitSundayApprovalUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _submitSundayApprovalUiState.postValue(SubmitSundayApprovalUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _submitSundayApprovalUiState.postValue(SubmitSundayApprovalUiState.ApiError("Something went wrong"))
            }
        }
    }

}