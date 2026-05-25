package com.i.common.attendance.ui.home.pjc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectPlanForBinding
import com.i.common.attendance.network.response.PlanForList
import com.i.common.attendance.ui.home.pjc.adapter.SelectPlanForAdapter

class SelectPlanForBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSelectPlanForBinding
    private lateinit var selectPlanForAdapter: SelectPlanForAdapter

    private var planForList: ArrayList<PlanForList> = arrayListOf()

    companion object {
        private const val ARG_LIST = "plan_for_list"

        fun newInstance(list: List<PlanForList>): SelectPlanForBottomSheetFragment {
            return SelectPlanForBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LIST, ArrayList(list))
                }
            }
        }
    }

    private var dismissCallback: ((PlanForList) -> Unit)? = null

    fun setDismissCallback(callback: (PlanForList) -> Unit) {
        dismissCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ SAFE: Only read arguments here
        planForList =
            arguments?.getParcelableArrayList(ARG_LIST) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSelectPlanForBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = with(binding) {

        selectPlanForAdapter = SelectPlanForAdapter { selectedStatus ->
            dismissCallback?.invoke(selectedStatus)
            dismiss()
        }

        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectPlanForAdapter
        }

        // ✅ Set data AFTER adapter initialization
        selectPlanForAdapter.submitList(planForList)
    }
}