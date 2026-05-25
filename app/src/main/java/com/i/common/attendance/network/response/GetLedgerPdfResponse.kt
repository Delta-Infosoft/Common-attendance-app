package com.i.common.attendance.network.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class GetLedgerPdfResponse(
    @SerializedName("status"  ) var status  : String?           = null,
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("result") var result: JsonElement? = null)

data class LedgerPdfData(
    @SerializedName("TransactionDt"       ) var TransactionDt       : String? = null,
    @SerializedName("TransactionTypeName" ) var TransactionTypeName : String? = null,
    @SerializedName("TransactionNo"       ) var TransactionNo       : String? = null,
    @SerializedName("RefNo"               ) var RefNo               : String? = null,
    @SerializedName("Dr_Amt"              ) var DrAmt               : String? = null,
    @SerializedName("Cr_Amt"              ) var CrAmt               : String? = null,
    @SerializedName("LgrName"             ) var LgrName             : String? = null,
    @SerializedName("CrLgrGUID"           ) var CrLgrGUID           : String? = null,
    @SerializedName("Refdt"               ) var Refdt               : String? = null,
    @SerializedName("Narration"           ) var Narration           : String? = null,
    @SerializedName("Narration1"          ) var Narration1          : String? = null,
    @SerializedName("TransactionRowNo"    ) var TransactionRowNo    : String? = null,
    @SerializedName("TransactionId"       ) var TransactionId       : String? = null,
    @SerializedName("TransactionLnId"     ) var TransactionLnId     : String? = null,
    @SerializedName("InsertedOn"          ) var InsertedOn          : String? = null,
    @SerializedName("SrNo"                ) var SrNo                : String? = null,
    @SerializedName("LastUpdateOn"        ) var LastUpdateOn        : String? = null,
    @SerializedName("UserName"            ) var UserName            : String? = null,
    @SerializedName("OT"                  ) var OT                  : String? = null,
    @SerializedName("StreetAddress"       ) var StreetAddress       : String? = null,
    @SerializedName("OthNarratoin"        ) var OthNarratoin        : String? = null,
    @SerializedName("FormType"            ) var FormType            : String? = null,
    @SerializedName("LastUpdatedByUser"   ) var LastUpdatedByUser   : String? = null,
    @SerializedName("RefTransactionDt"    ) var RefTransactionDt    : String? = null,
    @SerializedName("SrNoForDayBook"      ) var SrNoForDayBook      : String? = null,
    @SerializedName("LgrCode"             ) var LgrCode             : String? = null,
    @SerializedName("GSTNo"               ) var GSTNo               : String? = null,
    @SerializedName("ItmNarration"        ) var ItmNarration        : String? = null,
    @SerializedName("LRNo"                ) var LRNo                : String? = null,
    @SerializedName("TransporterName"     ) var TransporterName     : String? = null
)