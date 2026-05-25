package com.i.common.attendance.ui.home.tourvoucher.viewmodel

import com.i.common.attendance.network.response.UploadAttachmentItem

enum class AttachmentType(val value: String) {
    PHOTO("Photo"),
    AUDIO("Audio"),
    FILE("File")
}

sealed class AttachmentState {
    object Loading : AttachmentState()

    data class Success(
        val type: AttachmentType,
        val list: List<UploadAttachmentItem>
    ) : AttachmentState()

    data class Empty(
        val type: AttachmentType,
        val message: String
    ) : AttachmentState()

    data class Error(
        val type: AttachmentType,
        val message: String
    ) : AttachmentState()
}