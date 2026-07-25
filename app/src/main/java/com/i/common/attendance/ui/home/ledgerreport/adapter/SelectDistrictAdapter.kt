package com.i.common.attendance.ui.home.ledgerreport.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.GetDistrict

class SelectDistrictAdapter(
    private val onItemClick: (GetDistrict) -> Unit
) : ListAdapter<GetDistrict, SelectDistrictAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GetDistrict) = with(binding) {
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

    class DiffCallback : DiffUtil.ItemCallback<GetDistrict>() {
        override fun areItemsTheSame(oldItem: GetDistrict, newItem: GetDistrict): Boolean {
            return oldItem.DistrictId == newItem.DistrictId
        }

        override fun areContentsTheSame(oldItem: GetDistrict, newItem: GetDistrict): Boolean {
            return oldItem == newItem
        }
    }
}