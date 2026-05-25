package com.i.common.attendance.utils

object URLFactory {

    object Url {
        /*========================================================================================*/
        /* ================= BASE URL ================= */
        const val baseUrl = "http://103.190.6.152/DeltaAttendanceAPI/" /*THis is dynamic save from login page*/
        const val baseUrlDuke = "http://ierp.dukeplasto.com/DeltaViewer/API/"
        const val baseUrlFlotech = "http://Flotech.deltaierp.com:91/"

        const val BASE_URL_UNNATI_LOCAL_HOST = "http://localhost:13605/"

        const val BASE_URL_MASCOT_DELTA_ACCOUNT: String = "https://mascot.nishainfotech.com:90/DeltaiAccount/API/" //Live

        /*========================================================================================*/
        /* ================= AUTH ================= */
        const val CHECK_USER_API = "API_UserValid.aspx"
        //const val API_LOGIN_WITH_FCM_ID = "DeltaAttendanceAPIWIPL/API_LoginWithFCMId.aspx";
        const val API_LOGIN_WITH_FCM_ID = "API_LoginWithFCMId.aspx";
        const val API_CHANGE_PASSWORD: String = "API_ChangePassword.aspx"
        const val API_LOGOUT_WITH_FCM: String = "API_LogoutWithFCMId.aspx"
        /*========================================================================================*/
        /* ================= DashBoard ================= */
        const val API_GET_RECORD = "API_GetLastAttendances.aspx"
        const val API_CHECK_IN_CHECK_OUT_STATUS: String = "API_CheckInOutStatus.aspx"
        const val API_INSERT_LAT_LONG: String = "API_InsertLatLong.aspx"
        const val API_ATTENDANCE_IN_OUT: String = "API_AttendanceInOut.aspx"
        const val API_TEXT_LISTS: String = "API_TextLists.aspx"
        /*========================================================================================*/
        /* ================= PJC Cal ================= */
        const val API_GET_PJC: String = "API_GetPJC.aspx"
        const val API_HOLIDAY_WEEK_OFF: String = "API_HighlightWOFFHoliday.aspx"
        const val API_GET_PJC_EVENT: String = "API_GetPJCEvent.aspx"

        const val API_PLAN_FOR_LIST: String = "API_PlanForList.aspx"
        const val API_REASON: String = "API_GetReasonList.aspx"
        const val API_GET_DISTRICT_PJC: String = "API_GetDistrictForPJC.aspx"

        const val API_REASON_LIST: String = "API_GetReasonParamterList.aspx"

        const val API_SQL_QUERY_FOR_DROPDOWN: String = "API_LoadDropdownList.aspx"

        const val API_INSERT_PJC_ENTRY: String = "API_InsertPJC.aspx"

        /*========================================================================================*/
        /* ================= Staff Attendance ================= */
        const val API_GET_TEAM: String = "API_GetTeam.aspx"

        /*------------------------------------------------------------*/
        const val API_GET_TIME: String = "servertime.aspx"

        /*========================================================================================*/
        /* ================= Tour Voucher ================= */
        const val API_EMPLOYEE: String = "API_GetTeam.aspx"
        const val API_TRAVELING_BY: String = "API_TextLists.aspx"
        const val API_SAVE_TOUR_VOUCHER_LIST: String = "API_TourExpenseView.aspx"
        const val API_SAVE_TOUR_VOUCHER: String = "API_InsertTourExpense.aspx"


        const val API_UPDATE_SAVE_TOUR_VOUCHER: String = "API_UpdateTourExpense.aspx"

        const val API_CHECK_PJC_AND_DAILY_TOUR_DETAIL: String = "API_CheckEntryValidation.aspx"
        const val API_GET_DROP_DOWN_DATA: String = "API_LoadAllDropDown.aspx"
        const val API_GET_DISTRICT_DATA: String = "API_LoadDistDropDown.aspx"
        const val API_GET_CITY_DATA: String =   "API_LoadCityDropdown.aspx"
        const val API_INSERT_FIELD_VISIT: String = "API_InsertFieldVisit.aspx"

        const val API_GET_EMPLOYEE_DATA: String = "API_LoadEmpNameDropdown.aspx"

        const val API_BACK_DATED_RIGHTS: String = "API_GetBackDatedRights.aspx"

        const val API_WITHOUT_PJC_EXPENSE_RIGHTS: String = "API_GetAllowTourWithoutPJC.aspx"

        const val API_GET_TOUR_DETAILS: String = "API_SelectTourExpense.aspx"

        const val API_FILE_ATTACHMENT: String = "API_GetUploadedDocList.aspx"

        const val API_TRAVEL_DELETE_UPLOAD_LN: String = "API_DeleteUploadedDocument.aspx"
        /*------------------------------------------------------------------------------------*/
        /*------------------------------ Daily TOur Details -----------------------------------*/
        const val API_TOUR_DETAILS_LIST: String = "API_WeeklyDetailsView.aspx"
        const val API_GET_CATEGORY: String = "API_TextListsForViewer.aspx"
        const val API_GET_DEALER_NAME: String = "API_GetDealerAndDistributor.aspx"
        const val API_DISTRICT: String = "API_GetDistrictList.aspx"
        const val API_INSERT_TOUR_DETAILS: String = "API_InsertWeeklyTourDetail.aspx"
        /*------------------------------------------------------------------------------------*/
        /*------------------------------ Tour Voucher Approval --------------------------------*/
        const val API_TOUR_VOUCHER_APPROVE_LIST: String = "API_TourExpenseApprovalList.aspx"
        const val API_EXPENSE_APPROVE: String = "API_TourExpenseUpdateApprovalStatus.aspx"
        /*==========================================================================================*/
        /*============================== New customer/ dealer ======================================*/
        const val API_GET_SELECT_PORTFOLIO: String = "API_SelectPortfolio.aspx"
        const val API_GET_UPDATE_PORTFOLIO: String = "API_UpdatePortfolio.aspx"
        const val API_INSERT_VISIT_ENTRY: String = "API_InsertPortfolio.aspx"
        /*==========================================================================================*/
        /*============================== My Portfolio ======================================*/
        const val API_GET_VIEW_PORTFOLIO: String = "API_ViewPortfolio.aspx"

        /*==========================================================================================*/
        /*============================== Attendance Report ======================================*/
        const val API_GET_MONTH: String = "API_GetMonth.aspx"

        /*========================================================================================*/
        /* ================= WebView Activity ================= */
        const val API_GET_REPORT: String = "API_Facet.aspx"



        /*=================================== DUKE =================================================*/
        /* ================= Tour Agenda Tracking ================= */
        const val API_STATE: String = "API_GetState.aspx"
        const val API_GET_STATION: String = "API_GetBusinessCeneters.aspx"
        const val API_GET_DEALERS: String = "API_GetDealers.aspx"
        const val API_GET_SUBDEALER_DETAILS: String = "API_GetSubDealerDetails.aspx"
        const val API_GET_SERVICE_CENTER: String = "API_GetServiceCenterNames.aspx"
        const val API_GET_RUNNING_DETAILS: String = "API_GetRunningMeetingDetails.aspx"
        const val GET_FACETS: String = "API_Facets.aspx"
        const val API_GET_OBJECTIVE: String = "API_GetObjectives.aspx"
        const val API_START_END_MEETING: String = "API_InsertDealerInOutMeetingTime.aspx"
        const val API_INSERT_WEEKLY_DETAILS: String = "API_InsertTourTracking.aspx"
        const val API_GET_IN_OUT_DETAILS: String = "API_GetDealerInOutDetails.aspx"
        const val API_INSERT_OBJECTIVE: String = "API_InsertDealerObjective.aspx"
        const val API_GET_USER_CHECK_IN_OUT_RIGHTS: String = "API_GetCheckInCheckOutRights.aspx"

        const val API_INSERT_JTD_DETAILS: String = "API_JTDInsert.aspx"



        /*================  Week day off  ====================*/
        const val VALIDATE_SUNDAY: String = "API_ValidateSundayRequset.aspx"
        const val WEEK_OF_DATE: String = "API_InsertSundayRequest.aspx"
        const val API_GET_SUNDAY_REQUEST_LIST: String = "API_GetSundayRequestList.aspx"
        const val SUNDAY_LIST: String = "API_GetApprovalListSundayRequest.aspx"

        /*================================== DUKE ==========================================*/
        /*============================= Car / AIR Approval ======================================*/

        const val API_GET_EMP_DATA: String = "API_GetEmpId.aspx"
        const val API_GET_NEW_VOUCHER_NO: String = "API_GetNewVoucherNo.aspx"
        const val API_TRAVEL_BY_CAR: String = "API_GetTravellingByCar.aspx"
        const val API_GET_RATE_FOR_PER_KM_CAR_AIR_APPROVAL: String = "API_GetRateForPerKMCarAirApproval.aspx"
        const val API_GET_CITY_TYPE: String = "API_GetCityType.aspx"
        const val API_GET_CITIES: String = "API_GetCities.aspx"
        const val API_INSERT_CAR_APPROVAL: String = "API_InsertCarAirApproval.aspx"
        const val API_CAR_AIR_APPROVAL_LIST: String = "API_GetCarAirApproval.aspx"
        const val API_UPDATE_CAR_AIR_APPROVAL_STATUS: String = "API_UpdateCarAirApproval.aspx"

        /*================================== Flotech && Singla ==========================================*/
        /*============================= Daily Tour Details ======================================*/
        const val API_GET_ORDER: String = "DeltaiMarketing/API/API_GetSOs.aspx"
        const val API_CUSTOMER_VIEWER: String = "DeltaViewer/API/API_Customer.aspx"
        const val API_PRODUCT: String = "DeltaViewer/API/API_Products.aspx"
        const val API_GET_RATE: String = "API_GetProductDetails.aspx"
        const val API_INSERT_ORDER: String = "API_InsertSO.aspx"

        // Tour Advance Expense
        const val API_INSERT_TOUR_ADVANCE_EXPENSE: String ="API_InsertTourAdvancedExpense.aspx"
        const val API_VIEW_TOUR_ADVANCE_EXPENSE_LIST: String ="API_ViewTourAdvancedExpense.aspx"
        const val API_UPDATE_TOUR_ADVANCE_EXPENSE: String ="API_UpdateTourAdvancedExpense.aspx"

        /*===========================================================================================*/
        /*=================================== Mascot Account  =======================================*/

        const val API_CUSTOMER: String = "API_Customer.aspx"
        const val API_GET_LEDGER_REPORT: String = "API_LgrReport.aspx"

        /*===========================================================================================*/
        /*=================================== Unnati  =======================================*/
        const val API_INSERT_PROMOTIONAL_ACTIVITY: String = "API_InsertPromotionalActivity.aspx"
        const val API_GET_DISTRICTS: String = "API_GetDistricts.aspx"
        const val API_GET_TARGET_OUTSTANDING: String = "API_GetTargetAchOutstanding.aspx"
        const val API_DEALER_CHECK_IN_OUT: String = "API_DealerCheckInOut.aspx"
        const val API_CHECK_DEALER_IN_OUT_STATUS: String = "API_CheckDealerInOutStatus.aspx"
        const val API_FOLLOW_UP: String = "API_FollowUps.aspx"
        const val API_INSERT_LEAVE_REQUEST: String = "API_InsertLeaveRequest.aspx"
        const val API_VIEW_LEAVE_LIST: String = "API_LeaveRequestView.aspx"
        const val API_VIEW_LEAVE_APPROVAL: String = "API_LeaveApproval.aspx"
        const val API_VIEW_LEAVE_APPROVAL_UPDATE: String = "API_LeaveApprovalUpdate.aspx"


    }
}
