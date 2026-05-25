package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class UploadAttachmentResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result") val result: Any? = null
)

data class UploadAttachmentItem(
    @SerializedName("FUId") val fUId: String? = null,
    @SerializedName("LnNo") val lnNo: String? = null,
    @SerializedName("RecordId") val recordId: String? = null,
    @SerializedName("FormName") val formName: String? = null,
    @SerializedName("FilePath") val filePath: String? = null,
    @SerializedName("InsertedOn") val insertedOn: String? = null,
    @SerializedName("LastUpdatedOn") val lastUpdatedOn: String? = null,
    @SerializedName("FileName") val fileName: String? = null,
    @SerializedName("FileType") val fileType: String? = null,
    @SerializedName("AttachmentType") val attachmentType: String? = null,
    @SerializedName("File1") val file1: String? = null
)
