package com.i.common.attendance.ui.home.activity

import com.i.common.attendance.R

/**
 * Single source of truth for drawer menu visibility and ORDER per flavor.
 *
 * The list order here = the exact render order in the drawer.
 *
 * This exactly matches the current production behavior derived from your
 * existing handleDrawerByFlavor() + XML layout order.
 *
 * How to maintain:
 *  - Add a new menu item  → add to [MenuItem] enum + [menuItemLabel] + [handleDrawerItemClick] + relevant flavor lists
 *  - Remove from a client → delete from that flavor's list
 *  - Reorder for a client → reorder that flavor's list
 *  - New flavor           → add one new entry in [flavorMenuItems]
 */
object DrawerMenuConfig {

    enum class MenuItem {
        ACTION_REQUIRED,
        STAFF_ATTENDANCE,
        PJC_CALENDAR,
        DAILY_TOUR,
        TOUR_VOUCHER,
        TOUR_VOUCHER_APPROVAL,
        CHECK_IN,
        DEALER_CHECK_IN,
        ADD_LEAVE,
        VIEW_LEAVE_LIST,
        LEAVE_APPROVAL,
        PROMOTIONAL_ACTIVITY_FORM,
        NEW_CUSTOMER_DEALER,
        MY_PORTFOLIO,
        LOCATION_LOG,
        ORDER_BOOK,
        LEDGER_REPORT,
        OUTSTANDING_REPORT,
        SALES_REPORT,
        ATTENDANCE_REPORT,
        DEALER_WISE_TARGET_ENTRY,
        DISTRICT_WISE_REPORT,
        DEALER_WISE_REPORT,
        WEEK_OFF_DAY,
        WEEK_OFF_LIST,
        WEEK_OFF_DAY_APPROVAL,
        CAR_AIR_APPROVAL,
        CAR_AIR_APPROVAL_LIST,
        ACTIVITY,
        LIST_ACTIVITY,
        FORMS,
        KUSUM_SURVEY_FORM,
        LOCAL_SOLAR_SURVEY_FORM,
        PRIVACY_POLICY,
        TOUR_ADVANCE_EXPENSE,
    }

    val menuItemLabel: Map<MenuItem, Int> = mapOf(
        MenuItem.ACTION_REQUIRED            to R.string.label_action_required_app_setup,
        MenuItem.STAFF_ATTENDANCE           to R.string.toolbar_title_staff_attendance,
        MenuItem.PJC_CALENDAR               to R.string.toolbar_title_project_journey_cycle,
        MenuItem.DAILY_TOUR                 to R.string.toolbar_daily_tour_details,
        MenuItem.TOUR_VOUCHER               to R.string.toolbar_title_tour_voucher,
        MenuItem.TOUR_VOUCHER_APPROVAL      to R.string.toolbar_tour_voucher_approval,
        MenuItem.CHECK_IN                   to R.string.toolbar_title_check_in,
        MenuItem.DEALER_CHECK_IN            to R.string.toolbar_title_dealer_check_in,
        MenuItem.ADD_LEAVE                  to R.string.label_leave,
        MenuItem.VIEW_LEAVE_LIST            to R.string.label_leave_view,
        MenuItem.LEAVE_APPROVAL             to R.string.label_leave_approval,
        MenuItem.PROMOTIONAL_ACTIVITY_FORM  to R.string.toolbar_title_promotional_activity_form,
        MenuItem.NEW_CUSTOMER_DEALER        to R.string.label_new_customer_dealer,
        MenuItem.MY_PORTFOLIO               to R.string.label_my_portfolio,
        MenuItem.LOCATION_LOG               to R.string.toolbar_title_location_logs,
        MenuItem.ORDER_BOOK                 to R.string.toolbar_title_order_book,
        MenuItem.LEDGER_REPORT              to R.string.toolbar_title_ledger_report,
        MenuItem.OUTSTANDING_REPORT         to R.string.toolbar_title_outstanding_report,
        MenuItem.SALES_REPORT               to R.string.toolbar_title_sales_report,
        MenuItem.ATTENDANCE_REPORT          to R.string.toolbar_title_attendance_report,
        MenuItem.DEALER_WISE_TARGET_ENTRY   to R.string.toolbar_title_dealer_wise_report_entry,
        MenuItem.DISTRICT_WISE_REPORT       to R.string.toolbar_title_district_wise_report,
        MenuItem.DEALER_WISE_REPORT         to R.string.toolbar_title_dealer_wise_report,
        MenuItem.WEEK_OFF_DAY               to R.string.toolbar_title_week_off_day,
        MenuItem.WEEK_OFF_LIST              to R.string.toolbar_title_week_off_list,
        MenuItem.WEEK_OFF_DAY_APPROVAL      to R.string.toolbar_title_week_off_day_approval,
        MenuItem.CAR_AIR_APPROVAL           to R.string.toolbar_title_car_air_approval,
        MenuItem.CAR_AIR_APPROVAL_LIST      to R.string.toolbar_title_car_air_approval_list,
        MenuItem.ACTIVITY                   to R.string.toolbar_title_activity,
        MenuItem.LIST_ACTIVITY              to R.string.toolbar_title_list_activity,
        MenuItem.FORMS                      to R.string.toolbar_title_forms,
        MenuItem.KUSUM_SURVEY_FORM          to R.string.toolbar_title_kusum_survey_form,
        MenuItem.LOCAL_SOLAR_SURVEY_FORM    to R.string.toolbar_title_local_solar_survey_form,
        MenuItem.PRIVACY_POLICY             to R.string.toolbar_title_privacy_policy,
        MenuItem.TOUR_ADVANCE_EXPENSE       to R.string.label_tour_advance_expense,
    )

    // ── Flavor menus ──────────────────────────────────────────────────────────
    // Each list is the EXACT current production sequence for that flavor,
    // derived from the XML layout order + handleDrawerByFlavor() visibility flags.

    private val watermanItems = listOf(
        MenuItem.ACTION_REQUIRED,
        MenuItem.STAFF_ATTENDANCE,          // not hidden by waterman
        MenuItem.PJC_CALENDAR,
        MenuItem.DAILY_TOUR,
        MenuItem.TOUR_VOUCHER,
        MenuItem.TOUR_VOUCHER_APPROVAL,     // not hidden by waterman
        MenuItem.NEW_CUSTOMER_DEALER,
        MenuItem.MY_PORTFOLIO,
        MenuItem.ATTENDANCE_REPORT,         // not hidden by waterman
        MenuItem.DEALER_WISE_TARGET_ENTRY,  // not hidden by waterman
        MenuItem.DISTRICT_WISE_REPORT,      // not hidden by waterman
        MenuItem.DEALER_WISE_REPORT,        // not hidden by waterman
    )

    private val unnatiItems = listOf(
        MenuItem.ACTION_REQUIRED,
        MenuItem.PJC_CALENDAR,
        MenuItem.DAILY_TOUR,
        MenuItem.TOUR_VOUCHER,
        MenuItem.DEALER_CHECK_IN,
        MenuItem.ADD_LEAVE,
        MenuItem.VIEW_LEAVE_LIST,
        MenuItem.LEAVE_APPROVAL,
        MenuItem.PROMOTIONAL_ACTIVITY_FORM,
        MenuItem.NEW_CUSTOMER_DEALER,
        MenuItem.MY_PORTFOLIO,
        MenuItem.DEALER_WISE_TARGET_ENTRY,
        MenuItem.DISTRICT_WISE_REPORT,
        MenuItem.DEALER_WISE_REPORT,
    )

    private val dukeItems = listOf(
        MenuItem.ACTION_REQUIRED,
        MenuItem.PJC_CALENDAR,
        MenuItem.DAILY_TOUR,
        MenuItem.TOUR_VOUCHER,
        MenuItem.CHECK_IN,
        MenuItem.NEW_CUSTOMER_DEALER,
        MenuItem.MY_PORTFOLIO,
        MenuItem.WEEK_OFF_DAY,
        MenuItem.WEEK_OFF_LIST,
        MenuItem.WEEK_OFF_DAY_APPROVAL,
        MenuItem.CAR_AIR_APPROVAL,
        MenuItem.CAR_AIR_APPROVAL_LIST,
        MenuItem.ACTIVITY,
        MenuItem.LIST_ACTIVITY,
        MenuItem.FORMS,
        MenuItem.KUSUM_SURVEY_FORM,
        MenuItem.LOCAL_SOLAR_SURVEY_FORM,
        MenuItem.PRIVACY_POLICY,
    )

    private val flotechItems = listOf(
        MenuItem.ACTION_REQUIRED,
        MenuItem.PJC_CALENDAR,
        MenuItem.DAILY_TOUR,
        MenuItem.TOUR_VOUCHER,
        MenuItem.NEW_CUSTOMER_DEALER,
        MenuItem.MY_PORTFOLIO,
        MenuItem.LOCATION_LOG,
        MenuItem.ORDER_BOOK,
        MenuItem.OUTSTANDING_REPORT,
        MenuItem.SALES_REPORT,
    )

    private val singlaAlgoItems = listOf(
        MenuItem.ACTION_REQUIRED,
        MenuItem.PJC_CALENDAR,
        MenuItem.DAILY_TOUR,
        MenuItem.TOUR_VOUCHER,
        MenuItem.TOUR_ADVANCE_EXPENSE,
        MenuItem.NEW_CUSTOMER_DEALER,
        MenuItem.MY_PORTFOLIO,
        MenuItem.LOCATION_LOG,
        MenuItem.ATTENDANCE_REPORT,
    )

    private val mascotItems = listOf(
        MenuItem.ACTION_REQUIRED,
        MenuItem.PJC_CALENDAR,
        MenuItem.DAILY_TOUR,
        MenuItem.TOUR_VOUCHER,
        MenuItem.NEW_CUSTOMER_DEALER,
        MenuItem.MY_PORTFOLIO,
        MenuItem.LEDGER_REPORT,
    )

    private val flavorMenuItems: Map<String, List<MenuItem>> = mapOf(
        "waterman" to watermanItems,
        "unnati"   to unnatiItems,
        "duke"     to dukeItems,
        "flotech"  to flotechItems,
        "singla"   to singlaAlgoItems,
        "algo"     to singlaAlgoItems,
        "mascot"   to mascotItems,
    )

    /**
     * Returns the ordered menu item list for [flavor].
     * Falls back to a minimal safe list for any unregistered flavor.
     */
    fun menuItemsFor(flavor: String): List<MenuItem> =
        flavorMenuItems[flavor] ?: listOf(
            MenuItem.ACTION_REQUIRED,
            MenuItem.PJC_CALENDAR,
            MenuItem.DAILY_TOUR,
            MenuItem.TOUR_VOUCHER,
            MenuItem.NEW_CUSTOMER_DEALER,
            MenuItem.MY_PORTFOLIO,
        )
}