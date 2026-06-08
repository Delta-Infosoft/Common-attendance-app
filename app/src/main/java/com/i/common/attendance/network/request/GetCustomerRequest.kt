package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetCustomerRequest(
    val customerName: String = "",
    val districtId: String,
    val cityId: String = "",
    val empId: String ?=null,
) {

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("CustomerName", customerName)
            .addFormDataPart("DistrictId", districtId)
            .addFormDataPart("CityId", cityId)

        empId?.let {
            builder.addFormDataPart("EmpId", it)
        }

        return builder.build()
    }
}