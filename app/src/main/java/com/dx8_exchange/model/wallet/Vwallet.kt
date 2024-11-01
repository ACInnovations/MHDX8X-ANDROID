package com.dx8_exchange.model.wallet

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Vwallet:Parcelable {
    @field:SerializedName("id")
    var id: String? =null
    @field:SerializedName("address")
    var address: String? =null
    @field:SerializedName("walletType")
    var walletType: String? =null
    @field:SerializedName("secretType")
    var secretType: String? =null
    @field:SerializedName("createdAt")
    var createdAt: String? =null
    @field:SerializedName("archived")
    var archived: String? =null
    @field:SerializedName("description")
    var description: String? =null
    @field:SerializedName("primary")
    var primary: Boolean? =null
    @field:SerializedName("hasCustomPin")
    var hasCustomPin: Boolean? =null
    @field:SerializedName("identifier")
    var identifier: String? =null
    @field:SerializedName("userId")
    var userId: String? =null
    @field:SerializedName("custodial")
    var custodial: Boolean? =null
    @field:SerializedName("balance")
    var balance: vBalance? =null


}