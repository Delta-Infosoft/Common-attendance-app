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

data class SaveTourVoucherRequest(

    val empMobileNo: String,
    val fromDate: String,
    val toDate: String,
    val fromPlace: String,
    val toPlace: String,
    val startTime: String,
    val endTime: String,
    val nightHault: String,
    val travellingBy: String,
    val fareAmt: String,
    val autoCharges: String,
    val autoChargesDetail: String,
    val lodging: String,
    val dailyAllowance: String,
    val otherExpenses: String,
    val otherChargesDetails: String,
    val totalExpenses: String,

    // ✅ Added missing fields
    val expenseId: String,
    val designation: String,
    val departmentId: String,

    // optional image (if required)
    val imageUri: Uri? = null
) {

    fun toMultipartBody(context: Context): MultipartBody {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("EmpMobileNo", empMobileNo)
        builder.addFormDataPart("FromDate", fromDate)
        builder.addFormDataPart("ToDate", toDate)
        builder.addFormDataPart("FromPlace", fromPlace)
        builder.addFormDataPart("ToPlace", toPlace)
        builder.addFormDataPart("StartTime", startTime)
        builder.addFormDataPart("EndTime", endTime)
        builder.addFormDataPart("NightHault", nightHault)
        builder.addFormDataPart("TravellingBy", travellingBy)
        builder.addFormDataPart("FareAmt", fareAmt)
        builder.addFormDataPart("AutoCharges", autoCharges)
        builder.addFormDataPart("AutoChargesDetail", autoChargesDetail)
        builder.addFormDataPart("Lodging", lodging)
        builder.addFormDataPart("DailyAllowance", dailyAllowance)
        builder.addFormDataPart("OtherExpenses", otherExpenses)
        builder.addFormDataPart("OtherChargesDetails", otherChargesDetails)
        builder.addFormDataPart("TotalExpenses", totalExpenses)

        // ✅ Newly added
        builder.addFormDataPart("ExpenseId", expenseId)
        builder.addFormDataPart("Designation", designation)
        builder.addFormDataPart("DepartmentId", departmentId)

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

