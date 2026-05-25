package com.i.common.attendance.ui.home.ledgerreport.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectDailyTourDealerCategoryBinding
import com.i.common.attendance.network.response.CustomerData
import com.i.common.attendance.ui.home.ledgerreport.adapter.SelectCustomerNameCategoryAdapter
import com.i.common.attendance.utils.Constants

class SelectCustomerNameBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFragmentSelectDailyTourDealerCategoryBinding
    private lateinit var selectDailyTourDealerCategoryAdapter: SelectCustomerNameCategoryAdapter
    private var fullList: List<CustomerData> = emptyList()
    private var dealerCategoryList: ArrayList<CustomerData> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<CustomerData>): SelectCustomerNameBottomSheetFragment {
            return SelectCustomerNameBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((CustomerData) -> Unit)? = null

    fun setDismissCallback(callback: (CustomerData) -> Unit) {
        dismissCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dealerCategoryList = arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectDailyTourDealerCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {
        selectDailyTourDealerCategoryAdapter = SelectCustomerNameCategoryAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            Constants.hideKeyboard(binding.root)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectDailyTourDealerCategoryAdapter
        }

        fullList = dealerCategoryList.toList()
        selectDailyTourDealerCategoryAdapter.submitList(dealerCategoryList)
    }

    private fun manageSearchView(){
        binding.searchView.queryHint = "Search"
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query.isNullOrBlank()) {
            selectDailyTourDealerCategoryAdapter.submitList(fullList)
            return
        }
        val filteredList = fullList.filter {
            it.Name?.contains(query, ignoreCase = true) == true
        }
        selectDailyTourDealerCategoryAdapter.submitList(filteredList)
    }
}