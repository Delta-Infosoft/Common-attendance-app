package com.i.common.attendance.network.response

data class AddTourAdvanceExpenseResponse(
    val status: String?,
    val message: String?,
    val result: ResultData? = null
)

data class ResultData(
    val id: String?
)