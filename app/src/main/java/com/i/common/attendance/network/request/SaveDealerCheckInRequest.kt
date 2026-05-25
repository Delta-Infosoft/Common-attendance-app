package com.i.common.attendance.network.request

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

data class SaveDealerCheckInRequest(

    val mobileNo:String,
    val dealerCategory: String,
    val dealerCategoryId: String,
    val dealerName: String,
    val dealerId: String,
    val lat: String,
    val long: String,
    val remarks: String,
    val checkInTime: String,
    val checkOutTime: String,

    // GPS Photo
    val photoUri: Uri? = null
) {

    fun toMultipartBody(context: Context): MultipartBody {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("DealerCategory", dealerCategory)
        builder.addFormDataPart("MobileNo", mobileNo)
        builder.addFormDataPart("DealerCategoryId", dealerCategoryId)
        builder.addFormDataPart("DealerName", dealerName)
        builder.addFormDataPart("DealerId", dealerId)
        builder.addFormDataPart("Lat", lat)
        builder.addFormDataPart("Long", long)
        builder.addFormDataPart("Remarks", remarks)
        builder.addFormDataPart("InTime", checkInTime)
        builder.addFormDataPart("OutTime", checkOutTime)
        // Upload GPS Photo
        photoUri?.let { uri ->
            val file = copyUriToCacheSafe(context = context, uri = uri)
            val mimeType = when {
                uri.toString().contains(".jpg", true) -> "image/jpeg"
                uri.toString().contains(".jpeg", true) -> "image/jpeg"
                uri.toString().contains(".png", true) -> "image/png"
                else -> context.contentResolver.getType(uri) ?: "image/jpeg"
            }
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            builder.addFormDataPart("PhotoPath", file.name, requestBody)
        }
        return builder.build()
    }

    @Throws(IOException::class)
    private fun copyUriToCacheSafe(context: Context, uri: Uri): File {

        // file://
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            val file = File(uri.path!!)
            if (file.exists() && file.length() > 0) {
                return file
            }
            throw IOException("Invalid file URI")
        }

        // content://
        val resolver = context.contentResolver
        val fileName = resolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->

            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) {
                cursor.getString(index)
            } else {
                null
            }
        }

        val extension = fileName
            ?.substringAfterLast('.', "")
            ?.takeIf { it.length <= 5 }
            ?.let { ".$it" }
            ?: guessExtension(resolver.getType(uri))

        val outputFile = File(
            context.cacheDir,
            "dealer_checkin_${System.currentTimeMillis()}$extension"
        )

        resolver.openInputStream(uri)?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output, DEFAULT_BUFFER_SIZE)
            }

        } ?: throw IOException("Unable to open input stream")

        if (!outputFile.exists() || outputFile.length() == 0L) {
            throw IOException("Copied file is empty")
        }

        return outputFile
    }

    private fun guessExtension(mimeType: String?): String {
        return when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> ".jpg"
        }
    }
}