package com.i.common.attendance.ui.home.dailytour.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.DailyTourDealerName

class SelectDailyTourDealerNameAdapter(
    private val onItemClick: (DailyTourDealerName) -> Unit
) : ListAdapter<DailyTourDealerName, SelectDailyTourDealerNameAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DailyTourDealerName) = with(binding) {
            txtViewStatus.text = data.Name

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

    class DiffCallback : DiffUtil.ItemCallback<DailyTourDealerName>() {
        override fun areItemsTheSame(oldItem: DailyTourDealerName, newItem: DailyTourDealerName): Boolean {
            return oldItem.Name == newItem.Name
        }

        override fun areContentsTheSame(oldItem: DailyTourDealerName, newItem: DailyTourDealerName): Boolean {
            return oldItem == newItem
        }
    }
}