package com.dx8_exchange.model.wallet

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class VPairToken:Parcelable {
    @field:SerializedName("secretType")
    var secretType: String? =null
    @field:SerializedName("symbol")
    var symbol: String? =null
    @field:SerializedName("tokenAddress")
    var tokenAddress: String? =null
}