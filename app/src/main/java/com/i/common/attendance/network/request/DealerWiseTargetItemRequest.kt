package com.i.common.attendance.network.request

import com.google.gson.Gson
import okhttp3.MultipartBody

data class DealerWiseTargetItemRequest(
    val Type: String? = null,
    val AvgRate: String? = null,
    val Qty: String? = null,
    val Total: String? = null,
    val Sum: String? = null
)

data class SubmitDealerWiseTargetData(
    val dealerId: String,
    val months: Map<String, List<DealerWiseTargetItemRequest>>
)

data class SubmitDealerWiseTargetRequest(
    val userId: String,
    val data: SubmitDealerWiseTargetData
) {
    fun toMultipartBody(): MultipartBody {
        val gson = Gson()
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("UserId", userId)
            .addFormDataPart("Data", gson.toJson(data))
            .build()
    }
}