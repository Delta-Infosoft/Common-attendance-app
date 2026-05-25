package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class GetInOutDetailsResponse(
    @SerializedName("status")  var status  : String? = null,
    @SerializedName("message") var message : String? = null,
    @SerializedName("result")  var result:  Any?    = null
)

data class InOutRecords(
    @SerializedName("AutoId")   var AutoId   : String? = null,
    @SerializedName("DealerId") var DealerId : String? = null,
    @SerializedName("EmpId")    var EmpId    : String? = null,
    @SerializedName("InTime")   var InTime   : String? = null,
    @SerializedName("OutTime")  var OutTime  : String? = null,
    @SerializedName("MobileNo") var MobileNo : String? = null
)