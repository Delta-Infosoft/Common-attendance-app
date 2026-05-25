package com.i.common.attendance.ui.home.orderbook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.fragment.app.viewModels
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentAddOrderBookDetailsBinding
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.ProductListRequest
import com.i.common.attendance.network.response.CustomerModel
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.orderbook.viewmodel.GetCustomerUiState
import com.i.common.attendance.ui.home.orderbook.viewmodel.GetProductUiState
import com.i.common.attendance.ui.home.orderbook.viewmodel.GetRateUiState
import com.i.common.attendance.ui.home.orderbook.viewmodel.InsertOrderEntryUiState
import com.i.common.attendance.ui.home.orderbook.viewmodel.OrderBookViewModel
import com.i.common.attendance.utils.EncryptedPrefHelper
import javax.inject.Inject
import kotlin.getValue

class AddOrderBookFragment : BaseFragment() {
    private lateinit var binding : FragmentAddOrderBookDetailsBinding
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val orderBookViewModel: OrderBookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddOrderBookDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initApiCall()
        manageToolBar()
        moveOnClickListeners()
        observeCustomerList()
        observeProductList()
        observeRate()
        observeInsertOrderEntry()
    }

    private fun initApiCall() = with(binding){
        val user = sharedPref.getUser()
        orderBookViewModel.loadCustomerList(request = GetStateRequest(empId = user?.EmpID?:""))
        //orderBookViewModel.refreshProductList(request = ProductListRequest(productGrpId = ))
    }
    private fun moveOnClickListeners() = with(binding){

    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_order_book))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeCustomerList() {
        orderBookViewModel.getCustomerUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetCustomerUiState.Idle -> { }
                is GetCustomerUiState.Loading -> {
                    showLoader()
                }
                is GetCustomerUiState.Success -> {
                    hideLoader()
                }
                is GetCustomerUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetCustomerUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeProductList() {
        orderBookViewModel.getProductUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetProductUiState.Idle -> { }
                is GetProductUiState.Loading -> {
                    showLoader()
                }
                is GetProductUiState.Success -> {
                    hideLoader()
                }
                is GetProductUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetProductUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeInsertOrderEntry() {
        orderBookViewModel.insertOrderEntryUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertOrderEntryUiState.Idle -> { }
                is InsertOrderEntryUiState.Loading -> {
                    showLoader()
                }
                is InsertOrderEntryUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertOrderEntryUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertOrderEntryUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeRate() {
        orderBookViewModel.getRateUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetRateUiState.Idle -> { }
                is GetRateUiState.Loading -> {
                    showLoader()
                }
                is GetRateUiState.Success -> {
                    hideLoader()
                }
                is GetRateUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetRateUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun showLoader() {
        requireActivity().window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBar.visibility = View.GONE
    }

}