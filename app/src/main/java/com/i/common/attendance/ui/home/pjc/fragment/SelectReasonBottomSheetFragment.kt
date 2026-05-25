package com.i.common.attendance.ui.home.pjc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectReasonBinding
import com.i.common.attendance.network.response.ReasonList
import com.i.common.attendance.ui.home.pjc.adapter.SelectReasonAdapter

class SelectReasonBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectReasonBinding
    private lateinit var selectReasonAdapter: SelectReasonAdapter

    private var reasonList: ArrayList<ReasonList> = arrayListOf()

    companion object {
        private const val ARG_LIST = "reason_for_list"

        fun newInstance(list: List<ReasonList>): SelectReasonBottomSheetFragment {
            return SelectReasonBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((ReasonList) -> Unit)? = null

    fun setDismissCallback(callback: (ReasonList) -> Unit) {
        dismissCallback = callback
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
        binding = BottomSheetFragmentSelectReasonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = with(binding) {

        selectReasonAdapter = SelectReasonAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectReasonAdapter
        }

        // ✅ Set data AFTER adapter initialization
        selectReasonAdapter.submitList(reasonList)
    }
}