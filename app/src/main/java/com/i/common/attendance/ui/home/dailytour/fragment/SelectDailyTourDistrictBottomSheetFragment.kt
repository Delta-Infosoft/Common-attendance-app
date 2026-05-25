package com.i.common.attendance.ui.home.dailytour.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectDailyTourDistrictBinding
import com.i.common.attendance.network.response.DailyTourDistrict
import com.i.common.attendance.ui.home.dailytour.adapter.SelectDailyTourDistrictAdapter

class SelectDailyTourDistrictBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFragmentSelectDailyTourDistrictBinding
    private lateinit var selectDailyTourDistrictAdapter: SelectDailyTourDistrictAdapter
    private var fullList: List<DailyTourDistrict> = emptyList()
    private var dealerCategoryList: ArrayList<DailyTourDistrict> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<DailyTourDistrict>): SelectDailyTourDistrictBottomSheetFragment {
            return SelectDailyTourDistrictBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((DailyTourDistrict) -> Unit)? = null

    fun setDismissCallback(callback: (DailyTourDistrict) -> Unit) {
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
        binding = BottomSheetFragmentSelectDailyTourDistrictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {
        selectDailyTourDistrictAdapter = SelectDailyTourDistrictAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectDailyTourDistrictAdapter
        }

        fullList = dealerCategoryList.toList()
        selectDailyTourDistrictAdapter.submitList(dealerCategoryList)
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
            selectDailyTourDistrictAdapter.submitList(fullList)
            return
        }
        val filteredList = fullList.filter {
            it.District?.contains(query, ignoreCase = true) == true
        }
        selectDailyTourDistrictAdapter.submitList(filteredList)
    }
}