package com.i.common.attendance.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentSelectStatusBinding
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.PromotionalActivityViewModel
import com.i.common.attendance.ui.home.dealercheckin.viewmodel.TargetOutstandingState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.hideKeyboard
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.utils.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class SelectStatusFragment: BaseFragment() {
    private lateinit var binding: FragmentSelectStatusBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val targetOutStandingViewModel: PromotionalActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectStatusBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moveOnClickListeners()
        manageToolBar()
        handleBackPress()
        manageCheckInDetailsInfoForUnnati()
        observeTargetOutstanding()
    }

    private fun manageCheckInDetailsInfoForUnnati() = with(binding) {
        if (BuildConfig.FLAVOR == "unnati") {
            cardViewCheckInDetailsUnnati.visibility = View.VISIBLE
            targetOutStandingViewModel.getTargetOutstanding(shredPref.getUser()?.EmpID ?: "")
        } else {
            cardViewCheckInDetailsUnnati.visibility = View.GONE
        }
    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_select_status))
            isIconVisible(false)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            sendResultAndGoBack()
        }
    }
    private fun sendResultAndGoBack() {
        val statusText = binding.editTextStatus.text.toString()
        val remark = binding.editTextRemark.getTrimmedText()

        if (statusText.isNotEmpty()) {
            val statusCode = getStatusCode(statusText)

            parentFragmentManager.setFragmentResult(
                FragmentResultKeys.STATUS_RESULT,
                Bundle().apply {
                   // putString(FragmentResultKeys.KEY_STATUS, statusText)
                    putString(FragmentResultKeys.KEY_STATUS, statusCode)
                    putString(FragmentResultKeys.KEY_REMARK, remark)
                }
            )
        }

        parentFragmentManager.popBackStack()
    }


    private fun moveOnClickListeners() = with(binding){
        editTextStatus.setSafeOnClickListener {
            val selectStatusBottomSheetDialogFragment = SelectStatusBottomSheetDialogFragment()
            selectStatusBottomSheetDialogFragment.setDismissCallback { status ->
                editTextStatus.setText(status)
                Log.e("Status",status)
                Log.e("Remark",editTextRemark.getTrimmedText())
            }
            selectStatusBottomSheetDialogFragment.show(childFragmentManager,"")
        }

        btnSubmit.setSafeOnClickListener {
            hideKeyboard(it)
            sendResultAndGoBack()
        }
    }

    private fun getStatusCode(status: String): String {
        // If format is CODE-Text (e.g. "A-Absent")
        if (status.contains("-")) {
            return status.substringBefore("-").trim()
        }

        return when (status.uppercase()) {
            "ABSENT" -> "A"
            "HALF DAY PRESENT" -> "HF"
            "WEEKLY OFF PRESENT" -> "WOP"
            "CASUAL LEAVE OFF" -> "COFF"
            "FULL DAY PRESENT" -> "P"
            "WEEKLY OFF HALF DAY PRESENT" -> "WOHF"
            "OUTDOOR DUTY" -> "OOD"
            else -> "A" // default fallback
        }
    }

    private fun observeTargetOutstanding() = with(binding){
        targetOutStandingViewModel.targetOutstandingState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is TargetOutstandingState.Idle -> {
                }

                is TargetOutstandingState.Loading -> {
                    showLoader()
                }

                is TargetOutstandingState.Success -> {
                    hideLoader()
                    val data = state.list.firstOrNull()

                    val target = data?.targetAmt ?: "0"
                    val achieved = data?.achievedAmt ?: "0"
                    val outstanding = data?.outstandingAmt ?: "0"

                    txtViewTotalTarget.text = "Total Target : ₹$target"
                    txtViewAchievement.text = "Achievement : ₹$achieved"
                    txtViewOutstanding.text = "Outstanding : ₹$outstanding"
                }
                is TargetOutstandingState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is TargetOutstandingState.NetworkError -> {
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