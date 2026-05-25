package com.i.common.attendance.ui.home.touradvanceexpense.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentTourAdvanceExpenseBinding
import com.i.common.attendance.network.request.TourAdvanceExpenseListRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.touradvanceexpense.adapter.TourAdvanceExpenseAdapter
import com.i.common.attendance.ui.home.touradvanceexpense.viewmodel.AdvanceExpenseListState
import com.i.common.attendance.ui.home.touradvanceexpense.viewmodel.TourAdvanceExpenseViewModel
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TourAdvanceExpenseFragment: BaseFragment() {

    private lateinit var binding: FragmentTourAdvanceExpenseBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val tourAdvanceExpense: TourAdvanceExpenseViewModel by viewModels()
    private val tourAdvanceTrackingListAdapter by lazy {
        TourAdvanceExpenseAdapter(
            onItemClick = { item ->
                val bundle = Bundle().apply {
                    putParcelable("AdvanceExpenseItem", item)
                }
                val fragment = AddTourAdvanceExpenseFragment().apply {
                    arguments = bundle
                }
                Log.e("tourVoucherExpenseId",item.toString())
                loadFragment(fragment = fragment, isAdd = false, isAddBackStack = true)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTourAdvanceExpenseBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageToolBar()
        moveOnClickListeners()
        observeData()
        manageRecyclerView()
    }


    override fun onResume() {
        super.onResume()
        binding.txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        binding.txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
    }

    private fun manageRecyclerView() = with(binding){
        tourAdvanceExpense.getAdvanceExpenseList(
            TourAdvanceExpenseListRequest(
                fromDt = txtFromDate.getTrimmedText(),
                toDt = txtToDate.getTrimmedText(),
            )
        )
        recyclerViewUnPlanReport.apply {
            adapter = tourAdvanceTrackingListAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }
    }

    private fun moveOnClickListeners() = with(binding){
        txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))

        imgViewAdd.setSafeOnClickListener {
            loadFragment(fragment = AddTourAdvanceExpenseFragment(), isAdd = false, isAddBackStack = true)
        }
        txtFromDate.setSafeOnClickListener {
            openDatePicker(true)
        }
        txtToDate.setSafeOnClickListener {
            openDatePicker(false)
        }
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.label_tour_advance_expense))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun openDatePicker(isFromDate: Boolean) = with(binding){
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val selectedDate = sdf.format(Date(selection))

            if(isFromDate){
                binding.txtFromDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    tourAdvanceExpense.getAdvanceExpenseList(
                        TourAdvanceExpenseListRequest(
                            fromDt = txtFromDate.getTrimmedText(),
                            toDt = txtToDate.getTrimmedText(),
                        )
                    )
                }
            }else{
                binding.txtToDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    tourAdvanceExpense.getAdvanceExpenseList(
                        TourAdvanceExpenseListRequest(
                            fromDt = txtFromDate.getTrimmedText(),
                            toDt = txtToDate.getTrimmedText(),
                        )
                    )
                }
            }
        }
    }

    private fun observeData(){
        tourAdvanceExpense.listState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is AdvanceExpenseListState.Idle -> {}

                is AdvanceExpenseListState.Loading -> {
                    showLoader()
                }

                is AdvanceExpenseListState.Success -> {
                    hideLoader()

                    tourAdvanceTrackingListAdapter.submitList(state.data)
                }

                is AdvanceExpenseListState.Empty -> {
                    hideLoader()
                    showToast(state.message)

                    tourAdvanceTrackingListAdapter.submitList(emptyList())
                }

                is AdvanceExpenseListState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }

    private fun showLoader() {
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBarPJC.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarPJC.visibility = View.GONE
    }

}