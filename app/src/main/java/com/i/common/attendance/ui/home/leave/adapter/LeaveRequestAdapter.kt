package com.i.common.attendance.ui.home.leave.adapter// LeaveRequestAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.i.common.attendance.databinding.RowItemLeaveApprovalListUnnatiBinding
import com.i.common.attendance.network.response.ViewLeaveUnnatiList
import com.i.common.attendance.utils.Constants

class LeaveRequestAdapter(
    private val showApprovalButtons: Boolean = false,
    private val onApproveClick: (ViewLeaveUnnatiList) -> Unit = {},
    private val onRejectClick: (ViewLeaveUnnatiList) -> Unit = {}
) : ListAdapter<ViewLeaveUnnatiList, LeaveRequestAdapter.LeaveViewHolder>(LeaveRequestDiffUtil()) {

    inner class LeaveViewHolder(
        private val binding: RowItemLeaveApprovalListUnnatiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ViewLeaveUnnatiList) = with(binding) {

            // ── Date range ────────────────────────────────────────────────
            tvDateRange.text =
                "${Constants.convertDateFormat(item.FromDt ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")} → " +
                        Constants.convertDateFormat(item.ToDt ?: "", "dd-MMM-yyyy hh:mm:ss a", "dd-MMM-yyyy")

            // ── Status ────────────────────────────────────────────────────
            tvStatus.text = item.SApprovedDisapproved ?: "-"

            // ── Other details ─────────────────────────────────────────────
            txtViewEmpId.text       = "Emp ID : ${item.EmpId.orEmpty()}"
            txtViewEmpName.text     = "Emp Name : ${item.Employee.orEmpty()}"
            txtViewReason.text      = "Reason : ${item.LeaveReson.orEmpty()}"
            txtViewApprovalBy.text  = "Approval By : ${item.ApprovedDisapprovedBy.orEmpty()}"
            txtViewApprovalOn.text  = "Approval On : ${item.ApprovedDisapprovedOn.orEmpty()}"
            txtViewInsertedOn.text  = "Inserted On : ${item.InsertedOn.orEmpty()}"

            // ── Approve / Reject buttons ──────────────────────────────────
            // Shown only when:
            //   1. The adapter is used inside LeaveApprovalListUnnatiFragment
            //      (showApprovalButtons = true)
            //   AND
            //   2. The leave is still pending approval
            constApproveReject.visibility =
                if (showApprovalButtons /*&& item.ApprovedDisapproved.equals("Pending", true)*/)
                    View.VISIBLE
                else
                    View.GONE

            btnApprove.setOnClickListener { onApproveClick(item) }
            btnReject.setOnClickListener  { onRejectClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = RowItemLeaveApprovalListUnnatiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class LeaveRequestDiffUtil : DiffUtil.ItemCallback<ViewLeaveUnnatiList>() {

    override fun areItemsTheSame(oldItem: ViewLeaveUnnatiList, newItem: ViewLeaveUnnatiList): Boolean =
        oldItem.LeaveRequestId == newItem.LeaveRequestId

    override fun areContentsTheSame(oldItem: ViewLeaveUnnatiList, newItem: ViewLeaveUnnatiList): Boolean =
        oldItem == newItem
}