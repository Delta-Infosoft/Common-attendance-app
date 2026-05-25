package com.i.common.attendance.ui.home.touragendatracking.fragment

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
import com.i.common.attendance.databinding.FragmentWeekOffListDukeBinding
import com.i.common.attendance.network.request.ValidateSundayRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.touragendatracking.adapter.WeekOffListAdapter
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetWeekOffListUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeekOffListDukeFragment : BaseFragment() {
    private lateinit var binding : FragmentWeekOffListDukeBinding
    private val tourAgendaViewModel: TourAgendaTrackingViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val weekOffAdapter by lazy {
        WeekOffListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = sharedPref.getUser()
        tourAgendaViewModel.loadWeekOffList(request = ValidateSundayRequest(empId = user?.EmpID?:""))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekOffListDukeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageToolBar()
        observeWeekOffList()
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewWeekOffStatus.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = weekOffAdapter
        }
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_week_off_list))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeWeekOffList() {
        tourAgendaViewModel.getWeekOffListUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetWeekOffListUiState.Idle -> { }
                is GetWeekOffListUiState.Loading -> {
                    showLoader()
                }
                is GetWeekOffListUiState.Success -> {
                    hideLoader()
                    weekOffAdapter.submitList(state.list)
                }
                is GetWeekOffListUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetWeekOffListUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun showLoader() {
        requireActivity().window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }

        binding.progressBarPJC.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        binding.progressBarPJC.visibility = View.GONE
    }

}