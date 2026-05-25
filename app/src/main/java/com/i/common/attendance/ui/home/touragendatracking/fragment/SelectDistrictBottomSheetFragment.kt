package com.i.common.attendance.ui.home.touragendatracking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.R
import com.i.common.attendance.databinding.BottomSheetFragmentSelectStateBinding
import com.i.common.attendance.network.response.DistrictTourAgendaTracking
import com.i.common.attendance.ui.home.touragendatracking.adapter.SelectDistrictAdapter

class SelectDistrictBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectStateBinding
    private lateinit var selectActivityPlanAdapter: SelectDistrictAdapter

    private var planForList: ArrayList<DistrictTourAgendaTracking> = arrayListOf()
    private var fullList: List<DistrictTourAgendaTracking> = emptyList()


    companion object {
        private const val ARG_LIST = "activity_plan_list"

        fun newInstance(list: List<DistrictTourAgendaTracking>): SelectDistrictBottomSheetFragment {
            return SelectDistrictBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((DistrictTourAgendaTracking) -> Unit)? = null

    fun setDismissCallback(callback: (DistrictTourAgendaTracking) -> Unit) {
        dismissCallback = callback
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            val height = (resources.displayMetrics.heightPixels * 0.7).toInt()
            it.layoutParams.height = height
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        planForList = arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectStateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {
        txtViewSelect.text = getString(R.string.toolbar_title_select)

        selectActivityPlanAdapter = SelectDistrictAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectActivityPlanAdapter
        }

        fullList = planForList.toList()

        // ✅ Set data AFTER adapter initialization
        selectActivityPlanAdapter.submitList(planForList)
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
            selectActivityPlanAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.District?.contains(query, ignoreCase = true) == true
        }

        selectActivityPlanAdapter.submitList(filteredList)
    }

}