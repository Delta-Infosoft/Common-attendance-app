package com.i.common.attendance.ui.home.touragendatracking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemWeekOffApprovalListDukeBinding
import com.i.common.attendance.network.response.SundayRequestItem
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener

class SundayRequestAdapter(
    private val onAction: (item: SundayRequestItem, isApprove: Boolean, position: Int) -> Unit
) : ListAdapter<SundayRequestItem, SundayRequestAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: RowItemWeekOffApprovalListDukeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SundayRequestItem) = with(binding) {
            txtViewEmpId.text = item.empId ?: ""
            txtViewEmpName.text = item.empName ?: ""
            txtViewWeekOffDate.text = "${Constants.convertDateFormat(item.requestDate ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")}"
            txtViewReason.text = item.reason ?: ""

            btnApprove.setSafeOnClickListener {
                onAction(item, true, bindingAdapterPosition)
            }

            btnDisApprove.setSafeOnClickListener {
                onAction(item, false, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemWeekOffApprovalListDukeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<SundayRequestItem>() {

        override fun areItemsTheSame(
            oldItem: SundayRequestItem,
            newItem: SundayRequestItem
        ): Boolean {
            return oldItem.sundayRequestId == newItem.sundayRequestId
        }

        override fun areContentsTheSame(
            oldItem: SundayRequestItem,
            newItem: SundayRequestItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}