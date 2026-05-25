package com.i.common.attendance.ui.home.tourvoucher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.databinding.RowItemTourVoucherBinding
import com.i.common.attendance.network.response.TourVoucherItem
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.removeTrailingZeros
import com.i.common.attendance.utils.Constants.setSafeOnClickListener

class TourVoucherListAdapter( private val onEditClick: (TourVoucherItem) -> Unit,
                              private val onReportClick: (TourVoucherItem) -> Unit) : ListAdapter<TourVoucherItem, TourVoucherListAdapter.DealerViewHolder>(DiffCallback()) {
    inner class DealerViewHolder(private val binding: RowItemTourVoucherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TourVoucherItem) = with(binding) {
            tvDateRange.text = "${Constants.convertDateFormat(item.TravelDt ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")} → ${Constants.convertDateFormat(item.TravelToDt ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")}"
                tvFromLocation.text = "${item.FromPlace ?: ""} - ${Constants.convertDateFormat(item.StartTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"
                tvToLocation.text = "${item.ToPlace ?: ""} - ${Constants.convertDateFormat(item.EndTime ?: "", "dd-MMM-yyyy hh:mm:ss a", "hh:mm a")}"
            tvBus.text = item.TravellingBy ?: ""
            tvTotal.text = "Total : ${item.TotalExpenses?.removeTrailingZeros() ?: "0"} ₹"
            tvStatus.text = item.Status

            when (item.Status?.equals("Approved", ignoreCase = true)) {
                true -> {
                    tvStatus.setBackgroundResource(R.drawable.bg_shape_rect_round_corner_green)
                    tvEdit.visibility = View.GONE
                }
                false -> {
                    when (item.Status?.equals("Disapproved", ignoreCase = true)) {
                        true -> {
                            tvStatus.setBackgroundResource(R.drawable.roundcornerbgprimary)
                            tvEdit.visibility = View.VISIBLE
                        }
                        else -> {
                            tvStatus.setBackgroundResource(R.drawable.roundcornerbgyellow)
                            tvEdit.visibility = View.VISIBLE
                        }
                    }
                }
                else -> {
                    tvStatus.visibility = View.GONE
                   tvStatus.setBackgroundResource(R.drawable.roundcornerbgyellow)
                    tvEdit.visibility = View.GONE
                }
            }
            tvReport.setSafeOnClickListener {
                onReportClick(item)
            }

            tvEdit.setSafeOnClickListener {
                onEditClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealerViewHolder {
        val binding = RowItemTourVoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DealerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DealerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ---------------- DIFF UTIL ----------------

    class DiffCallback : DiffUtil.ItemCallback<TourVoucherItem>() {

        override fun areItemsTheSame(oldItem: TourVoucherItem, newItem: TourVoucherItem): Boolean {
            // Unique combination for row identity
            return oldItem.TravelDt == newItem.TravelDt &&
                    oldItem.StartTime == newItem.StartTime &&
                    oldItem.FromPlace == newItem.FromPlace        }

        override fun areContentsTheSame(oldItem: TourVoucherItem, newItem: TourVoucherItem): Boolean {
            return oldItem == newItem
        }
    }
}