package com.i.common.attendance.ui.home.tourvoucher.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectEmployeeBinding
import com.i.common.attendance.network.response.EmployeeModel
import com.i.common.attendance.ui.home.tourvoucher.adapter.SelectEmployeeNameAdapter

class SelectEmployeeNameBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectEmployeeBinding
    private lateinit var selectBranchAdapter: SelectEmployeeNameAdapter
    private var fullList: List<EmployeeModel> = emptyList()

    private var planForList: ArrayList<EmployeeModel> = arrayListOf()

    companion object {
        private const val ARG_LIST = "employee_list"

        fun newInstance(list: List<EmployeeModel>): SelectEmployeeNameBottomSheetFragment {
            return SelectEmployeeNameBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((EmployeeModel) -> Unit)? = null

    fun setDismissCallback(callback: (EmployeeModel) -> Unit) {
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

        selectBranchAdapter = SelectEmployeeNameAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectBranchAdapter
        }

        fullList = planForList.toList()
        // ✅ Set data AFTER adapter initialization
        selectBranchAdapter.submitList(planForList)
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
            selectBranchAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.UsersName?.contains(query, ignoreCase = true) == true
        }

        selectBranchAdapter.submitList(filteredList)
    }

}