package com.dx8_exchange.model.cryptoDetail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Exchange : Parcelable {
    @field:SerializedName("id")
    var id: Int? = null

    @field:SerializedName("name")
    var name: String? = null

    @field:SerializedName("slug")
    var slug: String? = null
}