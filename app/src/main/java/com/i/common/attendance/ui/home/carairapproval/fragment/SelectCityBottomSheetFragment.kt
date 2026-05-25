package com.i.common.attendance.ui.home.carairapproval.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.databinding.BottomSheetFragmentSelectTravelByBinding
import com.i.common.attendance.network.response.GetCities
import com.i.common.attendance.ui.home.carairapproval.adapter.SelectCityAdapter

class SelectCityBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectTravelByBinding
    private lateinit var selectTravelByAdapter: SelectCityAdapter

    private var fullList: List<GetCities> = emptyList()
    private var districtList: ArrayList<GetCities> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<GetCities>): SelectCityBottomSheetFragment {
            return SelectCityBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((GetCities) -> Unit)? = null

    fun setDismissCallback(callback: (GetCities) -> Unit) {
        dismissCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SAFE: Only read arguments here
        districtList =
            arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectTravelByBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {

        selectTravelByAdapter = SelectCityAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectTravelByAdapter
        }

        fullList = districtList.toList()
        // ✅ Set data AFTER adapter initialization
        selectTravelByAdapter.submitList(districtList)
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
            selectTravelByAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {

            if (BuildConfig.FLAVOR == "unnati") {
                it.city?.contains(query, ignoreCase = true) == true
            } else {
                it.name?.contains(query, ignoreCase = true) == true
            }
        }

        selectTravelByAdapter.submitList(filteredList)
    }
}