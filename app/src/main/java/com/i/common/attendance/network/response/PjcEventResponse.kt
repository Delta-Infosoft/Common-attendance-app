package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import kotlinx.android.parcel.Parcelize

data class PjcEventResponse(
    val status: String,
    val message: String,
    val result: JsonElement?
)

@Parcelize
data class PjcEventFullData(
    val pjcEvents: List<PjcEventDto>,
    val paymentFollowUps: List<PaymentFollowUpDto>,
    val orderFollowUps: List<PartyRemarkDto>,
    val newDealerAppointmentFollowUps: List<PartyRemarkDto>,
    val subDealerVisitFollowUps: List<PartyRemarkDto>,
    val newDealerSurvey: List<PartyRemarkDto>,
) : Parcelable

@Parcelize
data class PjcEventDto(
    val Notes: String?,
    val Place: String?,
    val PJCId: String?,
    val PJCLnId: String?
) : Parcelable

@Parcelize
data class PaymentFollowUpDto(
    val PartyName: String?,
    val Remarks: String?,
    val PaymentFollowUpAmount: String?
): Parcelable

@Parcelize
data class PartyRemarkDto(
    val PartyName: String?,
    val Remarks: String?
): Parcelable