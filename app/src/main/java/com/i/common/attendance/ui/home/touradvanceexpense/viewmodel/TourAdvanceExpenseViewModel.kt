package com.i.common.attendance.ui.home.touradvanceexpense.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.i.common.attendance.network.request.AddTourAdvanceExpenseRequest
import com.i.common.attendance.network.request.TourAdvanceExpenseListRequest
import com.i.common.attendance.network.response.TourAdvanceExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TourAdvanceExpenseViewModel @Inject constructor(
    private val repository: TourAdvanceExpenseRepository
) : ViewModel() {

    /*=========================== INSERT ===========================*/
    private val _insertState =
        MutableLiveData<AdvanceExpenseState>(AdvanceExpenseState.Idle)
    val insertState: LiveData<AdvanceExpenseState> = _insertState

    fun insertAdvanceExpense(request: AddTourAdvanceExpenseRequest) {
        _insertState.value = AdvanceExpenseState.Loading

        viewModelScope.launch {
            try {
                val response = repository.insertTourAdvanceExpense(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _insertState.value = AdvanceExpenseState.Error("Server error: ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertState.value = AdvanceExpenseState.Success(
                            message = body.message ?: "Success",
                            id = body.result?.id
                        )
                    }

                    "209" -> {
                        _insertState.value = AdvanceExpenseState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _insertState.value = AdvanceExpenseState.Error(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertState.value = AdvanceExpenseState.Error("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertState.value = AdvanceExpenseState.Error("Something went wrong")
            }
        }
    }

    /*=========================== UPDATE ===========================*/
    private val _updateState = MutableLiveData<AdvanceExpenseState>(AdvanceExpenseState.Idle)
    val updateState: LiveData<AdvanceExpenseState> = _updateState

    fun updateAdvanceExpense(request: AddTourAdvanceExpenseRequest) {
        _updateState.value = AdvanceExpenseState.Loading

        viewModelScope.launch {
            try {
                val response = repository.updateTourAdvanceExpense(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _updateState.value = AdvanceExpenseState.Error("Server error: ${response.code()}")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertState.value = AdvanceExpenseState.Success(
                            message = body.message ?: "Success",
                            id = body.result?.id
                        )
                    }

                    "209" -> {
                        _insertState.value = AdvanceExpenseState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _insertState.value = AdvanceExpenseState.Error(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _updateState.value = AdvanceExpenseState.Error("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _updateState.value = AdvanceExpenseState.Error("Something went wrong")
            }
        }
    }

    /*=========================== LIST ===========================*/
    private val _listState = MutableLiveData<AdvanceExpenseListState>(AdvanceExpenseListState.Idle)
    val listState: LiveData<AdvanceExpenseListState> = _listState

    fun getAdvanceExpenseList(request: TourAdvanceExpenseListRequest) {
        _listState.value = AdvanceExpenseListState.Loading

        viewModelScope.launch {
            try {
                val response = repository.tourAdvanceList(request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _listState.value = AdvanceExpenseListState.Error("Server error: ${response.code()}")
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
                                            TourAdvanceExpense::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _listState.value =
                                AdvanceExpenseListState.Empty(body.message ?: "No record found")
                        } else {
                            _listState.value =
                                AdvanceExpenseListState.Success(list)
                        }
                    }

                    "209" -> {
                        // 🔥 Important: Ignore result completely
                        _listState.value =
                            AdvanceExpenseListState.Empty(body.message ?: "No record found")
                    }

                    else -> {
                        _listState.value =
                            AdvanceExpenseListState.Error(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _listState.value = AdvanceExpenseListState.Error("Please check your internet connection")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _listState.value = AdvanceExpenseListState.Error("Something went wrong")
            }
        }
    }
}