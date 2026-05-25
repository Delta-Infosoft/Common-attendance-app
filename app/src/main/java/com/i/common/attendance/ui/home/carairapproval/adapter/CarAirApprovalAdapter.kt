package com.i.common.attendance.ui.home.carairapproval.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemCarAirApprovalListDukeBinding
import com.i.common.attendance.network.response.CarAirApprovalItem
import com.i.common.attendance.utils.Constants
import com.i.common.attendance.utils.Constants.setSafeOnClickListener

class CarAirApprovalAdapter(
    private val onApproveClick: (CarAirApprovalItem) -> Unit,
    private val onDisapproveClick: (CarAirApprovalItem) -> Unit
) : ListAdapter<CarAirApprovalItem, CarAirApprovalAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: RowItemCarAirApprovalListDukeBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: CarAirApprovalItem) = with(binding){
            fun String?.clean() = this?.replace("u0026", "&")?.replace("u0027", "'") ?: ""
            txtViewEmpId.text = item.name.clean()
            txtDate.text = "${Constants.convertDateFormat(item.date ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")}"
            txtViewEmpName.text = item.designation.clean()
            txtCategoryName.text = item.team.clean()
            txtViewPunchId.text = item.no.clean()
            txtViewPointDiscussion.text = item.department.clean() + " | ₹" + item.totalAmt
            tvFromLocation.text = "${item.fromPlace ?: ""} - ${Constants.convertDateFormat(item.journeyFromDate ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")}"
            tvToLocation.text = "${item.toPlace ?: ""} - ${Constants.convertDateFormat(item.journeyToDate ?: "", "M/d/yyyy hh:mm:ss a", "dd-MMM-yyyy")}"
            btnApprove.setSafeOnClickListener {
                onApproveClick(item)
            }
            btnDisApprove.setSafeOnClickListener {
                onDisapproveClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemCarAirApprovalListDukeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CarAirApprovalItem>() {

        override fun areItemsTheSame(
            oldItem: CarAirApprovalItem,
            newItem: CarAirApprovalItem
        ): Boolean {
            return oldItem.carAirApprovalId == newItem.carAirApprovalId
        }

        override fun areContentsTheSame(
            oldItem: CarAirApprovalItem,
            newItem: CarAirApprovalItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}