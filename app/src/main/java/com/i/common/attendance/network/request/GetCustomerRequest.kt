package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetCustomerRequest(
    val customerName: String = "",
    val districtId: String,
    val cityId: String = ""
) {

    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("CustomerName", customerName)
            .addFormDataPart("DistrictId", districtId)
            .addFormDataPart("CityId", cityId)
            .build()
    }
}