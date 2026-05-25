package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName


data class GetDistrictUnnatiResponse (

  @SerializedName("status"  ) var status  : String?           = null,
  @SerializedName("message" ) var message : String?           = null,
  @SerializedName("result"  ) var result  : ArrayList<GetDistrictUnnati> = arrayListOf()

)


data class GetDistrictUnnati(

  @SerializedName("DistrictId" ) var DistrictId : String? = null,
  @SerializedName("District"   ) var District   : String? = null,
)