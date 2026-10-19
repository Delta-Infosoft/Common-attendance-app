package com.i.common.attendance.ui.home.dealerwisereport.fragment

import android.app.Dialog
import android.os.Build
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
import com.i.common.attendance.network.response.DealerDetailsAccount
import com.i.common.attendance.ui.home.dealerwisereport.adapter.SelectCustomerNameCategoryAdapter
import com.i.common.attendance.utils.Constants

class SelectDealerNameBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFragmentSelectDailyTourDealerCategoryBinding
    private lateinit var selectDailyTourDealerCategoryAdapter: SelectCustomerNameCategoryAdapter
    private var fullList: List<DealerDetailsAccount> = emptyList()
    private var dealerCategoryList: ArrayList<DealerDetailsAccount> = arrayListOf()

    companion object {
        private const val ARG_LIST = "district_for_list"

        fun newInstance(list: List<DealerDetailsAccount>): SelectDealerNameBottomSheetFragment {
            return SelectDealerNameBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((DealerDetailsAccount) -> Unit)? = null

    fun setDismissCallback(callback: (DealerDetailsAccount) -> Unit) {
        dismissCallback = callback
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
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
        dealerCategoryList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(ARG_LIST, DealerDetailsAccount::class.java) ?: arrayListOf()
        } else {
            arguments?.classLoader = DealerDetailsAccount::class.java.classLoader
            @Suppress("DEPRECATION")
            arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
        }
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
        selectDailyTourDealerCategoryAdapter = SelectCustomerNameCategoryAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            Constants.hideKeyboard(binding.root)
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
            it.DealerName?.contains(query, ignoreCase = true) == true ||
            it.DealerCode?.contains(query, ignoreCase = true) == true
        }
        selectDailyTourDealerCategoryAdapter.submitList(filteredList)
    }
}