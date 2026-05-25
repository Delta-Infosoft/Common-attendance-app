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
import kotlin.io.copyTo
import kotlin.io.use
import kotlin.jvm.Throws
import kotlin.let
import kotlin.takeIf
import kotlin.text.substringAfterLast

data class InsertVisitRequest(
    val portfolioId : String?=null,
    val companyName: String,
    val city: String,
    val contactPersonName: String,
    val contactPersonMobileNo: String,
    val contactPersonEmailId: String,
    val lat: String,
    val long: String,
    val insertedByUserId: String,
    val remarks: String,
    val imageUri: Uri?
) {

    fun toMultipartBody(context: Context): MultipartBody {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("CompanyName", companyName)
        builder.addFormDataPart("City", city)
        builder.addFormDataPart("ContactPersonName", contactPersonName)
        builder.addFormDataPart("ContactPersonMobileNo", contactPersonMobileNo)
        builder.addFormDataPart("ContactPersonEmailId", contactPersonEmailId)
        builder.addFormDataPart("Lat", lat)
        builder.addFormDataPart("Long", long)
        builder.addFormDataPart("InsertedByUserId", insertedByUserId)
        builder.addFormDataPart("Remarks", remarks)
        portfolioId?.let {
            builder.addFormDataPart("PortfolioId", portfolioId)
        }

        // ✅ Optional image upload (same pattern as your reference)
        imageUri?.let { uri ->

            val file = copyUriToCacheSafe(context, uri)

            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

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