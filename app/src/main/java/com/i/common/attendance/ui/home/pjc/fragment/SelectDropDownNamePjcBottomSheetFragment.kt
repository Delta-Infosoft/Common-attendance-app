package com.i.common.attendance.ui.home.pjc.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectDropDownListForPjcBinding
import com.i.common.attendance.network.response.LoadDropDownList
import com.i.common.attendance.ui.home.pjc.adapter.SelectDropDownValueForPjcAdapter

class SelectDropDownNamePjcBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectDropDownListForPjcBinding
    private lateinit var selectDropDownValueForPjcAdapter: SelectDropDownValueForPjcAdapter

    private var reasonList: ArrayList<LoadDropDownList> = arrayListOf()
    private var fullList: List<LoadDropDownList> = emptyList()

    companion object {
        private const val ARG_LIST = "drop_down_for_list"

        fun newInstance(list: List<LoadDropDownList>): SelectDropDownNamePjcBottomSheetFragment {
            return SelectDropDownNamePjcBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((LoadDropDownList) -> Unit)? = null

    fun setDismissCallback(callback: (LoadDropDownList) -> Unit) {
        dismissCallback = callback
    }

    override fun onStart() {
        super.onStart()

        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog)
                    .findViewById<View>(R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = true
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SAFE: Only read arguments here
        reasonList = arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectDropDownListForPjcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BS_TEST", "reasonList size = ${reasonList.size}")
        setUpRecyclerView()
        manageSearchView()
    }

    private fun setUpRecyclerView() = with(binding) {

        selectDropDownValueForPjcAdapter = SelectDropDownValueForPjcAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = selectDropDownValueForPjcAdapter
        }

        fullList = reasonList.toList() // IMPORTANT: new instance
        // ✅ Set data AFTER adapter initialization
        selectDropDownValueForPjcAdapter.submitList(reasonList)

    }

    private fun manageSearchView(){
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
            selectDropDownValueForPjcAdapter.submitList(fullList)
            return
        }

        val filteredList = fullList.filter {
            it.Name?.contains(query, ignoreCase = true) == true
        }

        selectDropDownValueForPjcAdapter.submitList(filteredList)
    }

}