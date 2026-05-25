package com.i.common.attendance.ui.home.attendancereport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.MonthList

class SelectMonthAdapter(
    private val onItemClick: (MonthList) -> Unit
) : ListAdapter<MonthList, SelectMonthAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MonthList) = with(binding) {
            txtViewStatus.text = data.Month

            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowItemSelectStatusBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<MonthList>() {
        override fun areItemsTheSame(oldItem: MonthList, newItem: MonthList): Boolean {
            return oldItem.Month == newItem.Month
        }

        override fun areContentsTheSame(oldItem: MonthList, newItem: MonthList): Boolean {
            return oldItem == newItem
        }
    }
}