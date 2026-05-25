package com.i.common.attendance.network.request

import okhttp3.MultipartBody

data class FieldVisitRequest(
    val date: String,
    val branch: String,
    val empName: String,
    val workType: String,
    val activityPlan: String,
    val customerName: String,
    val district: String,
    val city: String,
    val visitOutcome: String,
    val salesAmount: String,
    val collectionAmount: String,
    val shopPotentialPerYear: String,
    val contactPersonName: String,
    val contactNumber: String
) {
    fun toMultipartBody(): MultipartBody {
        return MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("Date", date)
            .addFormDataPart("Branch", branch)
            .addFormDataPart("EmpName", empName)
            .addFormDataPart("WorkType", workType)
            .addFormDataPart("ActivityPlan", activityPlan)
            .addFormDataPart("CustomerName", customerName)
            .addFormDataPart("District", district)
            .addFormDataPart("City", city)
            .addFormDataPart("VisitOutcome", visitOutcome)
            .addFormDataPart("SalesAmount", salesAmount)
            .addFormDataPart("CollectionAmount", collectionAmount)
            .addFormDataPart("ShopPotentialPerYear", shopPotentialPerYear)
            .addFormDataPart("ContactPersonName", contactPersonName)
            .addFormDataPart("ContactNumber", contactNumber)
            .build()
    }
}