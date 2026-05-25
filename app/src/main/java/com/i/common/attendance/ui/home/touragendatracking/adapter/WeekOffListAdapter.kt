package com.i.common.attendance.ui.home.touragendatracking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemWeekOffListDukeBinding
import com.i.common.attendance.network.response.WeekOffItem

class WeekOffListAdapter : ListAdapter<WeekOffItem, WeekOffListAdapter.WeekOffViewHolder>(DiffCallback()) {

    inner class WeekOffViewHolder(private val binding: RowItemWeekOffListDukeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WeekOffItem) = with(binding) {
            tvDate.text = item.date ?: ""
            txtViewReason.text = item.reason ?: ""
            txtViewStatus.text = item.status ?: ""
            when (item.status?.lowercase()) {
                "approved" -> txtViewStatus.setTextColor(
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark)
                )
                "rejected" -> txtViewStatus.setTextColor(
                    ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                )
                else -> txtViewStatus.setTextColor(
                    ContextCompat.getColor(root.context, android.R.color.black)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekOffViewHolder {
        val binding = RowItemWeekOffListDukeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeekOffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekOffViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<WeekOffItem>() {

        override fun areItemsTheSame(oldItem: WeekOffItem, newItem: WeekOffItem): Boolean {
            return oldItem.sundayRequestId == newItem.sundayRequestId
        }

        override fun areContentsTheSame(oldItem: WeekOffItem, newItem: WeekOffItem): Boolean {
            return oldItem == newItem
        }
    }
}