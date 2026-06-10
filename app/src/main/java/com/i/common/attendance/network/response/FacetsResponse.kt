package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class FacetsResponse(
    val status: String,
    val result: JsonElement?
)

data class FacetsItem(
    @SerializedName("FacetsId"    ) var facetsId    : String? = null,
@SerializedName("FacetName"   ) var facetName   : String? = null,
@SerializedName("Reqd"        ) var reqd        : String? = null,
@SerializedName("FacetText"   ) var facetText   : String? = null,
@SerializedName("CompanyName" ) var companyName : String? = null

)