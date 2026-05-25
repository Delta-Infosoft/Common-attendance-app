package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemSubDealerVisitBinding
import com.i.common.attendance.network.response.PartyRemarkDto

class SubDealerVisitAdapter : ListAdapter<PartyRemarkDto, SubDealerVisitAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemSubDealerVisitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PartyRemarkDto) = with(binding) {
            txtViewPartyName.text = data.PartyName.orEmpty()
            txtViewDiscussion.text = data.Remarks.orEmpty()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowItemSubDealerVisitBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<PartyRemarkDto>() {
        override fun areItemsTheSame(
            oldItem: PartyRemarkDto,
            newItem: PartyRemarkDto
        ): Boolean {
            return oldItem.PartyName == newItem.PartyName &&
                    oldItem.Remarks == newItem.Remarks
        }

        override fun areContentsTheSame(
            oldItem: PartyRemarkDto,
            newItem: PartyRemarkDto
        ): Boolean {
            return oldItem == newItem
        }
    }
}
