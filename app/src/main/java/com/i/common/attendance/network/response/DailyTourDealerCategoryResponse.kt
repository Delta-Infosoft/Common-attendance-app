package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DailyTourDealerCategoryResponse (
  @SerializedName("status") var status: String? = null,
  @SerializedName("message") var message: String? = null,
  @SerializedName("result") var result: List<DailyTourDealerCategory>? = null
)
@Parcelize
data class DailyTourDealerCategory (
  @SerializedName("TextListId") var TextListId: String? = null,
  @SerializedName("Text") var Text: String? = null
): Parcelable
