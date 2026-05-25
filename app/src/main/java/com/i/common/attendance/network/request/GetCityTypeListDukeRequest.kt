package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetCityTypeListDukeRequest(
    val cityId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("CityId", cityId)
            .build()
    }
}