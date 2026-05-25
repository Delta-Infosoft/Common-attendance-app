package com.i.common.attendance.ui.home.touradvanceexpense.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentAddTourAdvanceExpenseBinding
import com.i.common.attendance.network.request.AddTourAdvanceExpenseRequest
import com.i.common.attendance.network.response.TourAdvanceExpense
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.touradvanceexpense.viewmodel.AdvanceExpenseState
import com.i.common.attendance.ui.home.touradvanceexpense.viewmodel.TourAdvanceExpenseViewModel
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AddTourAdvanceExpenseFragment: BaseFragment() {

    private lateinit var binding: FragmentAddTourAdvanceExpenseBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val tourAdvanceExpense: TourAdvanceExpenseViewModel by viewModels()
    private var advanceExpenseId: String? = null
    private var advanceExpenseItem: TourAdvanceExpense? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTourAdvanceExpenseBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        advanceExpenseItem = arguments?.getParcelable("AdvanceExpenseItem")
        advanceExpenseId = advanceExpenseItem?.AdvanceExpenseId

        setEditData()
        manageToolBar()
        moveOnClickListeners()
        observeData()
    }

    private fun setEditData() = with(binding) {
        advanceExpenseItem?.let { item ->
            txtName.setText(item.EmployeeName)
            txtRequestDate.setText(Constants.convertDateFormat(item.RequestDt ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy"))
            txtAdvanceAmount.setText(item.AdvanceAmount ?.toDoubleOrNull()
                ?.toInt()
                ?.toString()
                ?: "0")
            txtRemarks.setText(item.Remarks)
        }
    }

    private fun moveOnClickListeners() = with(binding){
        txtName.setText(shredPref.getUser()?.UsersName)
        txtRequestDate.setOnClickListener {
             openDatePicker(isFromDate = true)
        }
        btnSubmit.setSafeOnClickListener {
            val empId = shredPref.getUser()?.EmpID ?: ""
            val requestDate = txtRequestDate.text.toString().trim()
            val amount = txtAdvanceAmount.text.toString().trim()
            val remarks = txtRemarks.text.toString().trim()

            /*================ VALIDATION =================*/

            when {
                empId.isEmpty() -> {
                    showToast("Employee ID not found")
                    return@setSafeOnClickListener
                }

                requestDate.isEmpty() -> {
                    showToast("Please select request date")
                    return@setSafeOnClickListener
                }

                amount.isEmpty() -> {
                    showToast("Please enter advance amount")
                    return@setSafeOnClickListener
                }

                amount.toDoubleOrNull() == null -> {
                    showToast("Enter valid amount")
                    return@setSafeOnClickListener
                }

                remarks.isEmpty() -> {
                    showToast("Please enter remarks")
                    return@setSafeOnClickListener
                }
            }

            /*================ REQUEST =================*/

            val request = AddTourAdvanceExpenseRequest(
                empId = empId,
                requestDt = requestDate,
                advanceAmount = amount,
                remarks = remarks,
                advanceExpenseId = advanceExpenseId?.takeIf { it.isNotEmpty() }
            )

            /*================ INSERT / UPDATE =================*/

            if (advanceExpenseId.isNullOrEmpty()) {
                tourAdvanceExpense.insertAdvanceExpense(request)
            } else {
                tourAdvanceExpense.updateAdvanceExpense(request)
            }
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
    private fun openDatePicker(isFromDate: Boolean) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(selection))
            if (isFromDate) {
                binding.txtRequestDate.setText(selectedDate)
            } else {
                binding.txtRequestDate.setText(selectedDate)
            }
        }
    }
    private fun observeData(){
        /*================ INSERT OBSERVER ================*/
        tourAdvanceExpense.insertState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is AdvanceExpenseState.Idle -> {
                    // Initial state
                }

                is AdvanceExpenseState.Loading -> {
                    showLoader()
                }

                is AdvanceExpenseState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                    // Optional: close screen after success
                    // findNavController().popBackStack()
                }

                is AdvanceExpenseState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }

                is AdvanceExpenseState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }

        /*================ UPDATE OBSERVER ================*/
        tourAdvanceExpense.updateState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is AdvanceExpenseState.Idle -> {}

                is AdvanceExpenseState.Loading -> {
                    showLoader()
                }

                is AdvanceExpenseState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }

                is AdvanceExpenseState.Empty -> {
                    hideLoader()
                    showToast(state.message)
                }

                is AdvanceExpenseState.Error -> {
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