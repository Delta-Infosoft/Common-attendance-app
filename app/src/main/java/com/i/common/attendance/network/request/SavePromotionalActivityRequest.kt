package com.i.common.attendance.network.request

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.io.copyTo
import kotlin.io.use
import kotlin.jvm.Throws
import kotlin.let
import kotlin.takeIf
import kotlin.text.substringAfterLast

data class SavePromotionalActivityRequest(

    val month: String,
    val activityName: String,
    val dealerCategoryId: String,
    val dealerName: String,
    val dealerId: String,
    val districtId: String,
    val cityId: String,
    val approxExpense: String,
    val userId : String,
    val empId : String,

    // optional image (if required)
    val imageUri: Uri? = null
) {

    fun toMultipartBody(context: Context): MultipartBody {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("Month", month)
        builder.addFormDataPart("ActivityName", activityName)
        builder.addFormDataPart("DealerCategoryId", dealerCategoryId)
        builder.addFormDataPart("DealerName", dealerName)
        builder.addFormDataPart("DealerId", dealerId)
        builder.addFormDataPart("DistrictId", districtId)
        builder.addFormDataPart("CityId", cityId)
        builder.addFormDataPart("ApproxExpense", approxExpense)
        builder.addFormDataPart("UserId", userId)
        builder.addFormDataPart("EmpId", empId)
        imageUri?.let { uri ->
            val file = copyUriToCacheSafe(context, uri)
            val mimeType = when {

                uri.toString().contains(".jpg", true) -> "image/jpeg"

                uri.toString().contains(".jpeg", true) -> "image/jpeg"

                uri.toString().contains(".png", true) -> "image/png"

                else -> context.contentResolver.getType(uri) ?: "image/jpeg"
            }

            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

            builder.addFormDataPart(
                "PhotoPath",
                file.name,
                requestBody
            )
        }

        return builder.build()
    }
    @Throws(IOException::class)
    private fun copyUriToCacheSafe(context: Context, uri: Uri): File {

        // -------- CASE 1: file:// --------
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            val file = File(uri.path!!)
            if (file.exists() && file.length() > 0) {
                return file
            }
            throw IOException("Invalid file URI")
        }

        // -------- CASE 2: content:// --------
        val resolver = context.contentResolver
        val cacheDir = context.cacheDir

        val fileName = resolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0) cursor.getString(index) else null
        }

        val extension = fileName
            ?.substringAfterLast('.', "")
            ?.takeIf { it.length <= 5 }
            ?.let { ".$it" }
            ?: guessExtension(resolver.getType(uri))

        val outputFile = File(
            cacheDir,
            "tour_${System.currentTimeMillis()}$extension"
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

    fun guessExtension(mimeType: String?): String {
        return when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "audio/mpeg" -> ".mp3"
            "audio/mp4" -> ".m4a"
            "audio/aac" -> ".aac"
            "audio/wav" -> ".wav"
            "application/pdf" -> ".pdf"
            else -> ".bin"
        }
    }
}

