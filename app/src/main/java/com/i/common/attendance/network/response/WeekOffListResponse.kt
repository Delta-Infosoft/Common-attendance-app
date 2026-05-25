package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class WeekOffListResponse(
    @SerializedName("status")  val status:  String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null
)

data class WeekOffItem(
    @SerializedName("SundayRequestId") val sundayRequestId: String? = null,
    @SerializedName("EmpId")           val empId:           String? = null,
    @SerializedName("EMPName")         val empName:         String? = null,
    @SerializedName("date")            val date:            String? = null,
    @SerializedName("RequestDate")     val requestDate:     String? = null,
    @SerializedName("Reason")          val reason:          String? = null,
    @SerializedName("InsertedOn")      val insertedOn:      String? = null,
    @SerializedName("LastUpdatedOn")   val lastUpdatedOn:   String? = null,
    @SerializedName("Status")          val status:          String? = null
)