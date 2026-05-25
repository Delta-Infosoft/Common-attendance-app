package com.i.common.attendance.ui.home.dailytour.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectDailyTourDealerNameBinding
import com.i.common.attendance.network.response.DailyTourDealerName
import com.i.common.attendance.ui.home.dailytour.adapter.SelectDailyTourDealerNameAdapter

class SelectDailyTourDealerNameBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFragmentSelectDailyTourDealerNameBinding
    private lateinit var selectDailyTourDealerNameAdapter: SelectDailyTourDealerNameAdapter
    private var fullList: List<DailyTourDealerName> = emptyList()
    private var dealerCategoryList: ArrayList<DailyTourDealerName> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<DailyTourDealerName>): SelectDailyTourDealerNameBottomSheetFragment {
            return SelectDailyTourDealerNameBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((DailyTourDealerName) -> Unit)? = null

    fun setDismissCallback(callback: (DailyTourDealerName) -> Unit) {
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
        binding = BottomSheetFragmentSelectDailyTourDealerNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {
        selectDailyTourDealerNameAdapter = SelectDailyTourDealerNameAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectDailyTourDealerNameAdapter
        }

        fullList = dealerCategoryList.toList()
        selectDailyTourDealerNameAdapter.submitList(dealerCategoryList)
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
            selectDailyTourDealerNameAdapter.submitList(fullList)
            return
        }
        val filteredList = fullList.filter {
            it.Name?.contains(query, ignoreCase = true) == true
        }
        selectDailyTourDealerNameAdapter.submitList(filteredList)
    }
}