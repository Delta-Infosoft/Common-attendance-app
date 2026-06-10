package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DealerDetailsAccountResponse (

  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result: JsonElement? = null
)

@Parcelize
data class DealerDetailsAccount (
  @SerializedName("DealerName"  ) var DealerName  : String? = null,
  @SerializedName("DealerId"    ) var DealerId    : String? = null,
  @SerializedName("DealerCode"  ) var DealerCode  : String? = null,
  @SerializedName("DealerGroup" ) var DealerGroup : String? = null
): Parcelable