package com.i.common.attendance.ui.home.touragendatracking.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentWeekOffApprovalBinding
import com.i.common.attendance.network.request.SubmitSundayApprovalRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.touragendatracking.adapter.SundayRequestAdapter
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetSundayRequestListUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.SubmitSundayApprovalUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeekOffApprovalListDukeFragment : BaseFragment() {
    private lateinit var binding: FragmentWeekOffApprovalBinding
    private val tourAgendaViewModel: TourAgendaTrackingViewModel by viewModels()
    @Inject lateinit var sharedPref: EncryptedPrefHelper

    private val sundayRequestAdapter by lazy {
        SundayRequestAdapter { item, isApprove, position ->
            if (isApprove) {
                requireContext().showApprovalDialog("Approve"){
                    val user = sharedPref.getUser()
                    item.sundayRequestId?.let {
                        tourAgendaViewModel.submitSundayApproval(
                            request = SubmitSundayApprovalRequest(
                                empId = user?.EmpID ?: "",
                                approvedDisapproved = "A",
                                approvedByUserId = user?.InsertedByUserId ?: "",
                                sundayRequestId = it
                            )
                        )
                    }
                }
            } else {
                requireContext().showApprovalDialog("Disapprove"){
                    val user = sharedPref.getUser()
                    item.sundayRequestId?.let {
                        tourAgendaViewModel.submitSundayApproval(
                            request = SubmitSundayApprovalRequest(
                                empId = user?.EmpID ?: "",
                                approvedDisapproved = "D",
                                approvedByUserId = user?.InsertedByUserId ?: "",
                                sundayRequestId = it
                            )
                        )
                    }
                }
            }
        }
    }

    fun Context.showApprovalDialog(
        status: String, // "Approved" or "Disapproved"
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(this)
            .setTitle("Confirm $status")
            .setMessage("Are you sure you want to $status this Week Off Day approval request?")
            .setPositiveButton("Yes") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tourAgendaViewModel.loadSundayRequestList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekOffApprovalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        manageToolBar()
        observeSundayRequestList()
        observeSubmitSundayApproval()
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewWeekOffApproval.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = sundayRequestAdapter
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_week_off_day_approval))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeSundayRequestList() {
        tourAgendaViewModel.getSundayRequestListUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetSundayRequestListUiState.Idle -> { }
                is GetSundayRequestListUiState.Loading -> {
                    showLoader()
                }
                is GetSundayRequestListUiState.Success -> {
                    hideLoader()
                    sundayRequestAdapter.submitList(state.list)
                }
                is GetSundayRequestListUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetSundayRequestListUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeSubmitSundayApproval() {
        tourAgendaViewModel.submitSundayApprovalUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SubmitSundayApprovalUiState.Idle -> { }
                is SubmitSundayApprovalUiState.Loading -> {
                    showLoader()
                }
                is SubmitSundayApprovalUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    tourAgendaViewModel.loadSundayRequestList()
                }
                is SubmitSundayApprovalUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is SubmitSundayApprovalUiState.NetworkError -> {
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