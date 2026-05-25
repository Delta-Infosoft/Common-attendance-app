package com.i.common.attendance.ui.home.touragendatracking.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.DistrictTourAgendaTracking

class SelectDistrictAdapter(
    private val onItemClick: (DistrictTourAgendaTracking) -> Unit
) : ListAdapter<DistrictTourAgendaTracking, SelectDistrictAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DistrictTourAgendaTracking) = with(binding) {
            txtViewStatus.text = data.District

            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }
    override fun submitList(list: List<DistrictTourAgendaTracking>?) {
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
        Log.d("STATE_ADAPTER", "Binding position: $position -> ${item.District}")
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<DistrictTourAgendaTracking>() {
        override fun areItemsTheSame(oldItem: DistrictTourAgendaTracking, newItem: DistrictTourAgendaTracking): Boolean {
            return oldItem.District == newItem.District
        }

        override fun areContentsTheSame(oldItem: DistrictTourAgendaTracking, newItem: DistrictTourAgendaTracking): Boolean {
            return oldItem == newItem
        }
    }
}
