package com.i.common.attendance.ui.home.attendancereport.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectMonthBinding
import com.i.common.attendance.network.response.MonthList
import com.i.common.attendance.ui.home.attendancereport.adapter.SelectMonthAdapter

class SelectMonthBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectMonthBinding
    private lateinit var selectMonthAdapter: SelectMonthAdapter

    private var fullList: List<MonthList> = emptyList()
    private var districtList: ArrayList<MonthList> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<MonthList>): SelectMonthBottomSheetFragment {
            return SelectMonthBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((MonthList) -> Unit)? = null

    fun setDismissCallback(callback: (MonthList) -> Unit) {
        dismissCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SAFE: Only read arguments here
        districtList = arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectMonthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {

        selectMonthAdapter = SelectMonthAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectMonthAdapter
        }

        fullList = districtList.toList()
        // ✅ Set data AFTER adapter initialization
        selectMonthAdapter.submitList(districtList)
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
            selectMonthAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.Month?.contains(query, ignoreCase = true) == true
        }

        selectMonthAdapter.submitList(filteredList)
    }
}