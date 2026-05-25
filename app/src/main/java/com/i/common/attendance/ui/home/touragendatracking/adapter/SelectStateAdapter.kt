package com.i.common.attendance.ui.home.touragendatracking.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.GetState

class SelectStateAdapter(
    private val onItemClick: (GetState) -> Unit
) : ListAdapter<GetState, SelectStateAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GetState) = with(binding) {
            txtViewStatus.text = data.State

            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }
    override fun submitList(list: List<GetState>?) {
        super.submitList(list)
        Log.d("STATE_ADAPTER", "List size: ${list?.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowItemSelectStatusBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("STATE_ADAPTER", "Binding position: $position -> ${item.State}")
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<GetState>() {
        override fun areItemsTheSame(oldItem: GetState, newItem: GetState): Boolean {
            return oldItem.StateTextListId == newItem.StateTextListId
        }

        override fun areContentsTheSame(oldItem: GetState, newItem: GetState): Boolean {
            return oldItem == newItem
        }
    }
}
