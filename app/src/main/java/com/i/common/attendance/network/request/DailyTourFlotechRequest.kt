package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class DailyTourFlotechRequest(
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

    val isAllowPayment: String,
    val isOtherFollowUp: String,
    val allowPaymentDt: String?="",
    val otherFollowUpDt: String?=""
) {

    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder()
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

            .addFormDataPart("IsPaymentFollowUp", isPaymentFollowUp.toString())
            .addFormDataPart("IsOrderFollowUp", isOrderFollowUp.toString())
            .addFormDataPart("IsDiscountDiscussion", isDiscountDiscussion.toString())
            .addFormDataPart("IsSchemeDiscussion", isSchemeDiscussion.toString())
            .addFormDataPart("IsSalesPromotionalActivity", isSalesPromotionalActivity.toString())
            .addFormDataPart("IsStockPlanning", isStockPlanning.toString())
            .addFormDataPart("IsServiceOrRepairing", isServiceOrRepairing.toString())

            .addFormDataPart("PaymentFollowUpDt", if (paymentFollowUpDt.isNullOrBlank() || paymentFollowUpDt == "null") "" else paymentFollowUpDt)
            .addFormDataPart("OrderFollowUpDt", if (orderFollowUpDt.isNullOrBlank() || orderFollowUpDt == "null") "" else orderFollowUpDt)
            .addFormDataPart("PaymentFollowUpAmount", if (paymentFollowUpAmount.isNullOrBlank() || paymentFollowUpAmount == "null") "" else paymentFollowUpAmount)

            .addFormDataPart("IsAllowpayment", isAllowPayment.toString())
            .addFormDataPart("IsOtherfollowup", isOtherFollowUp.toString())
            .addFormDataPart("AllowpaymentDt", if (allowPaymentDt.isNullOrBlank() || allowPaymentDt == "null") "" else allowPaymentDt)
            .addFormDataPart("OtherfollowupDt", if (otherFollowUpDt.isNullOrBlank() || otherFollowUpDt == "null") "" else otherFollowUpDt)

            .build()
    }
}