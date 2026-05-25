package com.i.common.attendance.ui.home.tourvoucher.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.R
import com.i.common.attendance.databinding.BottomSheetFragmentSelectEmployeeBinding
import com.i.common.attendance.network.response.NameDropdownItem
import com.i.common.attendance.ui.home.tourvoucher.adapter.SelectDistrictAdapter

class SelectDistrictBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectEmployeeBinding
    private lateinit var selectDistrictAdapter: SelectDistrictAdapter

    private var planForList: ArrayList<NameDropdownItem> = arrayListOf()
    private var fullList: List<NameDropdownItem> = emptyList()

    companion object {
        private const val ARG_LIST = "branch_list"

        fun newInstance(list: List<NameDropdownItem>): SelectDistrictBottomSheetFragment {
            return SelectDistrictBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((NameDropdownItem) -> Unit)? = null

    fun setDismissCallback(callback: (NameDropdownItem) -> Unit) {
        dismissCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SAFE: Only read arguments here
        planForList = arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectEmployeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {
        txtViewSelect.text = getString(R.string.toolbar_title_select)
        selectDistrictAdapter = SelectDistrictAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectDistrictAdapter
        }

        fullList = planForList.toList()

        // ✅ Set data AFTER adapter initialization
        selectDistrictAdapter.submitList(planForList)
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
            it.name?.contains(query, ignoreCase = true) == true
        }

        selectDistrictAdapter.submitList(filteredList)
    }
}