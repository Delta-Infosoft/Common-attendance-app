package com.i.common.attendance.ui.home.touragendatracking.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSelectStatusBinding
import com.i.common.attendance.network.response.TourAgendaTrackingDealerName

class SelectDealerNameAdapter(
    private val onItemClick: (TourAgendaTrackingDealerName) -> Unit
) : ListAdapter<TourAgendaTrackingDealerName, SelectDealerNameAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectStatusBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TourAgendaTrackingDealerName) = with(binding) {
            txtViewStatus.text = data.Name

            root.setOnClickListener {
                onItemClick(data)
            }
        }
    }
    override fun submitList(list: List<TourAgendaTrackingDealerName>?) {
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

    class DiffCallback : DiffUtil.ItemCallback<TourAgendaTrackingDealerName>() {
        override fun areItemsTheSame(oldItem: TourAgendaTrackingDealerName, newItem: TourAgendaTrackingDealerName): Boolean {
            return oldItem.DealerId == newItem.DealerId
        }

        override fun areContentsTheSame(oldItem: TourAgendaTrackingDealerName, newItem: TourAgendaTrackingDealerName): Boolean {
            return oldItem == newItem
        }
    }
}
