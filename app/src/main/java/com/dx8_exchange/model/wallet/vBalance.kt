package com.dx8_exchange.model.wallet

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class vBalance:Parcelable {

    @field:SerializedName("available")
    var available: Boolean? =null
    @field:SerializedName("secretType")
    var secretType: String? =null
    @field:SerializedName("balance")
    var balance: Float? =null
    @field:SerializedName("gasBalance")
    var gasBalance: Float? =null
    @field:SerializedName("symbol")
    var symbol: String? =null
    @field:SerializedName("gasSymbol")
    var gasSymbol: String? =null
    @field:SerializedName("rawBalance")
    var rawBalance: Long? =null
    @field:SerializedName("rawGasBalance")
    var rawGasBalance: Long? =null
    @field:SerializedName("decimals")
    var decimals: Int? =null
}