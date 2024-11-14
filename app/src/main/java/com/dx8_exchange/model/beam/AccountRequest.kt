package com.dx8_exchange.model.beam

    data class AccountRequest(
        val userType: String = "individual"  // or "business" if needed in the future
        // Add other fields as required by Beam’s API
    )