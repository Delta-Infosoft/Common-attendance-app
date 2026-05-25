package com.i.common.attendance.ui.home.pjc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectDistrictBinding
import com.i.common.attendance.network.response.GetDistrictPjcList
import com.i.common.attendance.ui.home.pjc.adapter.SelectDistrictAdapter

class SelectDistrictBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectDistrictBinding
    private lateinit var selectDistrictAdapter: SelectDistrictAdapter

    private var fullList: List<GetDistrictPjcList> = emptyList()
    private var districtList: ArrayList<GetDistrictPjcList> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<GetDistrictPjcList>): SelectDistrictBottomSheetFragment {
            return SelectDistrictBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((GetDistrictPjcList) -> Unit)? = null

    fun setDismissCallback(callback: (GetDistrictPjcList) -> Unit) {
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
        binding = BottomSheetFragmentSelectDistrictBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {

        selectDistrictAdapter = SelectDistrictAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectDistrictAdapter
        }

        fullList = districtList.toList()
        // ✅ Set data AFTER adapter initialization
        selectDistrictAdapter.submitList(districtList)
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
            selectDistrictAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.Name?.contains(query, ignoreCase = true) == true
        }

        selectDistrictAdapter.submitList(filteredList)
    }
}