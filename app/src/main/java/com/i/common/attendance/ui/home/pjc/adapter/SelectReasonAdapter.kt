package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.ReasonList

class SelectReasonAdapter(
    private val onItemClick: (ReasonList) -> Unit
) : ListAdapter<ReasonList, SelectReasonAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ReasonList) = with(binding) {
            txtViewStatus.text = data.ReasonName

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

    class DiffCallback : DiffUtil.ItemCallback<ReasonList>() {
        override fun areItemsTheSame(oldItem: ReasonList, newItem: ReasonList): Boolean {
            return oldItem.ReasonName == newItem.ReasonName
        }

        override fun areContentsTheSame(oldItem: ReasonList, newItem: ReasonList): Boolean {
            return oldItem == newItem
        }
    }
}