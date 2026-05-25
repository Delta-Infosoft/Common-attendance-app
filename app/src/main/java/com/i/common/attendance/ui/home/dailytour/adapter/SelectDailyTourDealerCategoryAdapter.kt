package com.i.common.attendance.ui.home.dailytour.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.DailyTourDealerCategory

class SelectDailyTourDealerCategoryAdapter(
    private val onItemClick: (DailyTourDealerCategory) -> Unit
) : ListAdapter<DailyTourDealerCategory, SelectDailyTourDealerCategoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DailyTourDealerCategory) = with(binding) {
            txtViewStatus.text = data.Text

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

    class DiffCallback : DiffUtil.ItemCallback<DailyTourDealerCategory>() {
        override fun areItemsTheSame(oldItem: DailyTourDealerCategory, newItem: DailyTourDealerCategory): Boolean {
            return oldItem.TextListId == newItem.TextListId
        }

        override fun areContentsTheSame(oldItem: DailyTourDealerCategory, newItem: DailyTourDealerCategory): Boolean {
            return oldItem == newItem
        }
    }
}