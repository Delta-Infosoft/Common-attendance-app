package com.i.common.attendance.ui.home.dailytour.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.DailyTourDistrict

class SelectDailyTourDistrictAdapter(
    private val onItemClick: (DailyTourDistrict) -> Unit
) : ListAdapter<DailyTourDistrict, SelectDailyTourDistrictAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DailyTourDistrict) = with(binding) {
            txtViewStatus.text = data.District

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

    class DiffCallback : DiffUtil.ItemCallback<DailyTourDistrict>() {
        override fun areItemsTheSame(oldItem: DailyTourDistrict, newItem: DailyTourDistrict): Boolean {
            return oldItem.District == newItem.District
        }

        override fun areContentsTheSame(oldItem: DailyTourDistrict, newItem: DailyTourDistrict): Boolean {
            return oldItem == newItem
        }
    }
}