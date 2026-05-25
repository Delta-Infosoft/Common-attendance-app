package com.i.common.attendance.ui.home.carairapproval.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.TravelData

class SelectCityTypeAdapter(
    private val onItemClick: (TravelData) -> Unit
) : ListAdapter<TravelData, SelectCityTypeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TravelData) = with(binding) {
            txtViewStatus.text = data.text

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

    class DiffCallback : DiffUtil.ItemCallback<TravelData>() {
        override fun areItemsTheSame(oldItem: TravelData, newItem: TravelData): Boolean {
            return oldItem.textListId == newItem.textListId
        }

        override fun areContentsTheSame(oldItem: TravelData, newItem: TravelData): Boolean {
            return oldItem == newItem
        }
    }
}