package com.i.common.attendance.ui.home.dailytour.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true // or false, depending on your sheet's bg color
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                // Give the sheet container a concrete height to measure against
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.requestLayout()
            }
        }

        return dialog
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