package com.i.common.attendance.ui.home.touragendatracking.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.TourAgendaTrackingSubDealerName

class SelectSubDealerNameAdapter(
    private val onItemClick: (TourAgendaTrackingSubDealerName) -> Unit
) : ListAdapter<TourAgendaTrackingSubDealerName, SelectSubDealerNameAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TourAgendaTrackingSubDealerName) = with(binding) {
            txtViewStatus.text = data.Name

            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }
    override fun submitList(list: List<TourAgendaTrackingSubDealerName>?) {
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
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TourAgendaTrackingSubDealerName>() {
        override fun areItemsTheSame(oldItem: TourAgendaTrackingSubDealerName, newItem: TourAgendaTrackingSubDealerName): Boolean {
            return oldItem.Name == newItem.Name
        }

        override fun areContentsTheSame(oldItem: TourAgendaTrackingSubDealerName, newItem: TourAgendaTrackingSubDealerName): Boolean {
            return oldItem == newItem
        }
    }
}
