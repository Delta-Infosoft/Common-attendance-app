package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class EmployeeDukeResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("result") val result: Any? = null
)

data class EmployeeDataDuke(
    @SerializedName("Name") val name: String?,
    @SerializedName("Team") val team: String?,
    @SerializedName("Designation") val designation: String?,
    @SerializedName("DeptId") val deptId: String?,
    @SerializedName("DeptName") val deptName: String?
)
