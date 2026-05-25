//package com.i.common.attendance.ui.home.ledgerreport.adapter
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.Context
//import android.text.Html
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.i.common.attendance.R
//import java.text.DecimalFormat
//import java.util.Locale
//
//class LedgerReportAdapter1(
//    private val context: Context,
//    private val data: MutableList<LgrModel>,
//    private val dataNotFiltered: List<LgrModel>,
//    private val pdfReportInterface: PDFReportInterface
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private var listener: RecyclerViewClickListener? = null
//    private lateinit var prefsManager: PrefsManger
//
//    // Dialog references kept as properties to avoid leaks
//    private var dialogView: View? = null
//    private var builder: AlertDialog.Builder? = null
//    private var dialog: AlertDialog? = null
//
//    companion object {
//        private const val TYPE_DIVISION = 1
//        private const val TYPE_DATE = 2
//        private const val TYPE_LEDGER = 3
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when {
//            data[position].viewType.equals("division", ignoreCase = true) -> TYPE_DIVISION
//            data[position].viewType.equals("date", ignoreCase = true) -> TYPE_DATE
//            else -> TYPE_LEDGER
//        }
//    }
//
//    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        prefsManager = PrefsManger(context)
//        val inflater = LayoutInflater.from(viewGroup.context)
//        return when (viewType) {
//            TYPE_DIVISION -> {
//                val view = inflater.inflate(R.layout.item_ledger_report_division, viewGroup, false)
//                ViewHolderDiv(view)
//            }
//
//            TYPE_DATE -> {
//                val view = inflater.inflate(R.layout.row_item_ledger_report_date, viewGroup, false)
//                ViewHolderDt(view)
//            }
//
//            else -> {
//                val view = inflater.inflate(R.layout.item_ledger_report, viewGroup, false)
//                ViewHolderLgr(view)
//            }
//        }
//    }
//
//    // ── View Holders ────────────────────────────────────────────────────────────
//
//    class ViewHolderDt(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val date: TextView = itemView.findViewById(R.id.lgrdate)
//    }
//
//    class ViewHolderDiv(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val lblDivision: TextView = itemView.findViewById(R.id.lblDivision)
//    }
//
//    inner class ViewHolderLgr(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val balance: TextView = itemView.findViewById(R.id.balance)
//        val accountDetails: TextView = itemView.findViewById(R.id.accountDetails)
//        val accountType: TextView = itemView.findViewById(R.id.accountType)
//        val amount: TextView = itemView.findViewById(R.id.amount)
//        val lgrLNview: LinearLayout = itemView.findViewById(R.id.lgrLNview)
//        val linearAmount: LinearLayout = itemView.findViewById(R.id.linearAmount)
//        val linearShowReport: LinearLayout = itemView.findViewById(R.id.linearShowReport)
//        val linearShowReport1: LinearLayout = itemView.findViewById(R.id.linearShowReport1)
//
//        init {
//            try {
//                val btnPDF = itemView.findViewById<ImageView>(R.id.btnPDF)
//                btnPDF.setOnClickListener { view ->
//                    listener?.onItemClick(view, adapterPosition)
//                }
//                linearShowReport.setOnClickListener { view ->
//                    listener?.onItemClick(view, adapterPosition)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                FirebaseCrashlytics.getInstance().recordException(e)
//            }
//        }
//    }
//
//    // ── Bind ─────────────────────────────────────────────────────────────────────
//
//    @SuppressLint("SetTextI18n", "InflateParams")
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        try {
//            val item = data[position]
//            if (!item.isVisible) return
//
//            when (holder.itemViewType) {
//                TYPE_DIVISION -> {
//                    (holder as ViewHolderDiv).lblDivision.text = item.division
//                }
//
//                TYPE_DATE -> {
//                    (holder as ViewHolderDt).date.text = item.transactionDt
//                }
//
//                TYPE_LEDGER -> {
//                    val vhLgr = holder as ViewHolderLgr
//                    val indiaLocale = Locale("en", "IN")
//                    val decimalFormat =
//                        DecimalFormat.getCurrencyInstance(indiaLocale) as DecimalFormat
//
//                    // Transaction label
//                    if (item.transactionNo.isNotEmpty()) {
//                        @Suppress("DEPRECATION")
//                        vhLgr.balance.text = Html.fromHtml(
//                            "<b>${item.transactionNo}</b> ${item.narration}"
//                        )
//                    } else {
//                        @Suppress("DEPRECATION")
//                        vhLgr.balance.text = Html.fromHtml("<b>${item.transactionTypeName}</b>")
//                    }
//
//                    // Amount — prefer Dr, fallback to Cr
//                    if (item.dr_Amt.isNotEmpty()) {
//                        val amt = item.dr_Amt.toDoubleOrNull() ?: 0.0
//                        vhLgr.amount.text = "${decimalFormat.format(amt)}  Dr"
//                        vhLgr.linearAmount.setBackgroundResource(R.drawable.bg_rect_round_color_google_red)
//                    } else {
//                        val amt = item.cr_Amt.toDoubleOrNull() ?: 0.0
//                        vhLgr.amount.text = "${decimalFormat.format(amt)}  Cr"
//                        vhLgr.linearAmount.setBackgroundResource(R.drawable.bg_shape_rect_round_corner_green)
//                    }
//
//                    // Reference number
//                    if (item.refNo.isNotEmpty()) {
//                        vhLgr.accountDetails.visibility = View.VISIBLE
//                        vhLgr.accountDetails.text = item.refNo
//                    } else {
//                        vhLgr.accountDetails.visibility = View.GONE
//                    }
//
//                    // Narration1
//                    if (item.narration1.isNotEmpty()) {
//                        vhLgr.accountType.visibility = View.VISIBLE
//                        vhLgr.accountType.text = item.narration1
//                    } else {
//                        vhLgr.accountType.visibility = View.INVISIBLE
//                    }
//
//                    // Hide show-report button for Opening Balance
//                    vhLgr.linearShowReport1.visibility = View.GONE
//
//                    vhLgr.linearShowReport1.setOnClickListener {
//                        pdfReportInterface.onclick(item.transactionNo)
//                    }
//
//                    vhLgr.lgrLNview.setOnClickListener {
//                        showNarrationDialog()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            FirebaseCrashlytics.getInstance().recordException(e)
//        }
//    }
//
//    // ── Helpers ──────────────────────────────────────────────────────────────────
//
//
//    @SuppressLint("InflateParams")
//    private fun showNarrationDialog() {
//        try {
//            dialogView =
//                LayoutInflater.from(context).inflate(R.layout.show_narration_details, null, false)
//            val lgrNarrationLn = dialogView?.findViewById<LinearLayout>(R.id.asd)
//            builder = AlertDialog.Builder(context).apply { setView(dialogView) }
//            dialog = builder?.create()
//            dialog?.show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            FirebaseCrashlytics.getInstance().recordException(e)
//        }
//    }
//
//    override fun getItemCount(): Int = data.size
//
//    fun setItemClick(mListener: RecyclerViewClickListener) {
//        this.listener = mListener
//    }
//
//
///**
//     * Returns a filtered list based on transaction type.
//     * @param type "Credit", "Debit", or "" for all.
//     */
//
//    fun getListForFilter(type: String): ArrayList<LgrModel> {
//        val list = ArrayList<LgrModel>()
//        when {
//            type.equals("Credit", ignoreCase = true) ->
//                dataNotFiltered.filterTo(list) { it.isCredit }
//
//            type.equals("Debit", ignoreCase = true) ->
//                dataNotFiltered.filterTo(list) { it.isDebit }
//
//            type.isEmpty() ->
//                list.addAll(dataNotFiltered)
//        }
//        notifyDataSetChanged()
//        return list
//    }
//}
