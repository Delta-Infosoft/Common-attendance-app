package com.i.common.attendance.ui.home.carairapproval.fragment

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
import com.i.common.attendance.databinding.FragmentCarAirApprovalListBinding
import com.i.common.attendance.network.request.UpdateCarAirApprovalStatusRequest
import com.i.common.attendance.network.response.CarAirApprovalItem
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.carairapproval.adapter.CarAirApprovalAdapter
import com.i.common.attendance.ui.home.carairapproval.viewmodel.CarAirApprovalViewModel
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetCarAirApprovalListUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.UpdateCarAirApprovalStatusUiState
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class CarAirApprovalListFragment : BaseFragment() {
    private lateinit var binding : FragmentCarAirApprovalListBinding
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val carAirApprovalViewModel: CarAirApprovalViewModel by viewModels()
    private val carAirApprovalAdapter by lazy {
        CarAirApprovalAdapter(
            onApproveClick = { item ->
                requireContext().showApprovalDialog("Approve"){ remarks ->
                    val user = sharedPref.getUser()
                    item.carAirApprovalId?.let {
                        carAirApprovalViewModel.updateCarAirApprovalStatus(
                            request = UpdateCarAirApprovalStatusRequest(
                                userId = user?.AutoId?:"",
                                approvedDisapproved = "A",
                                carAirApprovalId = it,
                                suspenseApproved = "true",
                                remarks = remarks
                            )
                        )
                    }
                }
            },
            onDisapproveClick = { item ->
                requireContext().showApprovalDialog("Disapprove") { remarks ->
                    val user = sharedPref.getUser()
                    item.carAirApprovalId?.let {
                        carAirApprovalViewModel.updateCarAirApprovalStatus(
                            request = UpdateCarAirApprovalStatusRequest(
                                userId = user?.AutoId?:"",
                                approvedDisapproved = "D",
                                carAirApprovalId = it,
                                suspenseApproved = "false",
                                remarks = remarks
                            )
                        )
                    }
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarAirApprovalListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCall()
        manageToolBar()
        setUpRecyclerView()
        observeCarAirApprovalList()
        observeUpdateCarAirApprovalStatus()
    }
    fun Context.showApprovalDialog(
        status: String, // "Approved" or "Disapproved"
        onConfirm: (remarks: String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_district, null)
        val etRemarks = dialogView.findViewById<TextInputEditText>(R.id.txtMarketCenterName)
        val lyRemarks = dialogView.findViewById<TextInputLayout>(R.id.txtLayMarketCenterName)
        val btnSubmit = dialogView.findViewById<AppCompatButton>(R.id.btnSubmit)
        val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)
        btnSubmit.visibility = View.GONE
        btnCancel.visibility = View.GONE
        etRemarks.setHint("Remarks")
        lyRemarks.hint = "Remarks"

        AlertDialog.Builder(this)
            .setTitle("Confirm $status")
            .setMessage("Are you sure you want to $status this Car/Air approval request?")
            .setView(dialogView)
            .setPositiveButton("Yes") { dialog, _ ->
                val remarks = etRemarks.text.toString().trim()
                onConfirm(remarks)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
    private fun initCall() {
        carAirApprovalViewModel.loadCarAirApprovalList()
    }
    private fun setUpRecyclerView() = with(binding){
        recyclerViewWeekOffApproval.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter = carAirApprovalAdapter
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_car_air_approval_list))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeCarAirApprovalList() {
        carAirApprovalViewModel.getCarAirApprovalListUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetCarAirApprovalListUiState.Idle -> { }
                is GetCarAirApprovalListUiState.Loading -> {
                    showLoader()
                }
                is GetCarAirApprovalListUiState.Success -> {
                    hideLoader()
                    carAirApprovalAdapter.submitList(state.list ?: emptyList())
                }
                is GetCarAirApprovalListUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetCarAirApprovalListUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeUpdateCarAirApprovalStatus() {
        carAirApprovalViewModel.updateCarAirApprovalStatusUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UpdateCarAirApprovalStatusUiState.Idle -> { }
                is UpdateCarAirApprovalStatusUiState.Loading -> {
                    showLoader()
                }
                is UpdateCarAirApprovalStatusUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    initCall()
                }
                is UpdateCarAirApprovalStatusUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is UpdateCarAirApprovalStatusUiState.NetworkError -> {
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