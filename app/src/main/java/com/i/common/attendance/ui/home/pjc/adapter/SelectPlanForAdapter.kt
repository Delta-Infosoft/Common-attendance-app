package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.PlanForList

class SelectPlanForAdapter(
    private val onItemClick: (PlanForList) -> Unit
) : ListAdapter<PlanForList, SelectPlanForAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PlanForList) = with(binding) {
            txtViewStatus.text = data.PlanFor

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

    class DiffCallback : DiffUtil.ItemCallback<PlanForList>() {
        override fun areItemsTheSame(oldItem: PlanForList, newItem: PlanForList): Boolean {
            return oldItem.PlanForTextListId == newItem.PlanForTextListId
        }

        override fun areContentsTheSame(oldItem: PlanForList, newItem: PlanForList): Boolean {
            return oldItem == newItem
        }
    }
}
