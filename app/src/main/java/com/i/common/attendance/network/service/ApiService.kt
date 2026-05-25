package com.i.common.attendance.network.service

import com.i.common.attendance.network.response.AddTourAdvanceExpenseResponse
import com.i.common.attendance.network.response.ApiResponse
import com.i.common.attendance.network.response.AttendanceRecordResponse
import com.i.common.attendance.network.response.BusinessCenterNameResponse
import com.i.common.attendance.network.response.CheckDealerInOutStatusResponse
import com.i.common.attendance.network.response.CheckPJCEntryResponse
import com.i.common.attendance.network.response.CustomerResponse
import com.i.common.attendance.network.response.DailTourListResponse
import com.i.common.attendance.network.response.DailyTourDealerCategoryResponse
import com.i.common.attendance.network.response.DailyTourDealerNameResponse
import com.i.common.attendance.network.response.DailyTourDistrictResponse
import com.i.common.attendance.network.response.DistrictTourAgendaTrackingResponse
import com.i.common.attendance.network.response.EmployeeDukeResponse
import com.i.common.attendance.network.response.EmployeeResponse
import com.i.common.attendance.network.response.FacetsResponse
import com.i.common.attendance.network.response.FileUploadResponse
import com.i.common.attendance.network.response.ForgotPasswordResponse
import com.i.common.attendance.network.response.GetCarAirApprovalListResponse
import com.i.common.attendance.network.response.GetCitiesResponse
import com.i.common.attendance.network.response.GetCustomerResponse
import com.i.common.attendance.network.response.GetDistrictPjcResponse
import com.i.common.attendance.network.response.GetInOutDetailsResponse
import com.i.common.attendance.network.response.GetLedgerPdfResponse
import com.i.common.attendance.network.response.GetRatePerKMApiResponse
import com.i.common.attendance.network.response.GetRecords
import com.i.common.attendance.network.response.GetStateResponse
import com.i.common.attendance.network.response.GetTeamAttendanceResponse
import com.i.common.attendance.network.response.GetUserRightsResponse
import com.i.common.attendance.network.response.HolidayWeekOffResponse
import com.i.common.attendance.network.response.InsertPjcEventResponse
import com.i.common.attendance.network.response.LedgerPdfDataShowResponse
import com.i.common.attendance.network.response.LoadDropDownListResponse
import com.i.common.attendance.network.response.LocationTrackingResponse
import com.i.common.attendance.network.response.LoginResponse
import com.i.common.attendance.network.response.LogoutResponse
import com.i.common.attendance.network.response.MonthListResponse
import com.i.common.attendance.network.response.NameDropdownResponse
import com.i.common.attendance.network.response.OrderItem
import com.i.common.attendance.network.response.OrderListResponse
import com.i.common.attendance.network.response.PjcDateResponse
import com.i.common.attendance.network.response.PjcEventResponse
import com.i.common.attendance.network.response.PjcPermissionResponse
import com.i.common.attendance.network.response.PjcResponse
import com.i.common.attendance.network.response.PlanForListResponse
import com.i.common.attendance.network.response.ProductResponse
import com.i.common.attendance.network.response.RateResponse
import com.i.common.attendance.network.response.ReasonListParamsResponse
import com.i.common.attendance.network.response.ReasonResponse
import com.i.common.attendance.network.response.SelectPortfolioResponse
import com.i.common.attendance.network.response.ServerTimeResponse
import com.i.common.attendance.network.response.Status
import com.i.common.attendance.network.response.SundayRequestListResponse
import com.i.common.attendance.network.response.TargetOutstandingResponse
import com.i.common.attendance.network.response.TourAdvanceExpenseResponse
import com.i.common.attendance.network.response.TourAgendaTrackingDealerNameResponse
import com.i.common.attendance.network.response.TourAgendaTrackingObjectiveResponse
import com.i.common.attendance.network.response.TourAgendaTrackingRunningTaskDetailsResponse
import com.i.common.attendance.network.response.TourAgendaTrackingServiceCenterResponse
import com.i.common.attendance.network.response.TourAgendaTrackingSubDealerNameResponse
import com.i.common.attendance.network.response.TourExpenseTrackingFacetsResponse
import com.i.common.attendance.network.response.TourVoucherResponse
import com.i.common.attendance.network.response.TravelResponse
import com.i.common.attendance.network.response.TravelingByResponse
import com.i.common.attendance.network.response.UnPlanApiResponse
import com.i.common.attendance.network.response.UploadAttachmentResponse
import com.i.common.attendance.network.response.ValidateSundayResponse
import com.i.common.attendance.network.response.ViewLeaveUnnatiResponse
import com.i.common.attendance.network.response.ViewPortFolioResponse
import com.i.common.attendance.network.response.VoucherNoResponse
import com.i.common.attendance.network.response.WeekOffListResponse
import com.i.common.attendance.utils.URLFactory
import com.i.common.attendance.utils.URLFactory.Url.API_GET_MONTH
import com.i.common.attendance.utils.URLFactory.Url.API_GET_SELECT_PORTFOLIO
import com.i.common.attendance.utils.URLFactory.Url.API_GET_UPDATE_PORTFOLIO
import com.i.common.attendance.utils.URLFactory.Url.API_GET_VIEW_PORTFOLIO
import com.i.common.attendance.utils.URLFactory.Url.API_INSERT_VISIT_ENTRY
import com.i.common.attendance.utils.URLFactory.Url.API_TRAVEL_DELETE_UPLOAD_LN
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    /*========================================================================================*/
    /*================================= Authentication ========================================*/
    @POST(URLFactory.Url.CHECK_USER_API)
    suspend fun checkUser(@Body request: MultipartBody): Response<LoginResponse>

    @POST(URLFactory.Url.API_LOGIN_WITH_FCM_ID)
    suspend fun loginWithFcmId(@Body request: MultipartBody): Response<LoginResponse>

    @POST(URLFactory.Url.API_CHANGE_PASSWORD)
    suspend fun forgotPassword(@Body request: MultipartBody): Response<ForgotPasswordResponse>

    /*=======================================================================================*/
    @GET(URLFactory.Url.API_PLAN_FOR_LIST)
    suspend fun getApiPlanForList(): Response<PlanForListResponse>
    @POST(URLFactory.Url.API_REASON)
    suspend fun getReasonApi(@Body request: MultipartBody): Response<ReasonResponse>
    @POST(URLFactory.Url.API_GET_DISTRICT_PJC)
    suspend fun getDistrictPjcApi(@Body request: MultipartBody): Response<GetDistrictPjcResponse>
    @POST(URLFactory.Url.API_REASON_LIST)
    suspend fun getReasonListParam(@Body request: MultipartBody): Response<ReasonListParamsResponse>
    @POST(URLFactory.Url.API_SQL_QUERY_FOR_DROPDOWN)
    suspend fun getSqlQueryForDropdownParam(@Body request: MultipartBody): Response<LoadDropDownListResponse>
    @POST(URLFactory.Url.API_INSERT_PJC_ENTRY)
    suspend fun insertPJCEntry(@Body request: MultipartBody):  Response<InsertPjcEventResponse>

    /*========================================================================================*/
    /*================================= Dashboard ============================================*/
    @POST(URLFactory.Url.API_INSERT_LAT_LONG)
    suspend fun insertLatLongAPI(@Body request: MultipartBody): Response<LocationTrackingResponse>

    @POST(URLFactory.Url.API_GET_RECORD)
    suspend fun getRecordAPI(@Body request: MultipartBody): Response<GetRecords>

    @POST(URLFactory.Url.API_CHECK_IN_CHECK_OUT_STATUS)
    suspend fun getCheckInCheckOutStatusAPI(@Body request: MultipartBody): Response<AttendanceRecordResponse>

    @POST(URLFactory.Url.API_ATTENDANCE_IN_OUT)
    suspend fun getAttendanceInOutAPI(@Body request: MultipartBody): Response<LocationTrackingResponse>

    @POST(URLFactory.Url.API_TEXT_LISTS)
    suspend fun getTextListAPI(@Body request: MultipartBody): Response<Status>

    @POST(URLFactory.Url.API_LOGOUT_WITH_FCM)
    suspend fun logOutWithFCMIdAPI(@Body request: MultipartBody): Response<LogoutResponse>

    @POST(URLFactory.Url.API_GET_PJC)
    suspend fun getPjcApi(@Body request: MultipartBody): Response<PjcResponse>

    @POST(URLFactory.Url.API_HOLIDAY_WEEK_OFF)
    suspend fun getHolidayWeekOffParam(@Body request: MultipartBody): Response<HolidayWeekOffResponse>

    @POST(URLFactory.Url.API_GET_PJC_EVENT)
    suspend fun getPjcEvent(@Body request: MultipartBody): Response<PjcEventResponse>

    @POST(URLFactory.Url.API_GET_TEAM)
    suspend fun getTeamAPI(@Body request: MultipartBody): Response<GetTeamAttendanceResponse>

    @POST(URLFactory.Url.API_FOLLOW_UP)
    suspend fun getFollowUps(@Body request: MultipartBody): Response<PjcEventResponse>

    /*========================================================================================*/

    @GET(URLFactory.Url.API_GET_TIME)
    suspend fun getServerTime(): Response<ServerTimeResponse>

    /*==========================================================================================*/
    /*============================= Toucher Voucher ==========================================*/
    @POST(URLFactory.Url.API_EMPLOYEE)
    suspend fun getEmployeeParam(@Body request: MultipartBody):  Response<EmployeeResponse>
    @POST(URLFactory.Url.API_TRAVELING_BY)
    suspend fun getTravelingByParam(@Body request: MultipartBody):  Response<TravelingByResponse>
    @POST(URLFactory.Url.API_SAVE_TOUR_VOUCHER_LIST)
    suspend fun tourVoucherList(@Body request: MultipartBody):  Response<TourVoucherResponse>
    @POST(URLFactory.Url.API_SAVE_TOUR_VOUCHER)
    suspend fun insertTourVoucher(@Body request: MultipartBody): Response<UnPlanApiResponse>
    @POST(URLFactory.Url.API_UPDATE_SAVE_TOUR_VOUCHER)
    suspend fun insertTourEditVoucher(@Body request: MultipartBody):  Response<UnPlanApiResponse>
    @POST(URLFactory.Url.API_CHECK_PJC_AND_DAILY_TOUR_DETAIL)
    suspend fun checkPJCAndDTD(@Body request: MultipartBody): Response<CheckPJCEntryResponse>
    @POST(URLFactory.Url.API_GET_DROP_DOWN_DATA)
    suspend fun getCommonDropDownData(@Body request: MultipartBody): Response<ApiResponse>
    @POST(URLFactory.Url.API_GET_DISTRICT_DATA)
    suspend fun getCommonDropDownDistrict(): Response<NameDropdownResponse>
    @POST(URLFactory.Url.API_GET_CITY_DATA)
    suspend fun getCommonDropDownCity(): Response<NameDropdownResponse>
    @POST(URLFactory.Url.API_GET_EMPLOYEE_DATA)
    suspend fun apiGetEmployeeData(): Response<EmployeeResponse>
    @POST(URLFactory.Url.API_INSERT_FIELD_VISIT)
    suspend fun insertFieldVisitData(@Body request: MultipartBody): Response<UnPlanApiResponse>

    /*===========================================================================*/
    @POST(URLFactory.Url.API_BACK_DATED_RIGHTS)
    suspend fun getBackDatedRightAPI(@Body request: MultipartBody): Response<PjcDateResponse>

    @POST(URLFactory.Url.API_WITHOUT_PJC_EXPENSE_RIGHTS)
    suspend fun getWithoutPJCTourRightsAPI(@Body request: MultipartBody): Response<PjcPermissionResponse>

    @POST(URLFactory.Url.API_GET_TOUR_DETAILS)
    suspend fun getTourDetailsAPI(@Body request: MultipartBody): Response<TourVoucherResponse>

    @POST(URLFactory.Url.API_FILE_ATTACHMENT)
    suspend fun getAttachmentFileParam(@Body request: MultipartBody): Response<UploadAttachmentResponse>

    @POST(API_TRAVEL_DELETE_UPLOAD_LN)
    suspend fun deleteTravelAttachment(@Body request: MultipartBody): Response<FileUploadResponse>

    /*==========================================================================================*/
    /*============================= Daily Tour ==========================================*/
    @POST(URLFactory.Url.API_TOUR_DETAILS_LIST)
    suspend fun getDailyDetailsListAPI(@Body request: MultipartBody): Response<DailTourListResponse>
    @POST(URLFactory.Url.API_GET_CATEGORY)
    suspend fun getDealerCategoryAPI(@Body request: MultipartBody): Response<DailyTourDealerCategoryResponse>
    @POST(URLFactory.Url.API_GET_DEALER_NAME)
    suspend fun getDealerNameAPI(@Body request: MultipartBody): Response<DailyTourDealerNameResponse>
    @POST(URLFactory.Url.API_DISTRICT)
    suspend fun getDistrictAPI(@Body request: MultipartBody): Response<DailyTourDistrictResponse>
    @POST(URLFactory.Url.API_INSERT_TOUR_DETAILS)
    suspend fun insertDailyDetailsAPI(@Body request: MultipartBody): Response<FileUploadResponse>
    /*==========================================================================================*/
    /*============================= Daily Tour ==========================================*/
    @POST(URLFactory.Url.API_TOUR_VOUCHER_APPROVE_LIST)
    suspend fun getTourVoucherApprovalListAPI(@Body request: MultipartBody): Response<TourVoucherResponse>
    @POST(URLFactory.Url.API_EXPENSE_APPROVE)
    suspend fun updateExpenseStatus(@Body request: MultipartBody): Response<FileUploadResponse>
    /*==========================================================================================*/
    /*============================== New customer/ dealer ======================================*/
    @POST(API_GET_SELECT_PORTFOLIO)
    suspend fun getSelectPortFolio(@Body body: MultipartBody): Response<SelectPortfolioResponse>
    @POST(API_INSERT_VISIT_ENTRY)
    suspend fun insertVisit(@Body body: MultipartBody): Response<FileUploadResponse>

    @POST(API_GET_UPDATE_PORTFOLIO)
    suspend fun updatePortFolioAPI(@Body body: MultipartBody): Response<FileUploadResponse>

    /*==========================================================================================*/
    /*============================== Stock Details ======================================*/
    @POST(API_GET_VIEW_PORTFOLIO)
    suspend fun getViewPortFolio(@Body body: MultipartBody): Response<ViewPortFolioResponse>
    /*==========================================================================================*/
    /*============================== Attendance Report ======================================*/
    @POST(API_GET_MONTH)
    suspend fun getMonthAPI(): Response<MonthListResponse>
    /*==========================================================================================*/
    /*============================= Web View ==========================================*/
    @POST(URLFactory.Url.API_GET_REPORT)
    suspend fun getFacet(@Body request: MultipartBody): Response<FacetsResponse>

    /*==========================================================================================*/
    /*=================================== DUKE =================================================*/
    /* ================= Tour Agenda Tracking ================= */
    @POST(URLFactory.Url.API_STATE)
    suspend fun getState(@Body request: MultipartBody): Response<GetStateResponse>
    @POST(URLFactory.Url.API_DISTRICT)
    suspend fun getTourAgendaTrackingDistrictAPI(@Body request: MultipartBody): Response<DistrictTourAgendaTrackingResponse>
    @POST(URLFactory.Url.API_GET_STATION)
    suspend fun getStation(@Body request: MultipartBody): Response<BusinessCenterNameResponse>
    @POST(URLFactory.Url.API_GET_DEALERS)
    suspend fun getDealerName(@Body request: MultipartBody): Response<TourAgendaTrackingDealerNameResponse>
    @POST(URLFactory.Url.API_GET_SUBDEALER_DETAILS)
    suspend fun getSubDealer(@Body request: MultipartBody): Response<TourAgendaTrackingSubDealerNameResponse>
    @POST(URLFactory.Url.API_GET_SERVICE_CENTER)
    suspend fun getServiceCenters(@Body request: MultipartBody): Response<TourAgendaTrackingServiceCenterResponse>
    @POST(URLFactory.Url.API_GET_RUNNING_DETAILS)
    suspend fun getRunningTaskDetails(@Body request: MultipartBody): Response<TourAgendaTrackingRunningTaskDetailsResponse>
    @POST(URLFactory.Url.GET_FACETS)
    suspend fun getFacets(@Body request: MultipartBody): Response<TourExpenseTrackingFacetsResponse>
    @POST(URLFactory.Url.API_GET_OBJECTIVE)
    suspend fun getObjective(@Body request: MultipartBody): Response<TourAgendaTrackingObjectiveResponse>
    @POST(URLFactory.Url.API_START_END_MEETING)
    suspend fun startEndMeeting(@Body request: MultipartBody): Response<FileUploadResponse>
    @POST(URLFactory.Url.API_INSERT_WEEKLY_DETAILS)
    suspend fun insertTourTrackingDetails(@Body request: MultipartBody): Response<FileUploadResponse>
    @POST(URLFactory.Url.API_GET_IN_OUT_DETAILS)
    suspend fun getInOutDetails(@Body request: MultipartBody): Response<GetInOutDetailsResponse>
    @POST(URLFactory.Url.API_INSERT_OBJECTIVE)
    suspend fun insertObjective(@Body request: MultipartBody): Response<FileUploadResponse>
    @POST(URLFactory.Url.API_GET_USER_CHECK_IN_OUT_RIGHTS)
    suspend fun getUserRights(@Body request: MultipartBody): Response<GetUserRightsResponse>
    @POST(URLFactory.Url.API_INSERT_JTD_DETAILS)
    suspend fun insertJtdDetails(@Body request: MultipartBody): Response<FileUploadResponse>


    /*================================== DUKE ==========================================*/
    /*============================= Week OFF Day ======================================*/
    @POST(URLFactory.Url.VALIDATE_SUNDAY)
    suspend fun validateSunday(@Body request: MultipartBody): Response<ValidateSundayResponse>
    @POST(URLFactory.Url.WEEK_OF_DATE)
    suspend fun submitWeekOff(@Body request: MultipartBody): Response<FileUploadResponse>
    @POST(URLFactory.Url.API_GET_SUNDAY_REQUEST_LIST)
    suspend fun getSundayRequestList(@Body request: MultipartBody): Response<WeekOffListResponse>
    @POST(URLFactory.Url.SUNDAY_LIST)
    suspend fun getApprovalListSundayRequest(): Response<SundayRequestListResponse>

    @POST(URLFactory.Url.WEEK_OF_DATE)
    suspend fun submitSundayApproval(@Body body: MultipartBody): Response<FileUploadResponse>

    /*================================== DUKE ==========================================*/
    /*============================= Car / AIR Approval ======================================*/
    @POST(URLFactory.Url.API_GET_EMP_DATA)
    suspend fun getEmpData(@Body body: MultipartBody): Response<EmployeeDukeResponse>
    @POST(URLFactory.Url.API_GET_NEW_VOUCHER_NO)
    suspend fun getNewVoucherNo(@Body body: MultipartBody): Response<VoucherNoResponse>
    @POST(URLFactory.Url.API_TRAVEL_BY_CAR)
    suspend fun getTravellingByCar(@Body body: MultipartBody): Response<TravelResponse>
    @POST(URLFactory.Url.API_GET_RATE_FOR_PER_KM_CAR_AIR_APPROVAL)
    suspend fun getRateForPerKmCarAirApproval(@Body body: MultipartBody): Response<GetRatePerKMApiResponse>
    @POST(URLFactory.Url.API_GET_CITY_TYPE)
    suspend fun getCityType(@Body body: MultipartBody): Response<TravelResponse>
    @POST(URLFactory.Url.API_GET_CITIES)
    suspend fun getCity(): Response<GetCitiesResponse>
    @POST(URLFactory.Url.API_INSERT_CAR_APPROVAL)
    suspend fun insertCarApproval(@Body body: MultipartBody): Response<FileUploadResponse>
    @POST(URLFactory.Url.API_CAR_AIR_APPROVAL_LIST)
    suspend fun getCarAirApprovalList(): Response<GetCarAirApprovalListResponse>
    @POST(URLFactory.Url.API_UPDATE_CAR_AIR_APPROVAL_STATUS)
    suspend fun updateCarAirApprovalStatus(@Body body: MultipartBody): Response<FileUploadResponse>

    /*================================== Flotech && Singla ==========================================*/
    /*============================= Daily tour details ======================================*/
    @POST(URLFactory.Url.API_INSERT_TOUR_DETAILS)
    suspend fun insertDailyTourDetailsFlotech(@Body body: MultipartBody): Response<FileUploadResponse>

    @POST(URLFactory.Url.API_GET_ORDER)
    suspend fun getOrderList(@Body body: MultipartBody): Response<OrderListResponse>

    @POST(URLFactory.Url.API_CUSTOMER_VIEWER)
    suspend fun getCustomerViewerParam(@Body body: MultipartBody): Response<CustomerResponse>

    @POST(URLFactory.Url.API_PRODUCT)
    suspend fun getProductParam(@Body body: MultipartBody): Response<ProductResponse>

    @POST(URLFactory.Url.API_GET_RATE)
    suspend fun getRate(@Body body: MultipartBody): Response<RateResponse>

    @POST(URLFactory.Url.API_INSERT_ORDER)
    suspend fun insertOrderEntry(@Body body: MultipartBody): Response<FileUploadResponse>

    //Tour Advance Expense
    @POST(URLFactory.Url.API_INSERT_TOUR_ADVANCE_EXPENSE)
    suspend fun insertTourAdvanceExpense(@Body body: MultipartBody): Response<AddTourAdvanceExpenseResponse>
    @POST(URLFactory.Url.API_VIEW_TOUR_ADVANCE_EXPENSE_LIST)
    suspend fun getTourAdvanceExpenseList(@Body body: MultipartBody): Response<TourAdvanceExpenseResponse>
    @POST(URLFactory.Url.API_UPDATE_TOUR_ADVANCE_EXPENSE)
    suspend fun updateTourAdvanceExpense(@Body body: MultipartBody): Response<AddTourAdvanceExpenseResponse>

    /*===========================================================================================*/
    /*=================================== Mascot Account  =======================================*/

    @POST(URLFactory.Url.API_CUSTOMER)
    suspend fun getCustomer(@Body body: MultipartBody): Response<GetCustomerResponse>

    @POST(URLFactory.Url.API_GET_LEDGER_REPORT)
    suspend fun getLedgerReport(@Body body: MultipartBody): Response<GetLedgerPdfResponse>

    @POST(URLFactory.Url.API_GET_LEDGER_REPORT)
    suspend fun ledgerReportShowPdf(@Body body: MultipartBody): Response<LedgerPdfDataShowResponse>

    /*========================================================================================*/
    /*================================= Unnati New Requirement ============================================*/
    @POST(URLFactory.Url.API_INSERT_PROMOTIONAL_ACTIVITY)
    suspend fun insertPromotionalActivity(@Body request: MultipartBody): Response<FileUploadResponse>
    @POST(URLFactory.Url.API_GET_DISTRICTS)
    suspend fun getDistricts(): Response<DailyTourDistrictResponse>
    @POST(URLFactory.Url.API_GET_TARGET_OUTSTANDING)
    suspend fun getTargetOutstanding(@Body request: MultipartBody): Response<TargetOutstandingResponse>

    //CheckDealerInOutStatusRequest
    @POST(URLFactory.Url.API_DEALER_CHECK_IN_OUT)
    suspend fun insertDealerCheckIn(@Body request: MultipartBody): Response<FileUploadResponse>

    @POST(URLFactory.Url.API_CHECK_DEALER_IN_OUT_STATUS)
    suspend fun checkDealerInOutStatus(@Body request: MultipartBody): Response<CheckDealerInOutStatusResponse>

    @POST(URLFactory.Url.API_INSERT_LEAVE_REQUEST)
    suspend fun insertLeaveRequestUnnati(@Body request: MultipartBody): Response<FileUploadResponse>

    @POST(URLFactory.Url.API_VIEW_LEAVE_LIST)
    suspend fun viewLeaveListUnnati(@Body request: MultipartBody): Response<ViewLeaveUnnatiResponse>

    @POST(URLFactory.Url.API_VIEW_LEAVE_APPROVAL)
    suspend fun viewLeaveListApprovalUnnati(@Body request: MultipartBody): Response<ViewLeaveUnnatiResponse>

    @POST(URLFactory.Url.API_VIEW_LEAVE_APPROVAL_UPDATE)
    suspend fun viewLeaveListApprovalUpdateUnnati(@Body request: MultipartBody): Response<FileUploadResponse>


}