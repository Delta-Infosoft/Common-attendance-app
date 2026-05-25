package com.i.common.attendance.ui.home.carairapproval.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentCarAirApprovalBinding
import com.i.common.attendance.network.request.GetCityTypeListDukeRequest
import com.i.common.attendance.network.request.GetRatePerKMRequest
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.GetTravelDukeRequest
import com.i.common.attendance.network.request.GetVoucherNoDukeRequest
import com.i.common.attendance.network.request.InsertCarAirApprovalRequest
import com.i.common.attendance.network.response.EmployeeDataDuke
import com.i.common.attendance.network.response.GetRatePerKM
import com.i.common.attendance.network.response.TravelData
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.carairapproval.viewmodel.CarAirApprovalViewModel
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetCityTypeUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetCityUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetEmpDataUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetRatePerKMUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetTravellingByCarUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.GetVoucherNoUiState
import com.i.common.attendance.ui.home.carairapproval.viewmodel.InsertCarApprovalUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.ui.home.tourvoucher.fragment.SelectTravelByBottomSheetFragment
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.isEmpty
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class CarAirApprovalFragment : BaseFragment() {
    private lateinit var binding: FragmentCarAirApprovalBinding
    @Inject lateinit var sharedPref: EncryptedPrefHelper
    private val carAirApprovalViewModel: CarAirApprovalViewModel by viewModels()

    private var deptId : String = ""
    private var selectedCityTypeId : String = ""
    private var selectedTravelingByForCarId : String = ""
    private var selectedCitiesId : String = ""
    private var perKmRateValue = 0.0
    private var totalChargeKmValue = 0.0
    private var totalKmValue = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = sharedPref.getUser()
        carAirApprovalViewModel.loadEmpData(request = GetStateRequest(empId = user?.EmpID?:""))
        carAirApprovalViewModel.loadVoucherNo(request = GetVoucherNoDukeRequest(date = Constants.getCurrentTimestamp("dd-MMM-yyyy"), voucherTypeId = "871EDA46-62DD-4A73-BB5A-08D15A82B41D"))
        carAirApprovalViewModel.loadTravellingByCar(request = GetTravelDukeRequest(isCarYes = false.toString()))
        carAirApprovalViewModel.loadCityList()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarAirApprovalBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = sharedPref.getUser()
        binding.txtPunchNo.setText(user?.MobileNo?:"")
        binding.txtDate.setText(Constants.getCurrentTimestamp("dd-MMM-yyyy"))
        manageToolBar()
        moveOnClickListeners()
        observeEmpData()
        observeVoucherNo()
        observeTravellingByCar()
        observeRatePerKM()
        observeCity()
        observeCityType()
        observeInsertCarApproval()
    }
    fun getApprovalData(status: String): Pair<String, String> {
        return if (status == "Approved") {
            "A" to "true"
        } else {
            "D" to "false"
        }
    }
    fun TextInputEditText.afterTextChanged(action: (String) -> Unit) {
        this.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                action.invoke(s?.toString() ?: "")
            }
        })
    }
    fun calculateTotal(){
        val perKmRate = binding.txtPerKm.text.toString().trim().toDoubleOrNull() ?: 0.0
        val totalKm = binding.txtTotalKm.text.toString().trim().toDoubleOrNull() ?: 0.0

        val finalTotalAmount = perKmRate * totalKm

        binding.txtTotalAmount.setText(String.format("%.2f", finalTotalAmount))
    }
    private fun openDatePicker(isFromDate: Boolean) {

        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val constraints = CalendarConstraints.Builder()
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.dialog_title_select_date))
            .setSelection(today)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(selection))
            if(isFromDate){
                binding.txtJourneyFrom.setText(selectedDate)
            }else{
                binding.txtJourneyTo.setText(selectedDate)
            }
        }
    }
    private fun validateFormWithToast(): Boolean = with(binding)  {
        if (txtJourneyFrom.isEmpty()) {
            showToast(getString(R.string.validation_please_select_journey_from_date))
            return false
        }

        if (txtJourneyTo.isEmpty()) {
            showToast(getString(R.string.validation_please_select_journey_to_date))
            return false
        }

        if (txtFromPlace.isEmpty()) {
            showToast(getString(R.string.validation_please_select_from_place))
            return false
        }

        if (txtToPlace.isEmpty()) {
            showToast(getString(R.string.validation_please_enter_to_place))
            return false
        }

        if (txtTotalKm.isEmpty()) {
            showToast(getString(R.string.validation_please_enter_total_km))
            return false
        }

        if (txtTravelling.isEmpty()) {
            showToast(getString(R.string.validation_please_select_travel_by))
            return false
        }

        if (txtPerKm.isEmpty()) {
            showToast(getString(R.string.validation_please_enter_per_km_rate))
            return false
        }

        if (txtTotalAmount.isEmpty()) {
            showToast(getString(R.string.validation_please_enter_total_amount))
            return false
        }
        return true
    }
    private fun moveOnClickListeners() = with(binding){
        txtJourneyFrom.setSafeOnClickListener {
            openDatePicker(true)
        }
        txtJourneyTo.setSafeOnClickListener {
            openDatePicker(false)
        }

        txtPerKm.afterTextChanged { calculateTotal() }

        txtTotalKm.afterTextChanged { calculateTotal() }

        txtTravelling.setSafeOnClickListener {
            val list = carAirApprovalViewModel.cachedTravellingByCarList
            val bottomSheet = list?.let { it1 -> SelectTravelByBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtTravelling.setText(selected.Text)
                selectedTravelingByForCarId = selected.TextListId?:""
                carAirApprovalViewModel.refreshRatePerKM(GetRatePerKMRequest(travelingByForCarId = selected.TextListId?:""))
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }

        txtCities.setSafeOnClickListener {
            val list = carAirApprovalViewModel.cachedCityList
            val bottomSheet = list?.let { it1 -> SelectCityBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtCities.setText(selected.name)
                selectedCitiesId = selected.tadaCityId?:""
                carAirApprovalViewModel.refreshCityTypeList(GetCityTypeListDukeRequest(cityId = selected.tadaCityId?:""))
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }

        txtCityType.setSafeOnClickListener {
            val list = carAirApprovalViewModel.cachedCityTypeList
            val bottomSheet = list?.let { it1 -> SelectCityTypeBottomSheetFragment.Companion.newInstance(it1) }
            bottomSheet?.setDismissCallback { selected ->
                txtCityType.setText(selected.text)
                selectedCityTypeId = selected.textListId?:""
            }
            bottomSheet?.show(childFragmentManager, "SelectPlanFor")
        }

        btnSubmit.setSafeOnClickListener {
            if (!validateFormWithToast()) {
                return@setSafeOnClickListener
            }

            val user = sharedPref.getUser()
            val request = InsertCarAirApprovalRequest(
                empId = user?.EmpID?:"",
                designation = txtDesignation.getTrimmedText(),
                team = txtTeam.getTrimmedText(),
                deptId = deptId,
                journeyFromDate = txtJourneyFrom.getTrimmedText(),
                journeyToDate = txtJourneyTo.getTrimmedText(),
                fromPlace = txtFromPlace.getTrimmedText(),
                toPlace = txtToPlace.getTrimmedText(),
                totalKMsActual = txtTotalKm.getTrimmedText(),
                totalChargedKMs = "",
                travelingbyforCarId = selectedTravelingByForCarId,
                travellingBy = txtTravelling.getTrimmedText(),
                perKMRate = txtPerKm.getTrimmedText(),
                totalAmt = txtTotalAmount.getTrimmedText(),
                tadaCityId = selectedCitiesId,
                cityTypeTextListId = selectedCityTypeId
            )
            carAirApprovalViewModel.insertCarApproval(request)
        }

    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_car_air_approval))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }
    private fun observeEmpData() {
        carAirApprovalViewModel.getEmpDataUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetEmpDataUiState.Idle -> { }
                is GetEmpDataUiState.Loading -> {
                    showLoader()
                }
                is GetEmpDataUiState.Success -> {
                    hideLoader()
                    handleEmpData(state.list)
                }
                is GetEmpDataUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetEmpDataUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun handleEmpData(list: List<EmployeeDataDuke>) = with(binding){
        val firstItem = list.firstOrNull()
        firstItem?.let {
            deptId = it.deptId?:""
            txtTeam.setText(it.team)
            txtDesignation.setText(it.designation)
            txtDepartment.setText(it.deptName)
        }
    }
    private fun observeVoucherNo() = with(binding){
        carAirApprovalViewModel.getVoucherNoUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetVoucherNoUiState.Idle -> { }
                is GetVoucherNoUiState.Loading -> {
                    showLoader()
                }
                is GetVoucherNoUiState.Success -> {
                    hideLoader()
                    txtVoucherNo.setText(state.voucherNo)
                }
                is GetVoucherNoUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetVoucherNoUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeTravellingByCar() {
        carAirApprovalViewModel.getTravellingByCarUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetTravellingByCarUiState.Idle -> { }
                is GetTravellingByCarUiState.Loading -> {
                    showLoader()
                }
                is GetTravellingByCarUiState.Success -> {
                    hideLoader()
                }
                is GetTravellingByCarUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetTravellingByCarUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeRatePerKM() {
        carAirApprovalViewModel.getRatePerKMUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetRatePerKMUiState.Idle -> { }
                is GetRatePerKMUiState.Loading -> {
                    showLoader()
                }
                is GetRatePerKMUiState.Success -> {
                    hideLoader()
                    handleRatePerKM(state.list)
                }
                is GetRatePerKMUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetRatePerKMUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun handleRatePerKM(list: List<GetRatePerKM>) = with(binding){
        val firstItem = list.firstOrNull()

        if (firstItem != null) {

            val ratePerKms = firstItem.ratePerKMS
            val rateType = firstItem.rateType

            txtPerKm.setText(ratePerKms ?: "")

            if (ratePerKms.isNullOrEmpty()) {
                // Enable field
                txtPerKm.isEnabled = true
                txtPerKm.isFocusableInTouchMode = true
                txtPerKm.isFocusable = true

            } else {
                if (rateType == "2") {
                    // Disable field
                    txtPerKm.isEnabled = false
                    txtPerKm.isFocusable = false
                } else {
                    // Enable field
                    txtPerKm.isEnabled = true
                    txtPerKm.isFocusableInTouchMode = true
                    txtPerKm.isFocusable = true
                }
            }

        } else {
            // If list empty → enable field
            txtPerKm.isEnabled = true
            txtPerKm.isFocusableInTouchMode = true
            txtPerKm.isFocusable = true
        }
    }
    private fun observeCity() {
        carAirApprovalViewModel.getCityUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetCityUiState.Idle -> { }
                is GetCityUiState.Loading -> {
                    showLoader()
                }
                is GetCityUiState.Success -> {
                    hideLoader()
                }
                is GetCityUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetCityUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeCityType() {
        carAirApprovalViewModel.getCityTypeUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetCityTypeUiState.Idle -> { }
                is GetCityTypeUiState.Loading -> {
                    showLoader()
                }
                is GetCityTypeUiState.Success -> {
                    hideLoader()
                }
                is GetCityTypeUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetCityTypeUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeInsertCarApproval() {
        carAirApprovalViewModel.insertCarApprovalUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertCarApprovalUiState.Idle -> { }
                is InsertCarApprovalUiState.Loading -> {
                    showLoader()
                }
                is InsertCarApprovalUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                    parentFragmentManager.popBackStackImmediate()
                }
                is InsertCarApprovalUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertCarApprovalUiState.NetworkError -> {
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