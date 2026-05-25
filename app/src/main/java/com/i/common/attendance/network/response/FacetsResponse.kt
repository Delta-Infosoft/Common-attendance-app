package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class FacetsResponse(
    val status: String,
    val result: List<FacetsItem>?
)

data class FacetsItem(
    @SerializedName("FacetText") val facetText: String
)