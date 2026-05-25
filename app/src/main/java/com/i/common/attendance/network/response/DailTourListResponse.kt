package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class DailTourListResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("result") var result: Any? = null
)

@Parcelize
data class DailTourList(
    @SerializedName("Date") var Date: String? = null,
    @SerializedName("StartTime") var StartTime: String? = null,
    @SerializedName("EndTime") var EndTime: String? = null,
    @SerializedName("DealerName") var DealerName: String? = null,
    @SerializedName("DealerCategory") var DealerCategory: String? = null,
    @SerializedName("FromPlace") var FromPlace: String? = null,
    @SerializedName("ToPlace") var ToPlace: String? = null,
    @SerializedName("Area") var Area: String? = null,
    @SerializedName("Name") var Name: String? = null,
    @SerializedName("MobileNo") var MobileNo: String? = null,
    @SerializedName("Remarks") var Remarks: String? = null,

    // Flotech added field

    @SerializedName("WklyTourDetailId") var WklyTourDetailId: String? = null,
    @SerializedName("EmpId") var EmpId: String? = null,
    @SerializedName("TotalDays") var TotalDays: String? = null,
    @SerializedName("Dt") var Dt: String? = null,
    @SerializedName("TypeTextListId") var TypeTextListId: String? = null,
    @SerializedName("TypeTextListLnId") var TypeTextListLnId: String? = null,
    @SerializedName("Place") var Place: String? = null,
    @SerializedName("InsertedOn") var InsertedOn: String? = null,
    @SerializedName("LastUpdatedOn") var LastUpdatedOn: String? = null,
    @SerializedName("InsertedByUserId") var InsertedByUserId: String? = null,
    @SerializedName("LastUpdatedByUserId") var LastUpdatedByUserId: String? = null,
    @SerializedName("IsJourney") var IsJourney: String? = null,
    @SerializedName("ModeOfTravel") var ModeOfTravel: String? = null,
    @SerializedName("District") var District: String? = null,
    @SerializedName("BusinessCenter") var BusinessCenter: String? = null,
    @SerializedName("MobileNo1") var MobileNo1: String? = null,
    @SerializedName("Remarks1") var Remarks1: String? = null,
    @SerializedName("Name1") var Name1: String? = null,
    @SerializedName("IsPaymentFollowUp") var IsPaymentFollowUp: String? = null,
    @SerializedName("IsOrderFollowUp") var IsOrderFollowUp: String? = null,
    @SerializedName("IsDiscountDiscussion") var IsDiscountDiscussion: String? = null,
    @SerializedName("IsSchemeDiscussion") var IsSchemeDiscussion: String? = null,
    @SerializedName("IsSalesPromotionalActivity") var IsSalesPromotionalActivity: String? = null,
    @SerializedName("IsStockPlanning") var IsStockPlanning: String? = null,
    @SerializedName("IsServiceOrRepairing") var IsServiceOrRepairing: String? = null,
    @SerializedName("PaymentFollowUpDt") var PaymentFollowUpDt: String? = null,
    @SerializedName("OrderFollowUpDt") var OrderFollowUpDt: String? = null,
    @SerializedName("Payment Amount") var PaymentAmount: String? = null,
    @SerializedName("IsAllowpayment") var IsAllowpayment: String? = null,
    @SerializedName("IsOtherfollowup") var IsOtherfollowup: String? = null,
    @SerializedName("AllowpaymentDt") var AllowpaymentDt: String? = null,
    @SerializedName("OtherfollowupDt") var OtherfollowupDt: String? = null,
    @SerializedName("TypeOfFollowup") var TypeOfFollowup: String? = null,
    @SerializedName("TypeOfVisit") var TypeOfVisit: String? = null,
    @SerializedName("Work Summary") var WorkSummary: String? = null,
    @SerializedName("Worksummaryattachment") var Worksummaryattachment: String? = null,
    @SerializedName("Worksummaryattachment1") var Worksummaryattachment1: String? = null
): Parcelable