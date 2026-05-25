package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetSqlQueryForDropdownParamRequest(val sQLQuery: String, val value: String, val sQLQuery2 : String) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("SQLQuery", sQLQuery)
            .addFormDataPart("Value", value)
            .addFormDataPart("SQLQuery2", sQLQuery2)
            .build()
    }
}