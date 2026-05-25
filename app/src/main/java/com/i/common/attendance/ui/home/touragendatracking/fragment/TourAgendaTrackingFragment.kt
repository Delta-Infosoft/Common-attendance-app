package com.i.common.attendance.ui.home.touragendatracking.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentTourAgendaTrackingBinding
import com.i.common.attendance.network.request.BusinessCenterNameRequest
import com.i.common.attendance.network.request.DailyTourDealerCategoryRequest
import com.i.common.attendance.network.request.GetStateRequest
import com.i.common.attendance.network.request.TourAgendaDealerNameRequest
import com.i.common.attendance.network.request.TourAgendaTrackingDistrictRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetFactRequest
import com.i.common.attendance.network.request.TourAgendaTrackingGetRunningTaskDetailsRequest
import com.i.common.attendance.network.request.TourAgendaTrackingServiceCenterRequest
import com.i.common.attendance.network.request.TourAgendaTrackingSubDealerNameRequest
import com.i.common.attendance.network.response.TourAgendaTrackingFacets
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.ui.home.dailytour.fragment.SelectDailyTourDealerCategoryBottomSheetFragment
import com.i.common.attendance.ui.home.dailytour.viewmodel.DealerCategoryState
import com.i.common.attendance.ui.home.pjc.fragment.PjcInsertPlanFragment
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.FacetType
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetDealerNameUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetDistrictUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetFacetsUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetInOutDetailsUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetObjectiveUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetRunningTaskDetailsUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetServiceCenterUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetStateUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetStationUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetSubDealerNameUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.GetUserRightsUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.InsertJtdDetailsUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.InsertObjectiveUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.InsertTourTrackingUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.MeetingType
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.StartEndMeetingUiState
import com.i.common.attendance.ui.home.touragendatracking.viewmodel.TourAgendaTrackingViewModel
import com.i.common.attendance.ui.home.tourvoucher.fragment.SelectCommonDialogBottomSheetFragment
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.CheckPJCEntryUiState
import com.i.common.attendance.ui.home.tourvoucher.viewmodel.TourVoucherViewModel
import com.i.common.attendance.utils.Constants.getTrimmedText
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TourAgendaTrackingFragment : BaseFragment() {

    private lateinit var binding : FragmentTourAgendaTrackingBinding
    @Inject lateinit var sharedPreference: EncryptedPrefHelper
    private val tourAgendaViewModel: TourAgendaTrackingViewModel by viewModels()
    private val tourVoucherViewmodel: TourVoucherViewModel by viewModels()
    private var stateId = ""
    private var district = ""
    private var busignessCenterId = ""
    private var busignessCenterName = ""
    private var dealerId = ""

    /*tourAgendaViewModel.startEndMeeting(
    request = TourAgendaTrackingStartEndMeetingRequest(*//*params*//*),
    meetingType = MeetingType.START
    )

    tourAgendaViewModel.startEndMeeting(
    request = TourAgendaTrackingStartEndMeetingRequest(*//*params*//*),
    meetingType = MeetingType.END
    )*/

    /*val request = CheckPJCEntryRequest(
        mobileNo = shredPref.getUser()?.MobileNo ?: "",
        date = txtDate.getTrimmedText(),
        type = "CHECK_PJC"
    )
    tourVoucherViewmodel.checkPJCEntry(request)*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = sharedPreference.getUser()
        Log.e("user",user.toString())
        tourAgendaViewModel.loadStateList(request = GetStateRequest(user?.MobileNo?:""))
        tourAgendaViewModel.loadFacets(request = TourAgendaTrackingGetFactRequest(parameter = "MENU_DealerEntry"))
        tourAgendaViewModel.loadFacets(request = TourAgendaTrackingGetFactRequest(parameter = "ActivityMenuWithAddList"))
        tourAgendaViewModel.loadFacets(request = TourAgendaTrackingGetFactRequest(parameter = "Menu_TeamAttendance"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTourAgendaTrackingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        moveOnClickListeners()
        observeStateData()
        observeDistrictData()
        observeBusignessCenterNameData()
        observeDealerCategory()
        observeDealerNameData()
        observeSubDealerNameData()
        observeServiceCenterData()
        observeRunningTaskDetailsData()
        observeFacetsData()
        observeObjectiveData()
        observeStartEndMeeting()
        observeInsertTourTracking()
        observeInOutDetails()
        observeInsertObjective()
        observePJCEntryData()
        observeUserRights()
        observeInsertJtdDetails()
    }
    private fun moveOnClickListeners() = with(binding){
        txtState.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedStateList
            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectStateBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtState.setText(selected.State)
                stateId = selected.StateTextListId?:""
                val user = sharedPreference.getUser()
                tourAgendaViewModel.refreshDistrictList(request = TourAgendaTrackingDistrictRequest(empId = user?.EmpID?:"", stateName = selected.State?:""))


                txtDistrict.setText("")
                txtBusinessCenter.setText("")
                txtDealerCategory.setText("")
                txtDealerName.setText("")
                txtSubDealerName.setText("")
                txtServiceCenter.setText("")
                district = ""
                busignessCenterId = ""
                busignessCenterName = ""

                txtLayDealerName.visibility = View.GONE
                txtLaySubDealerName.visibility = View.GONE
                txtLayServiceCenter.visibility = View.GONE
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtDistrict.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedDistrictList
            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }
            if(txtState.getTrimmedText().isEmpty()){
                showToast(getString(R.string.validation_please_select_state_first))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectDistrictBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtDistrict.setText(selected.District)
                district = selected.District ?:""
                val user = sharedPreference.getUser()
                tourAgendaViewModel.refreshStationList(request = BusinessCenterNameRequest(user?.EmpID?:"",stateId,selected.District?:""))

                txtBusinessCenter.setText("")
                txtDealerCategory.setText("")
                txtDealerName.setText("")
                txtSubDealerName.setText("")
                txtServiceCenter.setText("")
                busignessCenterId = ""
                busignessCenterName = ""
                txtLayDealerName.visibility = View.GONE
                txtLaySubDealerName.visibility = View.GONE
                txtLayServiceCenter.visibility = View.GONE
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtBusinessCenter.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedStationList
            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }
            if(txtState.getTrimmedText().isEmpty()){
                showToast(getString(R.string.validation_please_select_state_first))
                return@setSafeOnClickListener
            }
            if(txtDistrict.getTrimmedText().isEmpty()){
                showToast(getString(R.string.validation_please_select_district))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectBusignessCenterNameBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtBusinessCenter.setText(selected.Name)
                busignessCenterId = selected.BusiCntrId ?:""
                busignessCenterName = selected.Name ?:""
                tourAgendaViewModel.refreshDealerCategory(DailyTourDealerCategoryRequest(type = "Weekly", deptId = ""))

                txtDealerCategory.setText("")
                txtDealerName.setText("")
                txtSubDealerName.setText("")
                txtServiceCenter.setText("")
                txtLayDealerName.visibility = View.GONE
                txtLaySubDealerName.visibility = View.GONE
                txtLayServiceCenter.visibility = View.GONE
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtDealerCategory.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedDealerCategoryList
            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_data_not_ready_yet))
                return@setSafeOnClickListener
            }
            if(txtState.getTrimmedText().isEmpty()){
                showToast(getString(R.string.validation_please_select_state_first))
                return@setSafeOnClickListener
            }
            if(txtDistrict.getTrimmedText().isEmpty()){
                showToast(getString(R.string.validation_please_select_district))
                return@setSafeOnClickListener
            }
            if(txtBusinessCenter.getTrimmedText().isEmpty()){
                showToast(getString(R.string.validation_please_select_busigness_center_name))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectDailyTourDealerCategoryBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtDealerCategory.setText(selected.Text)
                txtLayDealerName.visibility = View.VISIBLE
                if (selected.Text.equals("Dealer") || selected.Text.equals("Distributor")){
                    val user = sharedPreference.getUser()
                    tourAgendaViewModel.refreshDealerNameList(
                        TourAgendaDealerNameRequest(
                            empId = user?.EmpID ?: "",
                            dealerType = selected.Text ?: "",
                            busignessCenterId = busignessCenterId,
                            dealerName = txtDealerName.getTrimmedText()
                        )
                    )
                    txtDealerName.apply {
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isCursorVisible = false
                        isClickable = true
                    }

                    txtLayDealerName.apply {
                        endIconMode = TextInputLayout.END_ICON_CUSTOM
                        endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)
                        setEndIconTintList(ContextCompat.getColorStateList(context, R.color.primary))
                    }
                } else if(selected.Text.equals("Sub Dealer")) {
                    val user = sharedPreference.getUser()

                    tourAgendaViewModel.refreshSubDealerNameList(
                        TourAgendaTrackingSubDealerNameRequest(
                            empId = user?.EmpID ?: "",
                            busignessCenterId = busignessCenterId,
                        )
                    )
                    txtLaySubDealerName.visibility = View.VISIBLE

                } else if(selected.Text.equals("Service Center")) {

                    tourAgendaViewModel.refreshServiceCenterList(
                        TourAgendaTrackingServiceCenterRequest(
                           stateName = txtState.getTrimmedText()
                        )
                    )
                    txtLayServiceCenter.visibility = View.VISIBLE

                } else {
                    val user = sharedPreference.getUser()
                    tourAgendaViewModel.loadRunningTaskDetails(
                        request =
                            TourAgendaTrackingGetRunningTaskDetailsRequest(
                                empId = user?.EmpID ?: "",
                                dealerId = dealerId,
                                businessCenterId = busignessCenterId,
                                stateId = stateId,
                                districtId = district,
                                dealerCategoryId = ""
                            )
                    )

                    txtDealerName.apply {
                        setText("")
                        isFocusable = true
                        isFocusableInTouchMode = true
                        isCursorVisible = true
                        isClickable = true
                    }

                    txtLayDealerName.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtDealerName.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedDealerNameList
            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_please_select_other_dealer_category))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectDealerNameBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtDealerName.setText(selected.Name)
                dealerId = selected.DealerId ?:""
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtSubDealerName.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedSubDealerNameList

            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validation_no_data_found_for_sub_dealer))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectSubDealerNameBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtSubDealerName.setText(selected.Name)
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }
        txtServiceCenter.setSafeOnClickListener {
            val activityList = tourAgendaViewModel.cachedServiceCenterList

            if (activityList.isNullOrEmpty()) {
                showToast(getString(R.string.validaiton_no_data_found_for_service_center))
                return@setSafeOnClickListener
            }
            val bottomSheet = SelectServiceCenterBottomSheetFragment.Companion.newInstance(activityList)
            bottomSheet.setDismissCallback { selected ->
                txtServiceCenter.setText(selected.Name)
            }
            bottomSheet.show(childFragmentManager, "ActivityPlan")
        }

    }
    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_tour_agenda_tracking))
            manageBackButtonClick(true)
            manageDrawerLock(true)
            setDrawerEnabled(true)
            manageInfo(true)
            updateDrawerHeader(
                userName = sharedPreference.getUser()?.UsersName.toString(),
                email = "test@gmail.com"
            )
        }
    }
    private fun showLoader() {
        requireActivity().window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarHome.visibility = View.VISIBLE
    }
    private fun hideLoader() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarHome.visibility = View.GONE
    }
    private fun observeStateData() {
        tourAgendaViewModel.getStateUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetStateUiState.Idle -> {
                    // Do nothing
                }

                is GetStateUiState.Loading -> {
                    showLoader()
                }

                is GetStateUiState.Success -> {
                    hideLoader()
                }

                is GetStateUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is GetStateUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeDistrictData() {
        tourAgendaViewModel.getDistrictUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetDistrictUiState.Idle -> { }
                is GetDistrictUiState.Loading -> {
                    showLoader()
                }
                is GetDistrictUiState.Success -> {
                    hideLoader()
                    //setDistrictAdapter(state.list)
                }
                is GetDistrictUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetDistrictUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeBusignessCenterNameData() {
        tourAgendaViewModel.getStationUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetStationUiState.Idle -> { }
                is GetStationUiState.Loading -> {
                    showLoader()
                }
                is GetStationUiState.Success -> {
                    hideLoader()
                }
                is GetStationUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetStationUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeDealerCategory() {
        tourAgendaViewModel.dealerCategoryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DealerCategoryState.Loading -> {
                    showLoader()
                }
                is DealerCategoryState.Success -> {
                    hideLoader()
                }
                is DealerCategoryState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is DealerCategoryState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> {}
            }
        }
    }
    private fun observeDealerNameData() {
        tourAgendaViewModel.getDealerNameUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetDealerNameUiState.Idle -> { }
                is GetDealerNameUiState.Loading -> {
                    showLoader()
                }
                is GetDealerNameUiState.Success -> {
                    hideLoader()
                }
                is GetDealerNameUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetDealerNameUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeSubDealerNameData() {
        tourAgendaViewModel.getSubDealerNameUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetSubDealerNameUiState.Idle -> { }
                is GetSubDealerNameUiState.Loading -> {
                    showLoader()
                }
                is GetSubDealerNameUiState.Success -> {
                    hideLoader()
                }
                is GetSubDealerNameUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetSubDealerNameUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeServiceCenterData() {
        tourAgendaViewModel.getServiceCenterUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetServiceCenterUiState.Idle -> { }
                is GetServiceCenterUiState.Loading -> {
                    showLoader()
                }
                is GetServiceCenterUiState.Success -> {
                    hideLoader()
                }
                is GetServiceCenterUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetServiceCenterUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeRunningTaskDetailsData() {
        tourAgendaViewModel.getRunningTaskDetailsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetRunningTaskDetailsUiState.Idle -> { }
                is GetRunningTaskDetailsUiState.Loading -> {
                    showLoader()
                }
                is GetRunningTaskDetailsUiState.Success -> {
                    hideLoader()
                }
                is GetRunningTaskDetailsUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetRunningTaskDetailsUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeFacetsData() {
        tourAgendaViewModel.getFacetsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetFacetsUiState.Idle -> { }

                is GetFacetsUiState.Loading -> {
                    when (state.type) {
                        FacetType.DEALER_ENTRY  -> showLoader()
                        FacetType.ACTIVITY_MENU -> showLoader()
                        FacetType.TEAM_ATTENDACE -> showLoader()
                    }
                }

                is GetFacetsUiState.Success -> {
                    hideLoader()
                    when (state.type) {
                        FacetType.DEALER_ENTRY  -> handleDealerEntryFacets(state.list)
                        FacetType.ACTIVITY_MENU -> handleActivityMenuFacets(state.list)
                        FacetType.TEAM_ATTENDACE -> handleTeamAttendanceMenuFacets(state.list)
                    }
                }

                is GetFacetsUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                    when (state.type) {
                        FacetType.DEALER_ENTRY  -> (activity as HomeActivity).isNewDealerSubDealerVisible(false)
                        FacetType.ACTIVITY_MENU -> (activity as HomeActivity).isActivityListActivityVisible(false)
                        FacetType.TEAM_ATTENDACE -> (activity as HomeActivity).isTeamAttendanceVisible(false)
                    }
                }

                is GetFacetsUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                    when (state.type) {
                        FacetType.DEALER_ENTRY  -> (activity as HomeActivity).isNewDealerSubDealerVisible(false)
                        FacetType.ACTIVITY_MENU -> (activity as HomeActivity).isActivityListActivityVisible(false)
                        FacetType.TEAM_ATTENDACE -> (activity as HomeActivity).isTeamAttendanceVisible(false)
                    }
                }
            }
        }
    }
    private fun handleDealerEntryFacets(list: List<TourAgendaTrackingFacets>) {
        val isVisible = list.firstOrNull()?.Reqd.equals("True", ignoreCase = true)
        (activity as HomeActivity).isNewDealerSubDealerVisible(isVisible)
    }
    private fun handleActivityMenuFacets(list: List<TourAgendaTrackingFacets>) {
        val isVisible = list.firstOrNull()?.Reqd.equals("True", ignoreCase = true)
        (activity as HomeActivity).isActivityListActivityVisible(isVisible)
    }
    private fun handleTeamAttendanceMenuFacets(list: List<TourAgendaTrackingFacets>) {
        val isVisible = list.firstOrNull()?.Reqd.equals("True", ignoreCase = true)
        (activity as HomeActivity).isTeamAttendanceVisible(isVisible)
    }
    private fun observeObjectiveData() {
        tourAgendaViewModel.getObjectiveUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetObjectiveUiState.Idle -> { }
                is GetObjectiveUiState.Loading -> {
                    showLoader()
                }
                is GetObjectiveUiState.Success -> {
                    hideLoader()
                }
                is GetObjectiveUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetObjectiveUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeStartEndMeeting() {
        tourAgendaViewModel.startEndMeetingUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is StartEndMeetingUiState.Idle -> { }

                is StartEndMeetingUiState.Loading -> {
                    when (state.type) {
                        MeetingType.START -> showLoader()
                        MeetingType.END   -> showLoader()
                    }
                }

                is StartEndMeetingUiState.Success -> {
                    hideLoader()
                    when (state.type) {
                        MeetingType.START -> {
                           showToast(state.message)
                        }
                        MeetingType.END -> {
                           showToast(state.message)
                        }
                    }
                }

                is StartEndMeetingUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }

                is StartEndMeetingUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeInsertTourTracking() {
        tourAgendaViewModel.insertTourTrackingUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertTourTrackingUiState.Idle -> { }
                is InsertTourTrackingUiState.Loading -> {
                    showLoader()
                }
                is InsertTourTrackingUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertTourTrackingUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertTourTrackingUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeInOutDetails() {
        tourAgendaViewModel.getInOutDetailsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetInOutDetailsUiState.Idle -> { }
                is GetInOutDetailsUiState.Loading -> {
                    showLoader()
                }
                is GetInOutDetailsUiState.Success -> {
                    hideLoader()
                }
                is GetInOutDetailsUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetInOutDetailsUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeInsertObjective() {
        tourAgendaViewModel.insertObjectiveUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertObjectiveUiState.Idle -> { }
                is InsertObjectiveUiState.Loading -> {
                    showLoader()
                }
                is InsertObjectiveUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertObjectiveUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertObjectiveUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observePJCEntryData(){
        tourVoucherViewmodel.checkPJCEntryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CheckPJCEntryUiState.Loading -> {
                    showLoader()
                }

                is CheckPJCEntryUiState.Allowed -> {
                    hideLoader()
                }

                is CheckPJCEntryUiState.NotAllowed -> {
                    hideLoader()
                    val bottomSheet = SelectCommonDialogBottomSheetFragment.Companion.newInstance(
                        errorText = "You had not filed PJC details of selected date. So please first add your PJC details of selected date.",
                        pageText = "Would you like to open PJC entry form?"
                    )
                    bottomSheet.setDismissCallback { selected ->
                        tourVoucherViewmodel.resetCheckPJCState()
                        when (selected) {
                            "Yes" -> {
                                val pjcInsertPlanFragment = PjcInsertPlanFragment()
                                val bundle = Bundle()
                                val todayDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
                                bundle.putString("selected_date", todayDate)
                                pjcInsertPlanFragment.arguments = bundle
                                loadFragment(fragment = pjcInsertPlanFragment, isAdd = false, isAddBackStack = true)
                            }
                            "No" -> {
                            }
                        }
                    }
                    bottomSheet.show(childFragmentManager, "SelectCommon")
                }

                is CheckPJCEntryUiState.Error -> {
                    hideLoader()
                    showToast("Something went wrong")
                }

                else -> Unit
            }
        }
    }
    private fun observeUserRights() {
        tourAgendaViewModel.getUserRightsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetUserRightsUiState.Idle -> { }
                is GetUserRightsUiState.Loading -> {
                    showLoader()
                }
                is GetUserRightsUiState.Success -> {
                    hideLoader()
                }
                is GetUserRightsUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is GetUserRightsUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }
    private fun observeInsertJtdDetails() {
        tourAgendaViewModel.insertJtdDetailsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is InsertJtdDetailsUiState.Idle -> { }
                is InsertJtdDetailsUiState.Loading -> {
                    showLoader()
                }
                is InsertJtdDetailsUiState.Success -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertJtdDetailsUiState.ApiError -> {
                    hideLoader()
                    showToast(state.message)
                }
                is InsertJtdDetailsUiState.NetworkError -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }

}