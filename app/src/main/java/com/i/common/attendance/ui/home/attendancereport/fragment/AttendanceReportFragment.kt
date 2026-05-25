package com.i.common.attendance.ui.home.attendancereport.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentAttendanceReportBinding
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.adapter.LastFiveDayRecordsAdapter
import com.i.common.attendance.ui.home.attendancereport.viewmodel.AttendanceReportViewModel
import com.i.common.attendance.ui.home.attendancereport.viewmodel.MonthUiState
import com.i.common.attendance.ui.home.viewmodel.GetRecordsState
import com.i.common.attendance.ui.home.viewmodel.HomeViewModel
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceReportFragment :  BaseFragment() {
    private lateinit var binding: FragmentAttendanceReportBinding
    private val attendanceReportViewmodel: AttendanceReportViewModel by viewModels()
    private val viewModel: HomeViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val lastFiveDayAdapter by lazy { LastFiveDayRecordsAdapter(sharedPrefHelper = sharedPref,false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attendanceReportViewmodel.loadMonthList()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceReportBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        moveOnClickListeners()
        setRecyclerView()
        observeMonthList()
        observeAttendanceRecords()
    }

    private fun setRecyclerView() = with(binding) {
        recyclerViewRecords.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = lastFiveDayAdapter
        }
    }

    private fun moveOnClickListeners() = with(binding){
        txtMonth.setSafeOnClickListener {
            val list = attendanceReportViewmodel.getCachedMonthList()
            val bottomSheet = list?.let { it1 -> SelectMonthBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtMonth.setText(selected.Month)
                val user = sharedPref.getUser()
                viewModel.getRecords(
                    mobileNo = user?.MobileNo ?: "",
                    month = selected.Month ?:""
                )
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_attendance_report))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeMonthList() {
        attendanceReportViewmodel.monthListState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is MonthUiState.Loading -> showLoader()

                is MonthUiState.Success -> {
                    hideLoader()
                }

                is MonthUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is MonthUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> {}
            }
        }
    }
    private fun observeAttendanceRecords(){
        viewModel.recordsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetRecordsState.Loading -> showLoader()
                is GetRecordsState.Success -> {
                    hideLoader();
                    lastFiveDayAdapter.submitList(state.records)
                }
                is GetRecordsState.Empty -> {
                    hideLoader(); showToast(state.message)
                }
                is GetRecordsState.Error -> {
                    hideLoader(); showToast(state.message)
                }
                else -> Unit
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