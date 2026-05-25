package com.i.common.attendance.ui.home.pjc.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.i.common.attendance.network.request.GetDistrictPjcRequest
import com.i.common.attendance.network.request.GetPjcEventRequest
import com.i.common.attendance.network.request.GetPjcRequest
import com.i.common.attendance.network.request.GetSqlQueryForDropdownParamRequest
import com.i.common.attendance.network.request.InsertPjcEventRequest
import com.i.common.attendance.network.request.ReasonListParamsRequest
import com.i.common.attendance.network.response.GetDistrictPjcList
import com.i.common.attendance.network.response.LoadDropDownList
import com.i.common.attendance.network.response.PlanForList
import com.i.common.attendance.network.response.ReasonList
import com.i.common.attendance.network.response.ReasonListParamsList
import com.i.common.attendance.network.request.ReasonRequest
import com.i.common.attendance.network.response.EventsCalModel
import com.i.common.attendance.network.response.HolidayWeekOffDto
import com.i.common.attendance.network.response.HolidayWeekOffModel
import com.i.common.attendance.network.response.HolidayWeekOffResponse
import com.i.common.attendance.network.response.PJCItem
import com.i.common.attendance.network.response.PartyRemarkDto
import com.i.common.attendance.network.response.PaymentFollowUpDto
import com.i.common.attendance.network.response.PjcEventDto
import com.i.common.attendance.network.response.PjcEventFullData
import com.i.common.attendance.network.response.PjcResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: PjcCalenderRepository, private val watermanPjcCalenderRepository: WatermanPjcCalenderRepository
) : ViewModel() {

    private val gson = Gson()

    private val _pjcState = MutableLiveData<PJCState>()
    val pjcState: LiveData<PJCState> = _pjcState

    private val _pjcEventState = MutableLiveData<PjcEventState>()
    val pjcEventState: LiveData<PjcEventState> = _pjcEventState

    private val _planForListState = MutableLiveData<PlanForListState>()
    val planForListState: LiveData<PlanForListState> = _planForListState
    private var cachedList: List<PlanForList>? = null

    private val _reasonState = MutableLiveData<ReasonState>()
    val reasonState: LiveData<ReasonState> = _reasonState

    private var cachedReasonList: List<ReasonList>? = null

    private val _districtPjcState = MutableLiveData<DistrictPjcState>()
    val districtPjcState: LiveData<DistrictPjcState> = _districtPjcState

    private var cachedDistrictList: List<GetDistrictPjcList>? = null

    private val _reasonListParamsState = MutableLiveData<ReasonListParamsState>()
    val reasonListParamsState: LiveData<ReasonListParamsState> = _reasonListParamsState

    private var cachedReasonListParams: List<ReasonListParamsList>? = null

    private val _dropdownState = MutableLiveData<DropdownState>()
    val dropdownState: LiveData<DropdownState> = _dropdownState

    private var cachedDropdownList: List<LoadDropDownList>? = null

    private val _insertPjcState = MutableLiveData<InsertPjcState>()
    val insertPjcState: LiveData<InsertPjcState> = _insertPjcState

    // ----------------------------------------------------
    // Calendar Data
    // ----------------------------------------------------
    fun loadCalendarData(request: GetPjcRequest) {
        viewModelScope.launch {
            _pjcState.value = PJCState.Loading

            try {
                // ✅ Run both APIs in parallel
                val pjcDeferred = async { repository.getPjc(request) }
                //val holidayDeferred = async { repository.getHolidayWeekOffParam(request) }

                val pjcResponse = pjcDeferred.await()
                //val holidayResponse = holidayDeferred.await()

                // ---------- PJC ----------
                val pjcList = pjcResponse.body()?.toEventsList() ?: emptyList()

                // ---------- Holiday ----------
               // val holidayList = holidayResponse.body()?.toEventsList()?:emptyList()
                   /* if (holidayResponse.isSuccessful && holidayResponse.body()?.status == "200") {
                        holidayResponse.body()?.result ?: emptyList()
                    } else {
                        emptyList()
                    }*/

                if (pjcList.isEmpty()) {
                    _pjcState.value = PJCState.Empty("No Record Found")
                } else {
                    _pjcState.value = PJCState.Success(
                        pjcList = pjcList,
                        holidayList = emptyList()
                    )
                }

            } catch (e: Exception) {
                _pjcState.value = PJCState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    // ----------------------------------------------------
    // PJC EVENT (MULTI RESPONSE SAFE)
    // ----------------------------------------------------

    fun getPjcEvent(date: String, mobileNo: String) {
        _pjcEventState.value = PjcEventState.Loading

        viewModelScope.launch {
            try {
                val response = watermanPjcCalenderRepository.getPjcEvent(GetPjcEventRequest(date, mobileNo))
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _pjcEventState.value = PjcEventState.Error("${response.code()} Server Error")
                    return@launch
                }

                if (body.status != "200") {
                    _pjcEventState.value = PjcEventState.Error(body.message)
                    return@launch
                }

                val parsed = parseFullPjcEvents(body.result)

                if (parsed.pjcEvents.isEmpty() && parsed.orderFollowUps.isEmpty() && parsed.paymentFollowUps.isEmpty() && parsed.newDealerAppointmentFollowUps.isEmpty() && parsed.subDealerVisitFollowUps.isEmpty() && parsed.newDealerSurvey.isEmpty()) {
                    _pjcEventState.value = PjcEventState.Error("No events found")
                } else {
                    // currently UI consumes only PJC events
                    _pjcEventState.value = PjcEventState.Success(parsed)
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _pjcEventState.value = PjcEventState.Error("Something went wrong")
            }
        }
    }

    // ----------------------------------------------------
    // FollowUps (MULTI RESPONSE SAFE)
    // ----------------------------------------------------

    private val _pjcEventStateFollowUp = MutableLiveData<PjcEventState>()
    val pjcEventStateFollowUp: LiveData<PjcEventState> = _pjcEventStateFollowUp
    fun getFollowUps(mobileNo: String) {
        _pjcEventStateFollowUp.value = PjcEventState.Loading

        viewModelScope.launch {
            try {
                val response = watermanPjcCalenderRepository.getFollowUps(GetPjcEventRequest(mobileNo=mobileNo))
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _pjcEventStateFollowUp.value = PjcEventState.Error("${response.code()} Server Error")
                    return@launch
                }

                if (body.status != "200") {
                    _pjcEventStateFollowUp.value = PjcEventState.Error(body.message)
                    return@launch
                }

                val parsed = parseFullPjcEvents(body.result)

                if (parsed.pjcEvents.isEmpty() && parsed.orderFollowUps.isEmpty() && parsed.paymentFollowUps.isEmpty() && parsed.newDealerAppointmentFollowUps.isEmpty() && parsed.subDealerVisitFollowUps.isEmpty() && parsed.newDealerSurvey.isEmpty()) {
                    _pjcEventStateFollowUp.value = PjcEventState.Error("No events found")
                } else {
                    // currently UI consumes only PJC events
                    _pjcEventStateFollowUp.value = PjcEventState.Success(parsed)
                }

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _pjcEventStateFollowUp.value = PjcEventState.Error("Something went wrong")
            }
        }
    }

    // ----------------------------------------------------
    // PlanForList
    // ----------------------------------------------------

    fun loadPlanForList() {
        if (cachedList != null) return // ✅ prevent duplicate API calls

        _planForListState.value = PlanForListState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getPlanForList()
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _planForListState.value =
                        PlanForListState.Error("Server error")
                    return@launch
                }

                if (body.status != "200") {
                    _planForListState.value =
                        PlanForListState.Error(body.message ?: "Error")
                    return@launch
                }

                cachedList = body.result
                _planForListState.value = PlanForListState.Success(body.result)

            } catch (e: Exception) {
                _planForListState.value = PlanForListState.Error("Something went wrong")
            }
        }
    }

    fun getCachedList(): List<PlanForList>? = cachedList

    // ----------------------------------------------------
    // Reason
    // ----------------------------------------------------

    fun loadReasonList(request: ReasonRequest) {

        if (cachedReasonList != null) {
            _reasonState.value = ReasonState.Success(cachedReasonList!!)
            return
        }

        _reasonState.value = ReasonState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getReasonApi(request)
                val body = response.body()


                if (!response.isSuccessful || body == null) {
                    _reasonState.value =
                        ReasonState.Error("Server error : ${response.code()}")
                    return@launch
                }


                if (body.status != "200") {
                    _reasonState.value =
                        ReasonState.Error(body.message ?: "Something went wrong")
                    return@launch
                }

                val list = body.result


                if (list.isEmpty()) {
                    _reasonState.value =
                        ReasonState.Empty("No reason found")
                    return@launch
                }


                cachedReasonList = list
                _reasonState.value = ReasonState.Success(list)

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _reasonState.value =
                    ReasonState.Error("Something went wrong")
            }
        }
    }

    fun getCachedReasonList(): List<ReasonList>? = cachedReasonList
    // ----------------------------------------------------
    // Reason List Params Pjc
    // ----------------------------------------------------
    fun loadReasonListParams(request: ReasonListParamsRequest) {

        // ✅ Prevent duplicate API calls
        if (cachedReasonListParams != null) {
            _reasonListParamsState.value =
                ReasonListParamsState.Success(cachedReasonListParams!!)
            return
        }

        _reasonListParamsState.value = ReasonListParamsState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getReasonListParam(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _reasonListParamsState.value = ReasonListParamsState.Error("Server error : ${response.code()}")
                    return@launch
                }

                if (body.status != "200") {
                    _reasonListParamsState.value = ReasonListParamsState.Error(body.message ?: "Something went wrong")
                    return@launch
                }

                val list = body.result

                if (list.isEmpty()) {
                    _reasonListParamsState.value =
                        ReasonListParamsState.Empty("No parameters found")
                    return@launch
                }

                cachedReasonListParams = list
                _reasonListParamsState.value =
                    ReasonListParamsState.Success(list)

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _reasonListParamsState.value =
                    ReasonListParamsState.Error("Something went wrong")
            }
        }
    }

    fun getCachedReasonListParamsList(): List<ReasonListParamsList>? = cachedReasonListParams
    fun clearReasonListParamsCache() {
        cachedReasonListParams = null
    }

    // ----------------------------------------------------
    // Drop Down List Params Pjc
    // ----------------------------------------------------
    fun loadDropdownParams(request: GetSqlQueryForDropdownParamRequest) {

        // ✅ Prevent duplicate API call
        if (cachedDropdownList != null) {
            _dropdownState.value = DropdownState.Success(cachedDropdownList!!)
            return
        }

        _dropdownState.value = DropdownState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getSqlQueryForDropdownParam(request)
                val body = response.body()

                // ❌ HTTP error
                if (!response.isSuccessful || body == null) {
                    _dropdownState.value = DropdownState.Error("Server error : ${response.code()}")
                    return@launch
                }

                // ❌ API-level error
                if (body.status != "200") {
                    _dropdownState.value = DropdownState.Error(body.message ?: "Something went wrong")
                    return@launch
                }

                val list = body.result

                // ❌ Empty data
                if (list.isNullOrEmpty()) {
                    _dropdownState.value = DropdownState.Empty("No data found")
                    return@launch
                }

                // ✅ Success
                cachedDropdownList = list
                _dropdownState.value = DropdownState.Success(list)

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _dropdownState.value =
                    DropdownState.Error("Something went wrong")
            }
        }
    }

    fun getDropdownCacheList(): List<LoadDropDownList>? = cachedDropdownList

    fun clearDropdownCacheList() {
        cachedDropdownList = null
    }


    // ----------------------------------------------------
    // District Pjc
    // ----------------------------------------------------

    fun loadDistrictPjc(request: GetDistrictPjcRequest) {

        // ✅ Prevent duplicate API calls
        if (cachedDistrictList != null) {
            _districtPjcState.value = DistrictPjcState.Success(cachedDistrictList!!)
            return
        }

        _districtPjcState.value = DistrictPjcState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getDistrictPjcApi(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _districtPjcState.value = DistrictPjcState.Error("Server error : ${response.code()}")
                    return@launch
                }

                if (body.status != "200") {
                    _districtPjcState.value = DistrictPjcState.Error(body.message ?: "Something went wrong")
                    return@launch
                }

                val list = body.result

                if (list.isEmpty()) {
                    _districtPjcState.value = DistrictPjcState.Empty("No district found")
                    return@launch
                }

                cachedDistrictList = list
                _districtPjcState.value = DistrictPjcState.Success(list)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _districtPjcState.value =
                    DistrictPjcState.Error("Something went wrong")
            }
        }
    }

    fun getCachedDistrictList(): List<GetDistrictPjcList>? = cachedDistrictList

    // ----------------------------------------------------
    // Insert Pjc Event
    // ----------------------------------------------------

    fun insertPjcEvent(request: InsertPjcEventRequest) {

        _insertPjcState.value = InsertPjcState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.insertPJCEntry(request)
                val body = response.body()

                // ❌ HTTP / null response
                if (!response.isSuccessful || body == null) {
                    _insertPjcState.postValue(InsertPjcState.Error("Server error : ${response.code()}"))
                    return@launch
                }

                // ❌ API-level failure
                if (body.status != "200") {
                    _insertPjcState.postValue(InsertPjcState.Error(body.message ?: "Something went wrong"))
                    return@launch
                }

                // ❌ Invalid success response
                val result = body.result
                if (result == null || result.id.isNullOrEmpty()) {
                    _insertPjcState.postValue(InsertPjcState.Error("Invalid response from server"))
                    return@launch
                }

                // ✅ SUCCESS
                _insertPjcState.postValue(InsertPjcState.Success(result))

            } catch (e: IOException) {
                _insertPjcState.postValue(InsertPjcState.Error("No internet connection"))

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertPjcState.postValue(InsertPjcState.Error("Something went wrong"))
            }
        }
    }

    // -----------------------------
    // Mappers
    // -----------------------------
    fun PjcResponse.toEventsList(): List<EventsCalModel> {
        if (status != "200") return emptyList()
        if (result == null || !result.isJsonArray) return emptyList()

        return result.asJsonArray.mapNotNull { json ->
            try {
                val item = Gson().fromJson(json, PJCItem::class.java)
                EventsCalModel(
                    pjcId = item.pjcId.orEmpty(),
                    date = item.dt.orEmpty(),
                    notes = item.notes.orEmpty(),
                    place = item.place.orEmpty(),
                    pjcLnId = item.pjcLnId.orEmpty(),
                    isDrop = item.isDrop.orEmpty(),
                    dropReason = item.dropReason.orEmpty()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun HolidayWeekOffResponse.toEventsList(): List<HolidayWeekOffDto> {
        if (status != "200") return emptyList()
        if (result == null || !result.isJsonArray) return emptyList()

        return result.asJsonArray.mapNotNull { json ->
            try {
                val item = Gson().fromJson(json, HolidayWeekOffDto::class.java)
                HolidayWeekOffDto(
                    calendarDate = item.calendarDate.orEmpty(),
                    remarks = item.remarks.orEmpty(),
                    type = item.type.orEmpty())
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun List<HolidayWeekOffDto>.toModels(): List<HolidayWeekOffModel> {
        return map {
            HolidayWeekOffModel(
                calendarDate = getConvertedDatePJC(it.calendarDate).toInt(),
                calendarMonth = getConvertedMonth(it.calendarDate).toInt(),
                calendarYear = getConvertedYear(it.calendarDate).toInt(),
                remarks = it.remarks.orEmpty(),
                type = it.type.orEmpty()
            )
        }
    }

    fun getConvertedDatePJC(date: String?): String {
        if (date.isNullOrEmpty()) return ""

        return try {
            val input =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
            val output =
                SimpleDateFormat("dd", Locale.ENGLISH)

            val parsed = input.parse(date)
            output.format(parsed!!)
        } catch (e: Exception) {
            ""
        }
    }

    fun getConvertedMonth(date: String?): String {
        if (date.isNullOrEmpty()) return ""

        return try {
            val input =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
            val output =
                SimpleDateFormat("MM", Locale.ENGLISH)

            val parsed = input.parse(date)
            output.format(parsed!!)
        } catch (e: Exception) {
            ""
        }
    }

    fun getConvertedYear(date: String?): String {
        if (date.isNullOrEmpty()) return ""

        return try {
            val input =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
            val output =
                SimpleDateFormat("yyyy", Locale.ENGLISH)

            val parsed = input.parse(date)
            output.format(parsed!!)
        } catch (e: Exception) {
            ""
        }
    }


    // ----------------------------------------------------
    // MULTI RESPONSE PARSER (NO CRASH GUARANTEED)
    // ----------------------------------------------------

    private fun parseFullPjcEvents(result: JsonElement?): PjcEventFullData {
        Log.e("Result event",result.toString())
        val pjcEvents = mutableListOf<PjcEventDto>()
        val paymentFollowUps = mutableListOf<PaymentFollowUpDto>()
        val orderFollowUps = mutableListOf<PartyRemarkDto>()
        val newDealerAppointmentFollowUps = mutableListOf<PartyRemarkDto>()
        val subDealerVisitFollowUps = mutableListOf<PartyRemarkDto>()
        val newDealerSurvey = mutableListOf<PartyRemarkDto>()

        if (result == null || !result.isJsonArray) {
            return PjcEventFullData(
                pjcEvents,
                paymentFollowUps,
                orderFollowUps,
                newDealerAppointmentFollowUps,
                subDealerVisitFollowUps,
                newDealerSurvey
            )
        }

        val arr = result.asJsonArray

        fun parseArray(index: Int, block: (JsonElement) -> Unit) {
            if (arr.size() > index && arr[index].isJsonArray) {
                arr[index].asJsonArray.forEach {
                    try {
                        block(it)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        }

        parseArray(0) { pjcEvents.add(gson.fromJson(it, PjcEventDto::class.java)) }
        parseArray(1) { paymentFollowUps.add(gson.fromJson(it, PaymentFollowUpDto::class.java)) }
        parseArray(2) { orderFollowUps.add(gson.fromJson(it, PartyRemarkDto::class.java)) }
        parseArray(3) { newDealerAppointmentFollowUps.add(gson.fromJson(it, PartyRemarkDto::class.java)) }
        parseArray(4) { subDealerVisitFollowUps.add(gson.fromJson(it, PartyRemarkDto::class.java)) }
        parseArray(5) { newDealerSurvey.add(gson.fromJson(it, PartyRemarkDto::class.java)) }

        if (
            pjcEvents.isEmpty() &&
            paymentFollowUps.isEmpty() &&
            orderFollowUps.isEmpty() &&
            newDealerAppointmentFollowUps.isEmpty() &&
            subDealerVisitFollowUps.isEmpty() &&
            newDealerSurvey.isEmpty() &&
            arr[0].isJsonObject   // ✅ important check
        ) {
            // Check if it's flat array (new API)
            arr.forEach {
                try {
                    if (it.isJsonObject) {
                        pjcEvents.add(gson.fromJson(it, PjcEventDto::class.java))
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        return PjcEventFullData(
            pjcEvents,
            paymentFollowUps,
            orderFollowUps,
            newDealerAppointmentFollowUps,
            subDealerVisitFollowUps,
            newDealerSurvey
        )
    }

    /*====================================================================*/
    private val _serverTimeState = MutableLiveData<GetServerTimeState>()
    val serverTimeState: LiveData<GetServerTimeState> = _serverTimeState

    fun getServerTime() {

        _serverTimeState.value = GetServerTimeState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = repository.getServerTime() // suspend fun in repo
                val body = response.body()

                // ❌ HTTP / null response
                if (!response.isSuccessful || body == null) {
                    _serverTimeState.postValue(GetServerTimeState.Error("Server error : ${response.code()}"))
                    return@launch
                }

                // ❌ API-level failure
                if (body.status != "200") {
                    _serverTimeState.postValue(GetServerTimeState.Error(body.message ?: "Something went wrong"))
                    return@launch
                }

                // ❌ Invalid result
                val resultList = body.result
                if (resultList.isNullOrEmpty() || resultList[0].serverTime.isNullOrEmpty()) {
                    _serverTimeState.postValue(GetServerTimeState.Error("Invalid response from server"))
                    return@launch
                }

                // ✅ SUCCESS
                _serverTimeState.postValue(GetServerTimeState.Success(resultList[0]))

            } catch (e: IOException) {
                _serverTimeState.postValue(GetServerTimeState.Error("No internet connection"))

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _serverTimeState.postValue(GetServerTimeState.Error("Something went wrong"))
            }
        }
    }


}