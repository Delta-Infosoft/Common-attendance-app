package com.i.common.attendance.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TourAdvanceExpenseResponse (
  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : Any? = null
)

@Parcelize
data class TourAdvanceExpense (

  @SerializedName("EmpId"            )     var EmpId            :        String? = null,
  @SerializedName("Employee Name"     )     var EmployeeName     :        String? = null,
  @SerializedName("AdvanceAmount"    )     var AdvanceAmount    :        String? = null,
  @SerializedName("Remarks"          )     var Remarks          :        String? = null,
  @SerializedName("RequestDt"        )     var RequestDt        :        String? = null,
  @SerializedName("AdvanceExpenseId" )     var AdvanceExpenseId :        String? = null

): Parcelable