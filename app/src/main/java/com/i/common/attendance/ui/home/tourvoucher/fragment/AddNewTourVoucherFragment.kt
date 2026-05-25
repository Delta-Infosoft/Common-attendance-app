package com.i.common.attendance.ui.home.tourvoucher.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentAddNewTourVoucherBinding
import com.i.common.attendance.network.request.FieldVisitRequest
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.tourvoucher.data.DropdownType
import com.i.common.attendance.ui.home.tourvoucher.data.LocationDropdownType
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.DropdownUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.EmployeeUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.FieldVisitUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.LocationDropdownUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddNewTourVoucherFragment : BaseFragment() {

    private lateinit var binding : FragmentAddNewTourVoucherBinding

    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewTourVoucherBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        moveOnClickListeners()
        observeAllDropDownData()
        observeLocationData()
        observeSubmitData()
        observeEmployeeDataData()

        tourVoucherViewmodel.loadDropdown(DropdownType.BRANCH)
        tourVoucherViewmodel.loadDropdown(DropdownType.ACTIVITY_PLAN)
        tourVoucherViewmodel.loadDropdown(DropdownType.WORK_TYPE)
        tourVoucherViewmodel.loadLocationDropdown(LocationDropdownType.DISTRICT)
        tourVoucherViewmodel.loadLocationDropdown(LocationDropdownType.CITY)
        tourVoucherViewmodel.loadEmployeeDataList()
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_new_tour_voucher))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun moveOnClickListeners() = with(binding){
        txtFromDate.setSafeOnClickListener {
            openDatePicker()
        }

        txtBranch.setSafeOnClickListener {
            val branchList = tourVoucherViewmodel.dropdownCache[DropdownType.BRANCH]

            if (branchList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }

            val bottomSheet = SelectBranchBottomSheetFragment.Companion.newInstance(branchList)
            bottomSheet.setDismissCallback { selected ->
                txtBranch.setText(selected.text)
            }
            bottomSheet.show(childFragmentManager, "SelectBranch")
        }

        txtActivityPlan.setSafeOnClickListener {

            val activityList = tourVoucherViewmodel.dropdownCache[DropdownType.ACTIVITY_PLAN]

            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectActivityPlanBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtActivityPlan.setText(selected.text)
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }

        txtWorkType.setSafeOnClickListener {
            val workTypeList = tourVoucherViewmodel.dropdownCache[DropdownType.WORK_TYPE]

            if (workTypeList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }

            val bottomSheet = SelectWorkTypeBottomSheetFragment.Companion.newInstance(workTypeList)
            bottomSheet.setDismissCallback { selected ->
                txtWorkType.setText(selected.text)
            }
            bottomSheet.show(childFragmentManager, "WorkType")
        }

        txtDistrict.setSafeOnClickListener {
            val locationDistrictList = tourVoucherViewmodel.locationCache[LocationDropdownType.DISTRICT]

            if (locationDistrictList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }

            val bottomSheet = SelectDistrictBottomSheetFragment.Companion.newInstance(locationDistrictList)
            bottomSheet.setDismissCallback { selected ->
                txtDistrict.setText(selected.name)
            }
            bottomSheet.show(childFragmentManager, "WorkType")
        }

        txtCity.setSafeOnClickListener {
            val locationDistrictList = tourVoucherViewmodel.locationCache[LocationDropdownType.CITY]

            if (locationDistrictList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }

            val bottomSheet = SelectDistrictBottomSheetFragment.Companion.newInstance(locationDistrictList)
            bottomSheet.setDismissCallback { selected ->
                txtCity.setText(selected.name)
            }
            bottomSheet.show(childFragmentManager, "WorkType")
        }

        txtEmployee.setSafeOnClickListener {
            val employeeNameList = tourVoucherViewmodel.getCachedEmployeeDataList()

            if (employeeNameList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }

            val bottomSheet = SelectEmployeeNameBottomSheetFragment.Companion.newInstance(employeeNameList)
            bottomSheet.setDismissCallback { selected ->
                txtEmployee.setText(selected.UsersName)
            }
            bottomSheet.show(childFragmentManager, "WorkType")
        }







        btnSubmit.setSafeOnClickListener {
            if (isFormValid()) {
                tourVoucherViewmodel.saveFieldVisit(request = FieldVisitRequest(
                    date = binding.txtFromDate.getTrimmedText(),
                    branch = binding.txtBranch.getTrimmedText(),
                    empName = binding.txtEmployee.getTrimmedText(),
                    workType = binding.txtWorkType.getTrimmedText(),
                    activityPlan = binding.txtActivityPlan.getTrimmedText(),
                    customerName = binding.txtCustomer.getTrimmedText(),
                    district = binding.txtDistrict.getTrimmedText(),
                    city = binding.txtCity.getTrimmedText(),
                    visitOutcome = binding.txtVisitOutcome.getTrimmedText(),
                    salesAmount = binding.txtSales.getTrimmedText(),
                    collectionAmount = binding.txtCollection.getTrimmedText(),
                    shopPotentialPerYear = binding.txtShopPotentialPerYear.getTrimmedText(),
                    contactPersonName = binding.txtContactPersonName.getTrimmedText(),
                    contactNumber = binding.txtContactNumber.getTrimmedText()
                )
                )
            }
        }
    }

    private fun openDatePicker() {

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

            binding.txtFromDate.setText(selectedDate)
            binding.btnSubmit.isEnabled = true
        }
    }


    private fun observeAllDropDownData(){
        tourVoucherViewmodel.dropdownState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is DropdownUiState.Loading -> {
                    showLoader()
                }

                is DropdownUiState.Success -> {
                    when (state.group) {

                        DropdownType.BRANCH -> {
                            hideLoader()

                            //branchAdapter.submitList(state.list)
                        }

                        DropdownType.ACTIVITY_PLAN -> {
                            hideLoader()
                           // activityPlanAdapter.submitList(state.list)
                        }

                        DropdownType.WORK_TYPE -> {
                            hideLoader()
                           // workTypeAdapter.submitList(state.list)
                        }
                    }
                }

                is DropdownUiState.ApiError -> {
                    showToast("${state.group.groupName} : ${state.message}")
                }

                is DropdownUiState.NetworkError -> {
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    private fun observeLocationData() {
        tourVoucherViewmodel.locationDropdownState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is LocationDropdownUiState.Loading -> {
                    when (state.type) {
                        LocationDropdownType.DISTRICT -> showLoader()
                        LocationDropdownType.CITY -> showLoader()
                    }
                }

                is LocationDropdownUiState.Success -> {
                    when (state.type) {
                        LocationDropdownType.DISTRICT -> {
                            hideLoader()
                            //districtAdapter.submitList(state.list)
                        }
                        LocationDropdownType.CITY -> {
                            hideLoader()
                            //cityAdapter.submitList(state.list)
                        }
                    }
                }

                is LocationDropdownUiState.ApiError -> {
                    when (state.type) {
                        LocationDropdownType.DISTRICT -> hideLoader()
                        LocationDropdownType.CITY -> hideLoader()
                    }
                    showToast(state.message)
                }

                is LocationDropdownUiState.NetworkError -> {
                    when (state.type) {
                        LocationDropdownType.DISTRICT -> hideLoader()
                        LocationDropdownType.CITY -> hideLoader()
                    }
                    showToast(state.message)
                }

                else -> Unit // ✅ REQUIRED
            }
        }
    }

    private fun observeSubmitData(){
        tourVoucherViewmodel.fieldVisitState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is FieldVisitUiState.Loading -> {
                    showLoader()
                    manageButtonVisibility(true)
                }

                is FieldVisitUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }

                is FieldVisitUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is FieldVisitUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }

    }


    private fun observeEmployeeDataData(){
        tourVoucherViewmodel.employeeDataState.observe(viewLifecycleOwner) { state ->
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

    private fun isFormValid(): Boolean {

        if (binding.txtFromDate.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_from_date))
            return false
        }

        if (binding.txtBranch.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_branch))
            return false
        }

        if (binding.txtEmployee.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_employee))
            return false
        }

        if (binding.txtWorkType.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_work_type))
            return false
        }

        if (binding.txtActivityPlan.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_activity_plan))
            return false
        }

        if (binding.txtCustomer.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_customer_name))
            return false
        }

        if (binding.txtDistrict.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_district))
            return false
        }

        if (binding.txtCity.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_select_city))
            return false
        }

        if (binding.txtVisitOutcome.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_visit_outcome))
            return false
        }

        if (binding.txtSales.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_sales_amount))
            return false
        }

        if (binding.txtCollection.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_collection_amount))
            return false
        }

        if (binding.txtShopPotentialPerYear.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_shop_potential_per_year))
            return false
        }

        if (binding.txtContactPersonName.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_contact_person_name))
            return false
        }

        if (binding.txtContactNumber.text.isNullOrBlank()) {
            showToast(getString(R.string.validation_please_enter_contact_number))
            return false
        }

        return true
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

    private fun manageButtonVisibility(isInProgress: Boolean) = with(binding) {
        if (isInProgress) {
            btnSubmit.alpha = 0.5f
            btnSubmit.isEnabled = false
        } else {
            btnSubmit.alpha = 1f
            btnSubmit.isEnabled = true
        }
    }


}