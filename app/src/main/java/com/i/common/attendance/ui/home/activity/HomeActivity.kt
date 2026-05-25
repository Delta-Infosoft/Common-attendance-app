package com.i.common.attendance.ui.home.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.i.common.attendance.BuildConfig
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseActivity
import com.i.common.attendance.databinding.ActivityHomeBinding
import com.i.common.attendance.drawer.DrawerMenuAdapter
import com.i.common.attendance.ui.authentication.activity.AuthenticationActivity
import com.i.common.attendance.ui.home.attendancereport.fragment.AttendanceReportFragment
import com.i.common.attendance.ui.home.dailytour.fragment.DailyTourListFragment
import com.i.common.attendance.ui.home.dealerwisereport.data.FacetType
import com.i.common.attendance.ui.home.dealerwisereport.fragment.DealerWiseTargetEntryFragment
import com.i.common.attendance.ui.home.dealerwisereport.viewmodel.FacetUiState
import com.i.common.attendance.ui.home.dealerwisereport.viewmodel.ReportViewModel
import com.i.common.attendance.ui.home.fragment.ActionRequiredFragment
import com.i.common.attendance.ui.home.fragment.HomeFragment
import com.i.common.attendance.ui.home.myportfolio.fragment.MyPortfolioFragment
import com.i.common.attendance.ui.home.newcustomerdealer.fragment.NewCustomerDealerFragment
import com.i.common.attendance.ui.home.pjc.fragment.PjcFragment
import com.i.common.attendance.ui.home.carairapproval.fragment.CarAirApprovalFragment
import com.i.common.attendance.ui.home.carairapproval.fragment.CarAirApprovalListFragment
import com.i.common.attendance.ui.home.dealercheckin.fragment.DealerCheckInUnnatiFragment
import com.i.common.attendance.ui.home.dealercheckin.fragment.PromotionalActivityFormFragment
import com.i.common.attendance.ui.home.leave.fragment.AddLeaveUnnatiFragment
import com.i.common.attendance.ui.home.leave.fragment.LeaveApprovalListUnnatiFragment
import com.i.common.attendance.ui.home.leave.fragment.ViewLeaveListUnnatiFragment
import com.i.common.attendance.ui.home.ledgerreport.fragment.LedgerReportFragment
import com.i.common.attendance.ui.home.orderbook.fragment.OrderBookFragment
import com.i.common.attendance.ui.home.touradvanceexpense.fragment.AddTourAdvanceExpenseFragment
import com.i.common.attendance.ui.home.touradvanceexpense.fragment.TourAdvanceExpenseFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.TourAgendaTrackingFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.WeekOffApprovalListDukeFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.WeekOffDayDukeFragment
import com.i.common.attendance.ui.home.touragendatracking.fragment.WeekOffListDukeFragment
import com.i.common.attendance.ui.home.tourvoucher.fragment.TourVoucherListFragment
import com.i.common.attendance.ui.home.tourvoucherapproval.fragment.TourVoucherApprovalListFragment
import com.i.common.attendance.ui.home.viewmodel.HomeViewModel
import com.i.common.attendance.ui.home.webview.activity.WebViewActivity
import com.i.common.attendance.ui.home.webview.activity.WebViewActivity.Companion.WEB_VIEW_TITLE
import com.i.common.attendance.ui.home.webview.activity.WebViewActivity.Companion.WEB_VIEW_URL
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.Constants.showConfirmDialog
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.delta.attendanceappv2.ui.home.viewmodel.LogoutState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val sharedViewModel: HomeViewModel by viewModels()
    private val factViewmodel: ReportViewModel by viewModels()
    private val homeViewModel: com.i.common.attendance.ui.viewmodel.HomeViewModel by viewModels()

    @Inject
    lateinit var sharedPref: EncryptedPrefHelper

    // ─── Drawer adapter ───────────────────────────────────────────────────────
    private lateinit var drawerMenuAdapter: DrawerMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkLocationPermission() || !checkBatteryOptimization()) {
            binding.toolbarHome.visibility = View.GONE
            binding.imgViewInfo.visibility = View.GONE
            showPermissionError()
            return
        }

        binding.toolbarHome.visibility = View.VISIBLE
        binding.imgViewInfo.visibility = View.VISIBLE

        if (savedInstanceState == null) {
            val fragment = when (BuildConfig.FLAVOR) {
                "duke" -> TourAgendaTrackingFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment = fragment, isAdd = false, isAddBackStack = false)
        }

        setUpNavigationDrawer()
        observeLogOutApiData()
        showAutoStartDialogIfNeeded()
        observeFactData()
    }

    // ─── Drawer setup ─────────────────────────────────────────────────────────

    // Keep one mutable source for current drawer items
    private val currentDrawerItems = mutableListOf<DrawerMenuConfig.MenuItem>()

    private fun setUpNavigationDrawer() = with(binding) {

        imgViewMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Initial flavor menu
        currentDrawerItems.clear()
        currentDrawerItems.addAll(
            DrawerMenuConfig.menuItemsFor(BuildConfig.FLAVOR)
        )

        drawerMenuAdapter = DrawerMenuAdapter(
            items = currentDrawerItems, onItemClick = { menuItem ->
                handleDrawerItemClick(menuItem)
                drawerLayout.closeDrawer(GravityCompat.START)
            })

        recyclerViewDrawerMenu.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = drawerMenuAdapter
        }

        btnLogOut.setSafeOnClickListener {
            showConfirmDialog(
                context = this@HomeActivity,
                title = "Logout",
                message = "You will be logged out from this device",
                onOk = {
                    Log.e("LogOut Activity", "true")
                    val user = sharedPref.getUser()
                    sharedViewModel.logout(
                        userName = user?.MobileNo ?: "",
                        imei = Constants.getDeviceId(this@HomeActivity)
                    )
                })
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun updateDrawerMenu(menuItem: DrawerMenuConfig.MenuItem, isVisible: Boolean) {

        val updatedList = currentDrawerItems.toMutableList()
        if (isVisible) {

            if (!updatedList.contains(menuItem)) {
                val originalList = DrawerMenuConfig.menuItemsFor(BuildConfig.FLAVOR)
                val targetIndex = originalList.indexOf(menuItem)
                if (targetIndex == -1) return

                // Find nearest valid insert position
                val insertAt = updatedList.indexOfFirst { existingItem ->
                    originalList.indexOf(existingItem) > targetIndex
                }.let {
                    if (it == -1) updatedList.size else it
                }

                updatedList.add(insertAt, menuItem)
            }

        } else {
            updatedList.remove(menuItem)
        }

        currentDrawerItems.clear()
        currentDrawerItems.addAll(updatedList)

        refreshDrawerMenu(updatedList)
    }

    fun isNewDealerSubDealerVisible(isVisible: Boolean) {
        updateDrawerMenu(DrawerMenuConfig.MenuItem.NEW_CUSTOMER_DEALER, isVisible)
    }
    fun isActivityListActivityVisible(isVisible: Boolean) {
        updateDrawerMenu(DrawerMenuConfig.MenuItem.ACTIVITY, isVisible)
        updateDrawerMenu(DrawerMenuConfig.MenuItem.LIST_ACTIVITY, isVisible)
    }
    fun isTeamAttendanceVisible(isVisible: Boolean) {
        updateDrawerMenu(DrawerMenuConfig.MenuItem.STAFF_ATTENDANCE, isVisible)
    }
    /**
     * Central click dispatcher for every drawer menu item.
     * Add a new item here when you add it to DrawerMenuConfig.
     */
    private fun handleDrawerItemClick(item: DrawerMenuConfig.MenuItem) {
        when (item) {
            DrawerMenuConfig.MenuItem.ACTION_REQUIRED ->
                loadFragment(ActionRequiredFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.PJC_CALENDAR ->
                loadFragment(PjcFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.DAILY_TOUR ->
                loadFragment(DailyTourListFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.TOUR_VOUCHER ->
                loadFragment(TourVoucherListFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.TOUR_VOUCHER_APPROVAL ->
                loadFragment(TourVoucherApprovalListFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.MY_PORTFOLIO ->
                loadFragment(MyPortfolioFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.NEW_CUSTOMER_DEALER ->
                loadFragment(NewCustomerDealerFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.CHECK_IN ->
                loadFragment(HomeFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.DEALER_CHECK_IN ->
                loadFragment(DealerCheckInUnnatiFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.ADD_LEAVE ->
                loadFragment(AddLeaveUnnatiFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.VIEW_LEAVE_LIST ->
                loadFragment(ViewLeaveListUnnatiFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.LEAVE_APPROVAL ->
                loadFragment(LeaveApprovalListUnnatiFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.PROMOTIONAL_ACTIVITY_FORM ->
                loadFragment(PromotionalActivityFormFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.LEDGER_REPORT ->
                loadFragment(LedgerReportFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.ATTENDANCE_REPORT ->
                loadFragment(AttendanceReportFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.DEALER_WISE_TARGET_ENTRY ->
                loadFragment(DealerWiseTargetEntryFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.DISTRICT_WISE_REPORT ->
                factViewmodel.loadReport(FacetType.DISTRICT_WISE)

            DrawerMenuConfig.MenuItem.DEALER_WISE_REPORT ->
                factViewmodel.loadReport(FacetType.DEALER_WISE)

            DrawerMenuConfig.MenuItem.ORDER_BOOK ->
                loadFragment(OrderBookFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.OUTSTANDING_REPORT ->
                showToast(getString(R.string.validation_message_under_development))

            DrawerMenuConfig.MenuItem.SALES_REPORT ->
                showToast(getString(R.string.validation_message_under_development))

            DrawerMenuConfig.MenuItem.LOCATION_LOG -> {
                val mobileNo = sharedPref.getUser()?.MobileNo ?: ""
                val url = when (BuildConfig.FLAVOR) {
                    "flotech" -> "https://aws.deltasoftware.in/Flotech/DeltaiAttendance/Map/Map.aspx?MobileNo=$mobileNo&LoginFrom=APP"
                    "singla"  -> "http://103.168.19.137/DeltaiAttendance/Map/Map.aspx?MobileNo=$mobileNo&LoginFrom=APP"
                    "algo"    -> "https://aws.deltasoftware.in/ALGO/DeltaiAttendance/Map/Map.aspx?MobileNo=$mobileNo&LoginFrom=APP"
                    else      -> return // safety: should not reach here for other flavors
                }
                provideWebView("Location Logs", url)
            }

            DrawerMenuConfig.MenuItem.TOUR_ADVANCE_EXPENSE ->
                loadFragment(TourAdvanceExpenseFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.WEEK_OFF_DAY ->
                loadFragment(WeekOffDayDukeFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.WEEK_OFF_LIST ->
                loadFragment(WeekOffListDukeFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.WEEK_OFF_DAY_APPROVAL ->
                loadFragment(WeekOffApprovalListDukeFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.CAR_AIR_APPROVAL ->
                loadFragment(CarAirApprovalFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.CAR_AIR_APPROVAL_LIST ->
                loadFragment(CarAirApprovalListFragment(), isAdd = false, isAddBackStack = true)

            DrawerMenuConfig.MenuItem.ACTIVITY ->
                loadFragment(WeekOffDayDukeFragment(), isAdd = false, isAddBackStack = true) // replace with ActivityFragment

            DrawerMenuConfig.MenuItem.LIST_ACTIVITY ->
                loadFragment(WeekOffListDukeFragment(), isAdd = false, isAddBackStack = true) // replace with ListActivityFragment

            DrawerMenuConfig.MenuItem.FORMS ->
                provideWebView("Form", "https://ierp.dukeplasto.com/GoogleForm.html")

            DrawerMenuConfig.MenuItem.KUSUM_SURVEY_FORM ->
                provideWebView("Form", "https://docs.google.com/forms/d/e/1FAIpQLSdePgadZ_QnZhRMR9POkuWMHYR2xiRCJ0lnzWOtilajaLzepA/viewform")

            DrawerMenuConfig.MenuItem.LOCAL_SOLAR_SURVEY_FORM ->
                provideWebView("Form", "https://docs.google.com/forms/d/e/1FAIpQLSeByGMdFGjYUhj6LugarfNe0f3RuqZfJkn15nZqosOUpHomjQ/viewform?usp=header")

            DrawerMenuConfig.MenuItem.PRIVACY_POLICY ->
                provideWebView("Privacy Policy", "") // replace with actual URL

            DrawerMenuConfig.MenuItem.STAFF_ATTENDANCE -> {
                // TODO: uncomment when StaffAttendanceFragment is ready
                // loadFragment(StaffAttendanceFragment(), isAdd = false, isAddBackStack = true)
            }
        }
    }

    // ─── Toolbar helpers (unchanged public API) ────────────────────────────────

    fun manageInfo(isVisible: Boolean) = with(binding) {
        if (isVisible) {
            imgViewInfo.visibility = View.VISIBLE
            imgViewInfo.setOnClickListener {
                loadFragment(ActionRequiredFragment(), isAdd = false, isAddBackStack = true)
            }
        } else {
            imgViewInfo.visibility = View.GONE
        }
    }

    fun manageToolBar(isVisible: Boolean) {
        binding.toolbarHome.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun manageBackButtonClick(isVisible: Boolean) {
        if (isVisible) {
            binding.imgViewBack.visibility = View.VISIBLE
            binding.imgViewMenu.visibility = View.GONE
            binding.imgViewBack.setOnClickListener {
                supportFragmentManager.popBackStackImmediate()
            }
        } else {
            binding.imgViewBack.visibility = View.GONE
        }
    }

    fun manageToolBarTitle(title: String) {
        binding.txtViewTitle.text = title
    }

    fun isIconVisible(isVisible: Boolean) {
        if (isVisible) {
            binding.imgViewBack.visibility = View.VISIBLE
            binding.imgViewMenu.visibility = View.VISIBLE
        } else {
            binding.imgViewBack.visibility = View.GONE
            binding.imgViewMenu.visibility = View.GONE
        }
    }

    fun manageDrawerLock(isDrawerVisible: Boolean) {
        if (isDrawerVisible) {
            binding.imgViewMenu.visibility = View.VISIBLE
            binding.imgViewBack.visibility = View.GONE
        } else {
            binding.imgViewMenu.visibility = View.GONE
            binding.imgViewBack.visibility = View.VISIBLE
        }
    }

    fun setDrawerEnabled(enabled: Boolean) {
        binding.drawerLayout.setDrawerLockMode(
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED
            else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )
    }

    fun updateDrawerHeader(userName: String, email: String) {
        binding.txtViewUserName.text = getString(R.string.place_holder_name, userName)
    }

    /**
     * Dynamically update the drawer menu at runtime (e.g. after a role/permission
     * change without restarting the Activity).
     */
    fun refreshDrawerMenu(newItems: List<DrawerMenuConfig.MenuItem>) {
        drawerMenuAdapter.submitList(newItems)
    }

    // ─── Kept unchanged below ─────────────────────────────────────────────────

    private fun observeLogOutApiData() {
        Log.e("LogOut Click", "True")
        sharedViewModel.logoutState.observe(this) { state ->
            when (state) {
                is LogoutState.Loading -> showLoader()
                is LogoutState.Success -> {
                    hideLoader()
                    sharedPref.clear()
                    homeViewModel.clearLocalData()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is LogoutState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
                else -> Unit
            }
        }
    }

    private fun showLoader() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBarHomeActivity.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding.progressBarHomeActivity.visibility = View.GONE
    }

    private fun checkBatteryOptimization(): Boolean {
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.isIgnoringBatteryOptimizations(packageName)
        } else true
    }

    private fun checkLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionError() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(
                "This app uses location in the background to track attendance during working hours:\n\n" +
                        "1. 'Allow all the time' location permission\n" +
                        "2. 'No restrictions/Unrestricted/Allow background activity' battery settings\n" +
                        "3. 'Disable Pause app activity if unused\n\n" +
                        "Enable in settings and relaunch."
            )
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Exit") { _, _ -> finishAffinity() }
            .setCancelable(false)
            .show()
    }

    private fun showAutoStartDialogIfNeeded() {
        val prefs = getSharedPreferences("auto_start_pref", MODE_PRIVATE)
        if (prefs.getBoolean("shown", false)) return

        val manufacturer = Build.MANUFACTURER.lowercase()
        if (manufacturer !in listOf("vivo", "xiaomi", "redmi", "poco", "oppo", "realme")) {
            prefs.edit().putBoolean("shown", true).apply()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Enable Auto-Start")
            .setMessage(
                "For reliable attendance tracking, you may enable auto-start in background.\n\n" +
                        "Steps:\nSettings → Apps → Auto-start/Background autostart → Delta iAttendance → Allow"
            )
            .setPositiveButton("Enable") { _, _ ->
                prefs.edit().putBoolean("shown", true).apply()
                openAutoStartSettings()
            }
            .setNegativeButton("Later") { _, _ ->
                prefs.edit().putBoolean("shown", true).apply()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAutoStartSettings() {
        try {
            when (Build.MANUFACTURER.lowercase()) {
                "vivo" -> startActivity(Intent().apply {
                    component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                })
                "xiaomi", "redmi", "poco" -> startActivity(Intent().apply {
                    component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                })
                "oppo" -> startActivity(Intent().apply {
                    component = ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                })
                "realme" -> startActivity(Intent().apply {
                    component = ComponentName(
                        "com.realme.securitycenter",
                        "com.realme.securitycenter.permission.startup.StartupAppListActivity"
                    )
                })
                else -> startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                )
            }
        } catch (e: Exception) {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:$packageName"))
            )
        }
    }

    private fun provideWebView(title: String, url: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(WEB_VIEW_TITLE, title)
        intent.putExtra(WEB_VIEW_URL, url)
        startActivity(intent)
    }

    private fun observeFactData() {
        factViewmodel.facetState.observe(this) { state ->
            when (state) {
                is FacetUiState.Loading -> showLoader()
                is FacetUiState.Success -> {
                    hideLoader()
                    when (state.type) {
                        FacetType.DISTRICT_WISE -> provideWebView("District Wise Report", state.reportUrl)
                        FacetType.DEALER_WISE   -> provideWebView("Dealer Wise Report", state.reportUrl)
                    }
                }
                is FacetUiState.ApiError     -> { hideLoader(); showToast(state.message) }
                is FacetUiState.NetworkError -> { hideLoader(); showToast(state.message) }
                else -> Unit
            }
        }
    }
}