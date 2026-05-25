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
import com.i.common.attendance.network.response.DropdownItem
import com.i.common.attendance.ui.home.tourvoucher.adapter.SelectWorkTypeAdapter

class SelectWorkTypeBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectEmployeeBinding
    private lateinit var selectWorkTypeAdapter: SelectWorkTypeAdapter

    private var planForList: ArrayList<DropdownItem> = arrayListOf()
    private var fullList: List<DropdownItem> = emptyList()

    companion object {
        private const val ARG_LIST = "work_type_list"

        fun newInstance(list: List<DropdownItem>): SelectWorkTypeBottomSheetFragment {
            return SelectWorkTypeBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((DropdownItem) -> Unit)? = null

    fun setDismissCallback(callback: (DropdownItem) -> Unit) {
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

        selectWorkTypeAdapter = SelectWorkTypeAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectWorkTypeAdapter
        }

        fullList = planForList.toList()
        // ✅ Set data AFTER adapter initialization
        selectWorkTypeAdapter.submitList(planForList)
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
            selectWorkTypeAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.text?.contains(query, ignoreCase = true) == true
        }

        selectWorkTypeAdapter.submitList(filteredList)
    }

}