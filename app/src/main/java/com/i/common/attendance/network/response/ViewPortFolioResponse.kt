    package com.i.common.attendance.network.response

    import android.os.Parcelable
    import com.google.gson.annotations.SerializedName
    import kotlinx.parcelize.Parcelize

    data class ViewPortFolioResponse(
        val status: String?,
        val message: String?,
        val result: List<ViewPortFolioModel>? = null
        //val result: List<ViewPortFolioModel>?
    )

    @Parcelize
    data class ViewPortFolioModel(
        @SerializedName("PortfolioId"           ) var PortfolioId           : String? = null,
        @SerializedName("Dt"                    ) var Dt                    : String? = null,
        @SerializedName("CompanyName"           ) var CompanyName           : String? = null,
        @SerializedName("City"                  ) var City                  : String? = null,
        @SerializedName("ContactPersonName"     ) var ContactPersonName     : String? = null,
        @SerializedName("ContactPersonMobileNo" ) var ContactPersonMobileNo : String? = null,
        @SerializedName("ContactPersonEmailId"  ) var ContactPersonEmailId  : String? = null,
        @SerializedName("Lat"                   ) var Lat                   : String? = null,
        @SerializedName("Long"                  ) var Long                  : String? = null,
        @SerializedName("Remarks"               ) var Remarks               : String? = null,
        @SerializedName("PhotoPath"             ) var PhotoPath             : String? = null,
        @SerializedName("PhotoPathShow"         ) var PhotoPathShow             : String? = null,
        @SerializedName("FilePathShow"         ) var FilePathShow             : String? = null,
        @SerializedName("InsertedOn"            ) var InsertedOn            : String? = null,
        @SerializedName("LastUpdatedOn"         ) var LastUpdatedOn         : String? = null,
        @SerializedName("InsertedByUserId"      ) var InsertedByUserId      : String? = null,
        @SerializedName("LastUpdatedByUserId"   ) var LastUpdatedByUserId   : String? = null,
        @SerializedName("PhotoPath1"            ) var PhotoPath1            : String? = null,
        @SerializedName("PhotoPath2"            ) var PhotoPath2            : String? = null,
        @SerializedName("PhotoPath3"            ) var PhotoPath3            : String? = null,
        @SerializedName("PhotoPath4"            ) var PhotoPath4            : String? = null,
        @SerializedName("UsersName"             ) var UsersName             : String? = null
    ) : Parcelable