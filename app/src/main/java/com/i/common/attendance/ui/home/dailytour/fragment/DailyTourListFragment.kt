package com.i.common.attendance.ui.home.dailytour.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentDailyTourListBinding
import com.i.common.attendance.network.request.DailyTourListRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.adapter.DailyTourDetailsAdapter
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyDetailsState
import com.i.common.attendance.ui.home.dailytour.viewmodel.DailyTourViewModel
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
class DailyTourListFragment : BaseFragment() {
    private lateinit var binding : FragmentDailyTourListBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val dailyTourViewModel: DailyTourViewModel by viewModels()

    private val dailyTourListAdapter by lazy {
        if (BuildConfig.FLAVOR == "flotech") {
            DailyTourDetailsAdapter { item ->

            }
        } else {
            DailyTourDetailsAdapter(null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyTourListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        manageOnClickListeners()
        manageRecyclerView()

        observeDailyTourListData()
    }

    private fun observeDailyTourListData() {
        dailyTourViewModel.dailyDetailsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DailyDetailsState.Idle -> {}
                is DailyDetailsState.Loading -> {
                    showLoader()
                }
                is DailyDetailsState.Success -> {
                    hideLoader()
                    dailyTourListAdapter.submitList(state.data)
                }
                is DailyDetailsState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is DailyDetailsState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is DailyDetailsState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                    dailyTourListAdapter.submitList(emptyList())
                }
            }
        }
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_daily_tour_details))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun manageOnClickListeners() = with(binding){
        txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))

        imgViewAdd.setSafeOnClickListener {
            when (BuildConfig.FLAVOR) {
                "duke" -> {
                    loadFragment(fragment = AddDailyTourDetailsDukeFragment(), isAdd = false, isAddBackStack = true)
                }
                "flotech","singla","algo" -> {
                    loadFragment(fragment = AddDailyTourDetailsFlotechFragment(), isAdd = false, isAddBackStack = true)
                }
                else -> {
                    loadFragment(fragment = AddDailyTourDetailsFragment(), isAdd = false, isAddBackStack = true)
                }
            }
        }
        txtFromDate.setSafeOnClickListener {
            openDatePicker(true)
        }
        txtToDate.setSafeOnClickListener {
            openDatePicker(false)
        }
    }

    private fun openDatePicker(isFromDate: Boolean) = with(binding){

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraints = CalendarConstraints.Builder()
            .setEnd(today)
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today) // Default = today
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->

            val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val selectedDate = sdf.format(Date(selection))

            if(isFromDate){
                txtFromDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    val user = shredPref.getUser()
                    dailyTourViewModel.getDailyDetailsList(
                        DailyTourListRequest(
                            mobileNo = user?.MobileNo ?: "",
                            fromDate = txtFromDate.getTrimmedText(),
                            toDate = txtToDate.getTrimmedText()
                        )
                    )
                }
            }else{
                txtToDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    val user = shredPref.getUser()
                    dailyTourViewModel.getDailyDetailsList(
                        DailyTourListRequest(
                            mobileNo = user?.MobileNo ?: "",
                            fromDate = txtFromDate.getTrimmedText(),
                            toDate = txtToDate.getTrimmedText()
                        )
                    )
                }
            }
        }
    }

    private fun manageRecyclerView() = with(binding){
        val user = shredPref.getUser()
        dailyTourViewModel.getDailyDetailsList(
            DailyTourListRequest(
                mobileNo = user?.MobileNo ?: "",
                fromDate = txtFromDate.getTrimmedText(),
                toDate = txtToDate.getTrimmedText()
            )
        )
        recyclerViewDailyTour.apply {
            adapter = dailyTourListAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
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