package com.i.common.attendance.ui.home.leave.data

data class LeaveRequestData(
    val id: Int,
    val dateRange: String,
    val status: String,
    val empId: String,
    val empName: String,
    val reason: String,
    val approvalBy: String,
    val approvalOn: String,
    val insertedOn: String,
    val showButtons: Boolean = false
)