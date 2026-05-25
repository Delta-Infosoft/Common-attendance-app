package com.i.common.attendance.ui.home.tourvoucher.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentTourVoucherBinding
import com.i.common.attendance.network.request.EmployeeRequest
import com.i.common.attendance.network.request.TourVoucherRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.tourvoucher.adapter.TourVoucherListAdapter
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.EmployeeUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
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
class TourVoucherListFragment : BaseFragment() {

    private lateinit var binding : FragmentTourVoucherBinding
    @Inject lateinit var shredPref: EncryptedPrefHelper
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    private val tourVoucherListAdapter by lazy {
        TourVoucherListAdapter(
            onEditClick = { item ->
                val expenseId = item.ExpenseId ?: return@TourVoucherListAdapter
                val bundle = Bundle().apply {
                    putString("ExpenseId", expenseId)
                }
                val fragment = AddTourVoucherFragment().apply {
                    arguments = bundle
                }
                Log.e("tourVoucherExpenseId",expenseId)
                loadFragment(fragment = fragment, isAdd = false, isAddBackStack = true)
            },
            onReportClick = { item ->
                // 🔥 Handle report click here
                val reportUrl = item.TourReport ?: return@TourVoucherListAdapter
                val url = Constants.getReplacedString(reportUrl)

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                }
                startActivity(intent)
            }
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //tourVoucherViewmodel.loadEmployeeList(EmployeeRequest(shredPref.getUser()?.MobileNo ?: ""))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTourVoucherBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        manageOnClickListeners()
        manageRecyclerView()

        //observeEmployeeData()
        observeVoucherList()
    }

    private fun manageOnClickListeners() = with(binding){
        txtFromDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        txtToDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))

        imgViewAdd.setSafeOnClickListener {
            loadFragment(fragment = AddTourVoucherFragment(), isAdd = false, isAddBackStack = true)
        }
        txtFromDate.setSafeOnClickListener {
            openDatePicker(true)
        }
        txtToDate.setSafeOnClickListener {
            openDatePicker(false)
        }

       /* txtEmployee.setSafeOnClickListener {
            val list = tourVoucherViewmodel.getCachedEmployeeList()
            val bottomSheet = list?.let { it1 -> SelectEmployeeBottomSheetFragment.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtEmployee.setText(selected.UsersName)
                if(txtFromDate.getTrimmedText().isEmpty() && txtToDate.getTrimmedText().isEmpty()){
                    showToast(getString(R.string.validation_please_select_date))
                    return@setDismissCallback
                }
                val user = shredPref.getUser()
                selected.AutoId?.let { empId -> tourVoucherViewmodel.loadTourVoucherList(
                    TourVoucherRequest(
                        MobileNo = user?.MobileNo ?: "",
                        FromDt = txtFromDate.getTrimmedText(),
                        ToDt = txtToDate.getTrimmedText(),
                        EmpId = empId
                    )
                ) }
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }*/
    }

    private fun manageRecyclerView() = with(binding){
        val user = shredPref.getUser()
        tourVoucherViewmodel.loadTourVoucherList(
            TourVoucherRequest(
                MobileNo = user?.MobileNo ?: "",
                FromDt = txtFromDate.getTrimmedText(),
                ToDt = txtToDate.getTrimmedText(),
            )
        )
        recyclerViewUnPlanReport.apply {
            adapter = tourVoucherListAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        }
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toobar_title_tour_voucher))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
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
                binding.txtFromDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    val user = shredPref.getUser()
                    tourVoucherViewmodel.loadTourVoucherList(
                        TourVoucherRequest(
                            MobileNo = user?.MobileNo ?: "",
                            FromDt = txtFromDate.getTrimmedText(),
                            ToDt = txtToDate.getTrimmedText(),
                        )
                    )
                }
            }else{
                binding.txtToDate.setText(selectedDate)
                if(txtFromDate.getTrimmedText().isNotEmpty() && txtToDate.getTrimmedText().isNotEmpty()){
                    val user = shredPref.getUser()
                    tourVoucherViewmodel.loadTourVoucherList(
                        TourVoucherRequest(
                            MobileNo = user?.MobileNo ?: "",
                            FromDt = txtFromDate.getTrimmedText(),
                            ToDt = txtToDate.getTrimmedText(),
                        )
                    )
                }
            }
        }
    }

    private fun observeEmployeeData(){
        tourVoucherViewmodel.employeeState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EmployeeUiState.Loading -> showLoader()

                is EmployeeUiState.Success -> {
                    hideLoader()
                }

                is EmployeeUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is EmployeeUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is EmployeeUiState.UnknownError -> {
                    hideLoader()
                    showToast("Something went wrong")
                }

                else -> Unit
            }
        }
    }

    private fun observeVoucherList(){
        tourVoucherViewmodel.tourVoucherState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is TourVoucherUiState.Loading -> {
                    showLoader()
                }

                is TourVoucherUiState.Success -> {
                    hideLoader()
                    tourVoucherListAdapter.submitList(state.list)
                }

                is TourVoucherUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is TourVoucherUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
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