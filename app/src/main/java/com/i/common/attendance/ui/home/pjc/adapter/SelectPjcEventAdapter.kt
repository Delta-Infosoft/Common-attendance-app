package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.R
import com.i.common.attendance.databinding.RowItemSelectPjcEventBinding
import com.i.common.attendance.network.response.PjcEventDto

class SelectPjcEventAdapter : ListAdapter<PjcEventDto, SelectPjcEventAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSelectPjcEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PjcEventDto) = with(binding) {
            txtViewStationLabel.text = root.context.getString(
                R.string.place_holder_pjc_event_station,
                data.Place.orEmpty()
            )

            txtViewAgendaLabel.text = root.context.getString(
                R.string.place_holder_pjc_event_agenda,
                data.Notes.orEmpty()
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowItemSelectPjcEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<PjcEventDto>() {
        override fun areItemsTheSame(
            oldItem: PjcEventDto,
            newItem: PjcEventDto
        ): Boolean {
            return oldItem.PJCLnId == newItem.PJCLnId
        }

        override fun areContentsTheSame(
            oldItem: PjcEventDto,
            newItem: PjcEventDto
        ): Boolean {
            return oldItem == newItem
        }
    }
}