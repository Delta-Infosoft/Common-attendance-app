package com.i.common.attendance.ui.home.pjc.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.i.common.attendance.databinding.BottomSheetFragmentSelectPjcEventBinding
import com.i.common.attendance.network.response.PjcEventFullData
import com.i.common.attendance.ui.home.pjc.adapter.NewDealerAppointmentAdapter
import com.i.common.attendance.ui.home.pjc.adapter.NewDealerSurveyAdapter
import com.i.common.attendance.ui.home.pjc.adapter.OrderFollowUpAdapter
import com.i.common.attendance.ui.home.pjc.adapter.PaymentFollowUpAdapter
import com.i.common.attendance.ui.home.pjc.adapter.SelectPjcEventAdapter
import com.i.common.attendance.ui.home.pjc.adapter.SubDealerVisitAdapter

class SelectPjcEventBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var events: PjcEventFullData

    private lateinit var binding: BottomSheetFragmentSelectPjcEventBinding
    private val selectPjcEventAdapter by lazy {
        SelectPjcEventAdapter()
    }
    private val paymentFollowUpAdapter by lazy {
        PaymentFollowUpAdapter()
    }
    private val orderFollowUpAdapter by lazy {
        OrderFollowUpAdapter()
    }
    private val newDealerAppointmentAdapter by lazy {
        NewDealerAppointmentAdapter()
    }
    private val subDealerVisitAdapter by lazy {
        SubDealerVisitAdapter()
    }
    private val newDealerSurveyAdapter by lazy {
        NewDealerSurveyAdapter()
    }

    companion object {
        private const val ARG_EVENTS = "arg_events"

        fun newInstance(events: PjcEventFullData): SelectPjcEventBottomSheetFragment {
            return SelectPjcEventBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_EVENTS, events)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            events = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_EVENTS, PjcEventFullData::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(ARG_EVENTS)
            } ?: PjcEventFullData(
                pjcEvents = emptyList(),
                paymentFollowUps = emptyList(),
                orderFollowUps = emptyList(),
                newDealerAppointmentFollowUps = emptyList(),
                subDealerVisitFollowUps = emptyList(),
                newDealerSurvey = emptyList()
                ) // ✅ SAFE fallback

            Log.e("Event SelectPjcEventBottomSheetFragment", events.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFragmentSelectPjcEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewStatus.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = selectPjcEventAdapter
        }

        recyclerViewPaymentFollowUp.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = paymentFollowUpAdapter
        }

        recyclerViewOrderFollowUp.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = orderFollowUpAdapter
        }

        recyclerViewNewDealerAppointment.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = newDealerAppointmentAdapter
        }

        recyclerViewSubDealerVisit.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = subDealerVisitAdapter
        }

        recyclerViewNewDealerSurvey.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = newDealerSurveyAdapter
        }

        if(events.paymentFollowUps.isEmpty()) constPayment.visibility = View.GONE
        if(events.orderFollowUps.isEmpty()) constOrder.visibility = View.GONE
        if(events.newDealerAppointmentFollowUps.isEmpty()) constNewDealerAppointment.visibility = View.GONE
        if(events.subDealerVisitFollowUps.isEmpty()) constSubDealerAppointment.visibility = View.GONE
        if(events.newDealerSurvey.isEmpty()) constNewDealerSurvey.visibility = View.GONE

        selectPjcEventAdapter.submitList(events.pjcEvents)
        paymentFollowUpAdapter.submitList(events.paymentFollowUps)
        orderFollowUpAdapter.submitList(events.orderFollowUps)
        newDealerAppointmentAdapter.submitList(events.newDealerAppointmentFollowUps)
        subDealerVisitAdapter.submitList(events.subDealerVisitFollowUps)
        newDealerSurveyAdapter.submitList(events.newDealerSurvey)

    }
}