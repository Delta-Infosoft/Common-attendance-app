package com.i.common.attendance.network.response

data class SelectPortfolioResponse(
    val status: String?,
    val message: String?,
    val result: List<SelectPortfolioModel>?
)

data class SelectPortfolioModel(
    val companyName: String?,
    val city: String?,
    val contactPersonName: String?,
    val contactPersonMobileNo: String?,
    val contactPersonEmailId: String?,
    val remarks: String?,
    val photoPathShow: String?
)
