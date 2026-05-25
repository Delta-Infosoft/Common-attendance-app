package com.i.common.attendance.ui.home.ledgerreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.CustomerData
import com.i.common.attendance.network.response.DailyTourDealerCategory

class SelectCustomerNameCategoryAdapter(
    private val onItemClick: (CustomerData) -> Unit
) : ListAdapter<CustomerData, SelectCustomerNameCategoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CustomerData) = with(binding) {
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

    class DiffCallback : DiffUtil.ItemCallback<CustomerData>() {
        override fun areItemsTheSame(oldItem: CustomerData, newItem: CustomerData): Boolean {
            return oldItem.LgrId == newItem.LgrId
        }

        override fun areContentsTheSame(oldItem: CustomerData, newItem: CustomerData): Boolean {
            return oldItem == newItem
        }
    }
}