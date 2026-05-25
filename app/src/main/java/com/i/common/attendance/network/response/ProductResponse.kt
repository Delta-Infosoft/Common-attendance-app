package com.i.common.attendance.network.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("status")  val status:  String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("result")  val result:  Any?    = null  // ✅ Fix: Any? instead of List<ProductModel>
)

// ✅ ProductModel no changes needed
data class ProductModel(
    @SerializedName("ProductId")   val productId:   String? = null,
    @SerializedName("ProductName") val productName: String? = null
)