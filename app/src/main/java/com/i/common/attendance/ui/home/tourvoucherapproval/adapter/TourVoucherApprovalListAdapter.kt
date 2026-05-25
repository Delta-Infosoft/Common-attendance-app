package com.i.common.attendance.ui.home.tourvoucherapproval.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.databinding.RowItemTourVoucherApprovalListBinding
import com.i.common.attendance.network.response.TourVoucherItem
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.removeTrailingZeros
import com.i.common.attendance.utils.Constants.setSafeOnClickListener

class TourVoucherApprovalListAdapter(
    private val onSubmitClick: (TourVoucherItem) -> Unit
) : ListAdapter<TourVoucherItem, TourVoucherApprovalListAdapter.DealerViewHolder>(DiffCallback()) {

    inner class DealerViewHolder(private val binding: RowItemTourVoucherApprovalListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TourVoucherItem) = with(binding) {

            tvDateRange.text    = "${Constants.convertDateFormat(item.TravelDt ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")} → " +
                    Constants.convertDateFormat(item.TravelToDt ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")
            tvFromLocation.text = "${item.FromPlace ?: ""} - ${Constants.convertDateFormat(item.StartTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"
            tvToLocation.text   = "${item.ToPlace ?: ""} - ${Constants.convertDateFormat(item.EndTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"
            tvBus.text          = item.TravellingBy ?: ""
            tvTotal.text        = "Total : ${item.TotalExpenses?.removeTrailingZeros() ?: "0"} ₹"

            // ── Spinner setup ────────────────────────────────────────────────
            val statusOptions    = listOf("Approved", "DisApproved")
            val statusDisplayMap = mapOf("A" to "Approved", "D" to "DisApproved")
            val statusSubmitMap  = mapOf("Approved" to "A", "DisApproved" to "D")

            spinnerStatus.setAdapter(
                ArrayAdapter(root.context, android.R.layout.simple_dropdown_item_1line, statusOptions)
            )

            // Pre-select
            statusDisplayMap[item.ApprovedDisapproved]?.let {
                spinnerStatus.setText(it, false)
            }

            spinnerStatus.setOnItemClickListener { _, _, position, _ ->
                item.ApprovedDisapproved       = statusSubmitMap[statusOptions[position]]
                item.ApprovedDisapprovedRemarks = txtRemark.text?.toString()?.trim()
                txtLayStatus.error = null   // clear error on valid selection
            }

            // ── Submit — only validates UI, then fires callback ──────────────
            btnSubmit.setSafeOnClickListener {
                item.ApprovedDisapprovedRemarks = txtRemark.text?.toString()?.trim()

                if (item.ApprovedDisapproved.isNullOrEmpty()) {
                    txtLayStatus.error = root.context.getString(R.string.validation_please_select_a_status)
                    return@setSafeOnClickListener
                }

                txtLayStatus.error = null
                onSubmitClick(item)  // 🔁 send fully updated item to Fragment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DealerViewHolder(
            RowItemTourVoucherApprovalListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: DealerViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<TourVoucherItem>() {
        override fun areItemsTheSame(oldItem: TourVoucherItem, newItem: TourVoucherItem) =
            oldItem.TravelDt  == newItem.TravelDt  &&
                    oldItem.StartTime == newItem.StartTime  &&
                    oldItem.FromPlace == newItem.FromPlace

        override fun areContentsTheSame(oldItem: TourVoucherItem, newItem: TourVoucherItem) =
            oldItem == newItem
    }
}