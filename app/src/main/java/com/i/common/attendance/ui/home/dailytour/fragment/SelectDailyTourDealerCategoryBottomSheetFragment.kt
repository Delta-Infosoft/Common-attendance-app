package com.i.common.attendance.ui.home.dailytour.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectDailyTourDealerCategoryBinding
import com.i.common.attendance.network.response.DailyTourDealerCategory
import com.i.common.attendance.ui.home.dailytour.adapter.SelectDailyTourDealerCategoryAdapter

class SelectDailyTourDealerCategoryBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFragmentSelectDailyTourDealerCategoryBinding
    private lateinit var selectDailyTourDealerCategoryAdapter: SelectDailyTourDealerCategoryAdapter
    private var fullList: List<DailyTourDealerCategory> = emptyList()
    private var dealerCategoryList: ArrayList<DailyTourDealerCategory> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<DailyTourDealerCategory>): SelectDailyTourDealerCategoryBottomSheetFragment {
            return SelectDailyTourDealerCategoryBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((DailyTourDealerCategory) -> Unit)? = null

    fun setDismissCallback(callback: (DailyTourDealerCategory) -> Unit) {
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
        selectDailyTourDealerCategoryAdapter = SelectDailyTourDealerCategoryAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
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
            it.Text?.contains(query, ignoreCase = true) == true
        }
        selectDailyTourDealerCategoryAdapter.submitList(filteredList)
    }
}