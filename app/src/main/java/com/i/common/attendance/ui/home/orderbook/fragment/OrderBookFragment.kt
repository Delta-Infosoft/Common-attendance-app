package com.i.common.attendance.ui.home.orderbook.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentOrderBookFlotechBinding
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.newcustomerdealer.viewmodel.PortfolioViewModel
import com.i.common.attendance.ui.home.orderbook.adapter.OrderListAdapter
import com.i.common.attendance.ui.home.orderbook.viewmodel.GetOrderListUiState
import com.i.common.attendance.ui.home.orderbook.viewmodel.OrderBookViewModel
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class OrderBookFragment : BaseFragment() {
    private lateinit var binding : FragmentOrderBookFlotechBinding
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val orderBookViewModel: OrderBookViewModel by viewModels()
    private val orderListAdapter by lazy {
        OrderListAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBookFlotechBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = sharedPref.getUser()
        orderBookViewModel.refreshOrderList(request = GetStateRequest(empId = user?.EmpID?:""))
        manageToolBar()
        moveOnclickListeners()
        setRecyclerView()
        observeOrderList()
    }
    private fun moveOnclickListeners() = with(binding){
        imgViewAdd.setSafeOnClickListener {
            loadFragment(fragment = AddOrderBookFragment(), isAdd = false, isAddBackStack = true)
        }
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
    private fun setRecyclerView() = with(binding){
        recyclerViewOrderBook.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = orderListAdapter
        }
    }
    private fun observeOrderList() {
         orderBookViewModel.getOrderListUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetOrderListUiState.Idle -> { }
                is GetOrderListUiState.Loading -> {
                    showLoader()
                }
                is GetOrderListUiState.Success -> {
                    hideLoader()
                    orderListAdapter.submitList(state.list)
                }
                is GetOrderListUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetOrderListUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun showLoader() {
        requireActivity().window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarOrderBook.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarOrderBook.visibility = View.GONE
    }

}