package com.i.common.attendance.ui.home.pjc.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.i.common.attendance.R
import com.i.common.attendance.base.BaseFragment
import com.i.common.attendance.databinding.FragmentPjcBinding
import com.i.common.attendance.network.request.GetPjcRequest
import com.i.common.attendance.network.response.EventsCalModel
import com.i.common.attendance.network.response.HolidayWeekOffModel
import com.i.common.attendance.ui.home.activity.HomeActivity
import com.i.common.attendance.utils.Constants.setSafeOnClickListener
import com.i.common.attendance.utils.EncryptedPrefHelper
import com.i.common.attendance.ui.home.pjc.viewmodel.CalendarViewModel
import com.i.common.attendance.ui.home.pjc.viewmodel.PJCState
import com.i.common.attendance.ui.home.pjc.viewmodel.PjcEventState
import com.sickmartian.calendarview.CalendarView
import com.sickmartian.calendarview.MonthView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PjcFragment : BaseFragment() {

    private lateinit var binding : FragmentPjcBinding
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private val cal: Calendar = Calendar.getInstance()
    @Inject lateinit var sharedPref: EncryptedPrefHelper

    private val viewmodel: CalendarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPjcBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageToolBar()
        initCalendar()
        observePjcApi()
        observePjcEventApi()
        moveOnCLickListeners()
    }

    private fun moveOnCLickListeners() = with(binding) {
        btnFloatingCalendar.setSafeOnClickListener{
            loadFragment(fragment = PjcInsertPlanFragment(), isAdd = false, isAddBackStack = true)
        }
    }

    private fun manageToolBar() {
        (activity as HomeActivity).apply {
            manageToolBar(isVisible = true)
            manageToolBarTitle(getString(R.string.toolbar_title_project_journey_cycle))
            manageBackButtonClick(true)
            setDrawerEnabled(false)
            manageInfo(false)
        }
    }

    private fun initCalendar() = with(binding) {

        // Initial state
        setStateByCalendar(cal)
        applyCalendarState()

        // Month label
        updateMonthLabel()

        // Day click listener
        monthView.setDaySelectedListener(object : CalendarView.DaySelectionListener {

            override fun onTapEnded(
                calendarView: CalendarView,
                dayMetadata: CalendarView.DayMetadata
            ) {
                monthView.setSelectedDay(dayMetadata)
                val user = sharedPref.getUser()
                val selectedDate = "${dayMetadata.day}-${getMonthName(dayMetadata.month)}-${dayMetadata.year}"

                viewmodel.getPjcEvent(date = selectedDate, mobileNo = user?.MobileNo?:"")
                // TODO: call your event API
                // initWsToGetPJCEvent(selectedDate)
            }

            override fun onLongClick(
                calendarView: CalendarView,
                dayMetadata: CalendarView.DayMetadata
            ) {
                monthView.setSelectedDay(dayMetadata)
            }
        })


        // Next month
        calenderNextIcon.setSafeOnClickListener {
            cal.add(Calendar.MONTH, 1)
            onMonthChanged()
        }

        // Previous month
        calenderBackIcon.setSafeOnClickListener {
            cal.add(Calendar.MONTH, -1)
            onMonthChanged()
        }

        // Load current month data
        loadMonthWiseData()
    }

    private fun onMonthChanged() {
        setStateByCalendar(cal)
        applyCalendarState()
        updateMonthLabel()
        loadMonthWiseData()
    }

    private fun applyCalendarState() = with(binding) {
        if (monthView is MonthView) {
            (monthView as MonthView).setDate(mMonth, mYear)
        }

        monthView.setFirstDayOfTheWeek(CalendarView.SUNDAY_SHIFT)
        monthView.setCurrentDay(getCalendarForState())
    }

    private fun setStateByCalendar(calendar: Calendar) {
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH) + 1 // IMPORTANT
        mDay = calendar.get(Calendar.DATE)
    }

    private fun getCalendarForState(): Calendar =
        Calendar.getInstance().apply {
            setMinimalDaysInFirstWeek(1)
            firstDayOfWeek = Calendar.SUNDAY
            set(Calendar.YEAR, mYear)
            set(Calendar.MONTH, mMonth - 1)
            set(Calendar.DATE, mDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

    private fun updateMonthLabel() = with(binding) {
        tctViewMonthName.text = SimpleDateFormat(
            "MMM yyyy",
            Locale.ENGLISH
        ).format(cal.time)
    }

    private fun loadMonthWiseData() {
        val monthName = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(cal.time)

        // TODO: call API exactly like Java
        // initWsToGetPJC(monthName)
        val user = sharedPref.getUser()
        viewmodel.loadCalendarData(GetPjcRequest(user?.MobileNo ?: "", monthName))
        //viewmodel.loadHolidayWeekOff(user?.MobileNo?:"", monthName)
    }

    private fun getMonthName(month: Int): String {
        return SimpleDateFormat("MMM", Locale.ENGLISH).format(
            Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1)
            }.time
        )
    }

    /* ---------------- CALENDAR EVENTS ---------------- */

    private fun showCalendar(events: List<EventsCalModel>) {
        if (binding.monthView !is MonthView) return
        val monthView = binding.monthView as MonthView

        monthView.removeAllContent()

        val sdf = SimpleDateFormat(
            "dd-MMM-yyyy HH:mm:ss a",
            Locale.ENGLISH
        )
        val primarySdf = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.ENGLISH)
        val fallbackSdf = SimpleDateFormat("dd/MMM/yyyy HH:mm:ss a", Locale.ENGLISH)
        val apiDukeSdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss a", Locale.ENGLISH)

        events.forEachIndexed { index, event ->
            try {
                val date = try {
                    primarySdf.parse(event.date)
                } catch (e: Exception) {
                    null
                } ?: try {
                    fallbackSdf.parse(event.date)
                } catch (e: Exception) {
                    null
                } ?: try {
                    apiDukeSdf.parse(event.date)
                } catch (e: Exception) {
                    null
                } ?: return@forEachIndexed
                //val date = sdf.parse(event.date) ?: return@forEachIndexed

                val cal = Calendar.getInstance().apply { time = date }

                // Only render current visible month/year
                if (cal.get(Calendar.MONTH) + 1 != mMonth ||
                    cal.get(Calendar.YEAR) != mYear
                ) return@forEachIndexed

                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

                // ✅ Inflate SAME layout as Java
                val view = layoutInflater.inflate(
                    R.layout.layout_calendar_event,
                    null,
                    false
                )

                val lblEvent = view.findViewById<TextView>(R.id.lblEvent)
                val lblEventDropPlan =
                    view.findViewById<TextView>(R.id.lblEventDropPlan)

                // ✅ SAME LOGIC AS JAVA
                if (event.isDrop.equals("True", ignoreCase = true)) {

                    lblEventDropPlan.text =
                        if (event.dropReason.isNotEmpty())
                            event.dropReason
                        else
                            "Drop"

                    lblEventDropPlan.visibility = View.VISIBLE
                    lblEvent.visibility = View.GONE

                    // Strike-through
                    lblEventDropPlan.paintFlags =
                        lblEventDropPlan.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                } else {

                    lblEvent.text =
                        if (event.notes.isNotEmpty())
                            event.notes
                        else
                            "Plan"

                    lblEvent.visibility = View.VISIBLE
                    lblEventDropPlan.visibility = View.GONE
                }

                // ✅ ADD TO CALENDAR CELL
                monthView.addViewToDayInCurrentMonth(dayOfMonth, view)

            } catch (e: Exception) {
                Log.e("PJC_CAL", "Calendar render error", e)
            }
        }
    }

    private fun drawHolidayWeekOff(list: List<HolidayWeekOffModel>, month: Int, year: Int) {
        list.forEach { model ->
            val view = layoutInflater.inflate(
                R.layout.layout_week_off_event,
                null
            )

            val lblWeekOff =
                view.findViewById<TextView>(R.id.lblWeekOff)
            val lblHoliday =
                view.findViewById<TextView>(R.id.lblHolidayOff)

            when (model.type) {
                "WOFF" -> {
                    lblWeekOff.visibility = View.VISIBLE
                    lblHoliday.visibility = View.GONE
                }
                "Holiday" -> {
                    lblWeekOff.visibility = View.GONE
                    lblHoliday.visibility = View.VISIBLE
                }
            }

            // ✅ DIRECTLY ADD — NO SUNDAY CHECK
            binding.monthView.addViewToDayInCurrentMonth(
                model.calendarDate,
                view
            )
        }
    }

    private fun observePjcApi(){
        viewmodel.pjcState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is PJCState.Loading -> {
                    showLoader()
                }

                is PJCState.Success -> {
                    hideLoader()
                    binding.monthView.post {
                        showCalendar(events = state.pjcList)
                        drawHolidayWeekOff(list = state.holidayList, month = mMonth, year = mYear)
                    }
                }

                is PJCState.Empty -> {
                    hideLoader()
                    binding.monthView.removeAllContent()
                    showToast(state.message)
                }

                is PJCState.Error -> {
                    hideLoader()
                    showToast(state.message)
                }
            }
        }
    }

    private fun observePjcEventApi() {
        viewmodel.pjcEventState.observe(viewLifecycleOwner) { state ->
            when (state) {

                is PjcEventState.Loading -> {
                    showLoader()
                    Log.e("PjcEventState.Loading","PjcEventState.Loading")
                }

                is PjcEventState.Success -> {
                    hideLoader()
                    Log.e("PjcEventState.Success","PjcEventState.Success")
                    Log.e("event",state.events.toString())
                    SelectPjcEventBottomSheetFragment.Companion.newInstance(state.events).show(parentFragmentManager,"SelectPjcEventBottomSheet")
                }

                is PjcEventState.Error -> {
                    Log.e("PjcEventState.Error","PjcEventState.Error")
                    hideLoader()
                    showToast(state.message)
                }

                else -> Unit
            }
        }
    }

    private fun showLoader() {
        requireActivity().window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val params = attributes
            params.alpha = 0.5f   // 👈 dim level (0.0f - 1.0f)
            attributes = params
        }

        binding.progressBarPJC.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        requireActivity().window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val params = attributes
            params.alpha = 1.0f   // 👈 restore normal brightness
            attributes = params
        }

        binding.progressBarPJC.visibility = View.GONE
    }
}