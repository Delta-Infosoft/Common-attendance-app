package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class TourAgendaTrackingObjectiveResponse(
    @SerializedName("status")  val status:  String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null
)

data class TourAgendaTrackingObjectiveItem(
    @SerializedName("RoadMapId")        val roadMapId:        String? = null,
    @SerializedName("Objective")        val objective:        String? = null,
    @SerializedName("BriefReport")      val briefReport:      String? = null,
    @SerializedName("Status")           val status:           String? = null,
    @SerializedName("BusiCenter")       val busiCenter:       String? = null,
    @SerializedName("InsertedOn")       val insertedOn:       String? = null,
    @SerializedName("CurrentYearSales") val currentYearSales: String? = null,
    @SerializedName("Budget")           val budget:           String? = null
)