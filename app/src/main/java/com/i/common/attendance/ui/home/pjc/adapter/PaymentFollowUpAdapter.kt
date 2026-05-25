package com.i.common.attendance.ui.home.pjc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemPaymentFollowUpBinding
import com.i.common.attendance.network.response.PaymentFollowUpDto

class PaymentFollowUpAdapter : ListAdapter<PaymentFollowUpDto, PaymentFollowUpAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemPaymentFollowUpBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PaymentFollowUpDto) = with(binding) {
            txtViewPartyName.text = data.PartyName.orEmpty()
            txtViewDiscussion.text = data.Remarks.orEmpty()
            txtViewAmount.text = data.PaymentFollowUpAmount.orEmpty()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowItemPaymentFollowUpBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<PaymentFollowUpDto>() {
        override fun areItemsTheSame(
            oldItem: PaymentFollowUpDto,
            newItem: PaymentFollowUpDto
        ): Boolean {
            return oldItem.PartyName == newItem.PartyName &&
                    oldItem.PaymentFollowUpAmount == newItem.PaymentFollowUpAmount
        }

        override fun areContentsTheSame(
            oldItem: PaymentFollowUpDto,
            newItem: PaymentFollowUpDto
        ): Boolean {
            return oldItem == newItem
        }
    }
}
