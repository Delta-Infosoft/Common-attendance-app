package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetVoucherNoDukeRequest(
    val date: String, val voucherTypeId: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Date", date)
            .addFormDataPart("VoucherTypeId", voucherTypeId)
            .build()
    }
}