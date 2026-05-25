package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class TourExpenseTrackingFacetsResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null  // ✅ Fix: Any? instead of ArrayList<TourAgendaTrackingFacets>
)

data class TourAgendaTrackingFacets(
    @SerializedName("FacetsId"    ) var FacetsId    : String? = null,
    @SerializedName("FacetName"   ) var FacetName   : String? = null,
    @SerializedName("CompanyName" ) var CompanyName : String? = null,
    @SerializedName("Reqd"        ) var Reqd        : String? = null,
    @SerializedName("FacetText"   ) var FacetText   : String? = null,
    @SerializedName("LnNo"        ) var LnNo        : String? = null,
    @SerializedName("InsertedOn"  ) var InsertedOn  : String? = null,
    @SerializedName("Para1"       ) var Para1       : String? = null,
    @SerializedName("Para2"       ) var Para2       : String? = null,
    @SerializedName("IsEditable"  ) var IsEditable  : String? = null
)