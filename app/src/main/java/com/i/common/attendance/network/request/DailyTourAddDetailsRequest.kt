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

data class DailyTourAddDetailsRequest(
    val date: String,
    val startTime: String,
    val endTime: String,
    val fromPlace: String,
    val toPlace: String,
    val typeTextListId: String,
    val dealerName: String,
    val district: String,
    val area: String,
    val businessCenter: String,
    val mobileNo: String,
    val pointDiscussion: String,
    val empMobileNo: String,

    val isPaymentFollowUp: String,
    val isOrderFollowUp: String,
    val isDiscountDiscussion: String,
    val isSchemeDiscussion: String,
    val isSalesPromotionalActivity: String,
    val isStockPlanning: String,
    val isServiceOrRepairing: String,

    val paymentFollowUpDt: String?="",
    val orderFollowUpDt: String?="",
    val paymentFollowUpAmount: String?="",

    val isNewDealerAppointment: String?="",
    val newDealerAppointmentDt: String?="",

    val isSubDealerVisit: String?="",
    val subDealerVisitDate: String?="",

    val isNewDealerSurvey: String?="",
    val newDealerSurveyDate: String?="",

    // optional image (if required)
    val imageUri: Uri? = null
) {

    fun toMultipartBody(context: Context): MultipartBody {

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Date", date)
            .addFormDataPart("StartTime", startTime)
            .addFormDataPart("EndTime", endTime)
            .addFormDataPart("FromPlace", fromPlace)
            .addFormDataPart("ToPlace", toPlace)
            .addFormDataPart("TypeTextListId", typeTextListId)
            .addFormDataPart("DealerName", dealerName)

            .addFormDataPart("District", district)
            .addFormDataPart("Area", area)
            .addFormDataPart("BusinessCenter", businessCenter)
            .addFormDataPart("MobileNo", mobileNo)
            .addFormDataPart("PointDiscussion", pointDiscussion)
            .addFormDataPart("EmpMobileNo", empMobileNo)

            .addFormDataPart("IsPaymentFollowUp", isPaymentFollowUp)
            .addFormDataPart("IsOrderFollowUp", isOrderFollowUp)
            .addFormDataPart("IsDiscountDiscussion", isDiscountDiscussion)
            .addFormDataPart("IsSchemeDiscussion", isSchemeDiscussion)
            .addFormDataPart("IsSalesPromotionalActivity", isSalesPromotionalActivity)
            .addFormDataPart("IsStockPlanning", isStockPlanning)
            .addFormDataPart("IsServiceOrRepairing", isServiceOrRepairing)

            .addFormDataPart("PaymentFollowUpDt", if (paymentFollowUpDt.isNullOrBlank() || paymentFollowUpDt == "null") "" else paymentFollowUpDt)
            .addFormDataPart("OrderFollowUpDt", if (orderFollowUpDt.isNullOrBlank() || orderFollowUpDt == "null") "" else orderFollowUpDt)
            .addFormDataPart("PaymentFollowUpAmount", if (paymentFollowUpAmount.isNullOrBlank() || paymentFollowUpAmount == "null") "" else paymentFollowUpAmount)

            .addFormDataPart("IsNewDealerAppointment", isNewDealerAppointment.toString())
            .addFormDataPart("NewDealerAppointmentDt",  if (newDealerAppointmentDt.isNullOrBlank() || newDealerAppointmentDt == "null") "" else newDealerAppointmentDt)

            .addFormDataPart("IsSubDealerVisit", isSubDealerVisit.toString())
            .addFormDataPart("SubDealerVisitDate", if (subDealerVisitDate.isNullOrBlank() || subDealerVisitDate == "null") "" else subDealerVisitDate)

            .addFormDataPart("IsNewDealerSurvey", isNewDealerSurvey.toString())
            .addFormDataPart("NewDealerSurveyDate", if (newDealerSurveyDate.isNullOrBlank() || newDealerSurveyDate == "null") "" else newDealerSurveyDate)

        imageUri?.let { uri ->

            val file = copyUriToCacheSafe(context, uri)

            val mimeType = context.contentResolver.getType(uri)
                ?: "image/jpeg"

            val requestBody =
                file.asRequestBody(mimeType.toMediaTypeOrNull())

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