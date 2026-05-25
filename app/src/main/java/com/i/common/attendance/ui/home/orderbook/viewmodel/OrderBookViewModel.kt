package com.i.common.attendance.ui.home.orderbook.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.InsertOrderRequest
import com.i.common.attendance.network.request.ProductListRequest
import com.i.common.attendance.network.request.RateRequest
import com.i.common.attendance.network.response.CustomerModel
import com.i.common.attendance.network.response.OrderItem
import com.i.common.attendance.network.response.ProductModel
import com.i.common.attendance.network.response.RateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OrderBookViewModel @Inject constructor(
    private val repository: OrderBookRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ===================== Order List =====================
    private val _getOrderListUiState = MutableLiveData<GetOrderListUiState>()
    val getOrderListUiState: LiveData<GetOrderListUiState> = _getOrderListUiState
    var cachedOrderList: List<OrderItem>? = null
    fun loadOrderList(request: GetStateRequest) {

        cachedOrderList?.let {
            _getOrderListUiState.value = GetOrderListUiState.Success(it)
            return
        }

        _getOrderListUiState.value = GetOrderListUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getOrderList(request)

                if (!response.isSuccessful) {
                    _getOrderListUiState.value = GetOrderListUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getOrderListUiState.value = GetOrderListUiState.ApiError("Empty server response")
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
                                            OrderItem::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getOrderListUiState.value = GetOrderListUiState.ApiError(body.message ?: "No orders found")
                        } else {
                            cachedOrderList = list
                            _getOrderListUiState.value = GetOrderListUiState.Success(list)
                        }
                    }
                    "209" -> {
                        _getOrderListUiState.value = GetOrderListUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _getOrderListUiState.value = GetOrderListUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getOrderListUiState.postValue(GetOrderListUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getOrderListUiState.postValue(GetOrderListUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshOrderList(request: GetStateRequest) {
        cachedOrderList = null
        loadOrderList(request)
    }

    /*==============================================================================================*/
    private val _getCustomerUiState = MutableLiveData<GetCustomerUiState>()
    val getCustomerUiState: LiveData<GetCustomerUiState> = _getCustomerUiState
    var cachedCustomerList: List<CustomerModel>? = null
    fun loadCustomerList(request: GetStateRequest) {
        cachedCustomerList?.let {
            _getCustomerUiState.value = GetCustomerUiState.Success(it)
            return
        }
        _getCustomerUiState.value = GetCustomerUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getCustomerViewerParam(request)

                if (!response.isSuccessful) {
                    _getCustomerUiState.value =
                        GetCustomerUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getCustomerUiState.value = GetCustomerUiState.ApiError("Empty server response")
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
                                            CustomerModel::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getCustomerUiState.value =
                                GetCustomerUiState.ApiError(body.message ?: "No customer found")
                        } else {
                            cachedCustomerList = list
                            _getCustomerUiState.value = GetCustomerUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getCustomerUiState.value =
                            GetCustomerUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getCustomerUiState.value =
                            GetCustomerUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getCustomerUiState.postValue(GetCustomerUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getCustomerUiState.postValue(GetCustomerUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshCustomerList(request: GetStateRequest) {
        cachedCustomerList = null
        loadCustomerList(request)
    }

    /*===============================================================================================*/
    private val _getProductUiState = MutableLiveData<GetProductUiState>()
    val getProductUiState: LiveData<GetProductUiState> = _getProductUiState
    var cachedProductList: List<ProductModel>? = null
    fun loadProductList(request: ProductListRequest) {
        cachedProductList?.let {
            _getProductUiState.value = GetProductUiState.Success(it)
            return
        }

        _getProductUiState.value = GetProductUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getProductParam(request)

                if (!response.isSuccessful) {
                    _getProductUiState.value =
                        GetProductUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getProductUiState.value = GetProductUiState.ApiError("Empty server response")
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
                                            ProductModel::class.java
                                        )
                                    }
                                }
                            }

                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getProductUiState.value =
                                GetProductUiState.ApiError(body.message ?: "No product found")
                        } else {
                            cachedProductList = list
                            _getProductUiState.value = GetProductUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getProductUiState.value =
                            GetProductUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getProductUiState.value =
                            GetProductUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getProductUiState.postValue(GetProductUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getProductUiState.postValue(GetProductUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshProductList(request: ProductListRequest) {
        cachedProductList = null
        loadProductList(request)
    }

    /*==========================================================================================*/
    private val _getRateUiState = MutableLiveData<GetRateUiState>()
    val getRateUiState: LiveData<GetRateUiState> = _getRateUiState
    var cachedRateList: List<RateModel>? = null
    fun loadRate(request: RateRequest) {
        cachedRateList?.let {
            _getRateUiState.value = GetRateUiState.Success(it)
            return
        }
        _getRateUiState.value = GetRateUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getRate(request)

                if (!response.isSuccessful) {
                    _getRateUiState.value = GetRateUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _getRateUiState.value = GetRateUiState.ApiError("Empty server response")
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
                                            RateModel::class.java
                                        )
                                    }
                                }
                            }
                            else -> emptyList()
                        }

                        if (list.isEmpty()) {
                            _getRateUiState.value = GetRateUiState.ApiError(body.message ?: "No rate found")
                        } else {
                            cachedRateList = list
                            _getRateUiState.value = GetRateUiState.Success(list)
                        }
                    }

                    "209" -> {
                        _getRateUiState.value = GetRateUiState.ApiError(body.message ?: "No Record Found")
                    }

                    else -> {
                        _getRateUiState.value = GetRateUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _getRateUiState.postValue(GetRateUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _getRateUiState.postValue(GetRateUiState.ApiError("Something went wrong"))
            }
        }
    }
    fun refreshRate(request: RateRequest) {
        cachedRateList = null
        loadRate(request)
    }

    /*=========================================================================================*/
    private val _insertOrderEntryUiState = MutableLiveData<InsertOrderEntryUiState>()
    val insertOrderEntryUiState: LiveData<InsertOrderEntryUiState> = _insertOrderEntryUiState
    fun insertOrderEntry(request: InsertOrderRequest) {
        _insertOrderEntryUiState.value = InsertOrderEntryUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.insertOrderEntry(request)

                if (!response.isSuccessful) {
                    _insertOrderEntryUiState.value = InsertOrderEntryUiState.ApiError("Server error: ${response.code()}")
                    return@launch
                }

                val body = response.body()
                if (body == null) {
                    _insertOrderEntryUiState.value = InsertOrderEntryUiState.ApiError("Empty server response")
                    return@launch
                }

                when (body.status) {
                    "200" -> {
                        _insertOrderEntryUiState.value = InsertOrderEntryUiState.Success(body.message ?: "Success")
                    }
                    "209" -> {
                        _insertOrderEntryUiState.value = InsertOrderEntryUiState.ApiError(body.message ?: "No Record Found")
                    }
                    else -> {
                        _insertOrderEntryUiState.value = InsertOrderEntryUiState.ApiError(body.message ?: "Something went wrong")
                    }
                }

            } catch (e: IOException) {
                _insertOrderEntryUiState.postValue(InsertOrderEntryUiState.NetworkError("Please check your internet connection"))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _insertOrderEntryUiState.postValue(InsertOrderEntryUiState.ApiError("Something went wrong"))
            }
        }
    }


}