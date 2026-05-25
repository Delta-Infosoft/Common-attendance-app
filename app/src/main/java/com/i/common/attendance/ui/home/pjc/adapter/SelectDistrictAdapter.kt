package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.GetDistrictPjcList

class SelectDistrictAdapter(
    private val onItemClick: (GetDistrictPjcList) -> Unit
) : ListAdapter<GetDistrictPjcList, SelectDistrictAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GetDistrictPjcList) = with(binding) {
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

    class DiffCallback : DiffUtil.ItemCallback<GetDistrictPjcList>() {
        override fun areItemsTheSame(oldItem: GetDistrictPjcList, newItem: GetDistrictPjcList): Boolean {
            return oldItem.DistrictId == newItem.DistrictId
        }

        override fun areContentsTheSame(oldItem: GetDistrictPjcList, newItem: GetDistrictPjcList): Boolean {
            return oldItem == newItem
        }
    }
}