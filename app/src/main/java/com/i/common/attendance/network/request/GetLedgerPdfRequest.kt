package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class GetLedgerPdfRequest(
    val lgrId: String,
    val divisionId: String,
    val branchName: String,
    val fromDt: String,
    val toDt: String,
    val showPdf: String? = null
) {

    fun toMultipartBody(): MultipartBody {

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("LgrId", lgrId)
            .addFormDataPart("DivisionId", divisionId)
            .addFormDataPart("BranchName", branchName)
            .addFormDataPart("FromDt", fromDt)
            .addFormDataPart("ToDt", toDt)

        showPdf?.takeIf { it.isNotBlank() }?.let {
            builder.addFormDataPart("ShowPDF", it)
        }

        return builder.build()
    }
}