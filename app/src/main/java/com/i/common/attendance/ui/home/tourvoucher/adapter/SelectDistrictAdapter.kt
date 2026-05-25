package com.i.common.attendance.ui.home.tourvoucher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.NameDropdownItem

class SelectDistrictAdapter(
    private val onItemClick: (NameDropdownItem) -> Unit
) : ListAdapter<NameDropdownItem, SelectDistrictAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: NameDropdownItem) = with(binding) {
            txtViewStatus.text = data.name

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

    class DiffCallback : DiffUtil.ItemCallback<NameDropdownItem>() {
        override fun areItemsTheSame(oldItem: NameDropdownItem, newItem: NameDropdownItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: NameDropdownItem, newItem: NameDropdownItem): Boolean {
            return oldItem == newItem
        }
    }
}
