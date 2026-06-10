package com.i.common.attendance.ui.home.dealerwisereport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.DealerDetailsAccount

class SelectCustomerNameCategoryAdapter(
    private val onItemClick: (DealerDetailsAccount) -> Unit
) : ListAdapter<DealerDetailsAccount, SelectCustomerNameCategoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DealerDetailsAccount) = with(binding) {
            txtViewStatus.text = data.DealerName

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

    class DiffCallback : DiffUtil.ItemCallback<DealerDetailsAccount>() {
        override fun areItemsTheSame(oldItem: DealerDetailsAccount, newItem: DealerDetailsAccount): Boolean {
            return oldItem.DealerId == newItem.DealerId
        }

        override fun areContentsTheSame(oldItem: DealerDetailsAccount, newItem: DealerDetailsAccount): Boolean {
            return oldItem == newItem
        }
    }
}