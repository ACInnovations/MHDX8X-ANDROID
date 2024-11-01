package com.dx8_exchange.model.wallet

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class VTokenPairs: Parcelable {
    @field:SerializedName("from")
    var from: VPairToken? =null
    @field:SerializedName("to")
    var to: VPairToken? =null
}