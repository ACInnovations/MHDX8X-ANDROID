package com.dx8_exchange.model

import java.util.Date

data class VenlyTransactionHistory(
    var createdAt : String,
    var id : String,
    var walletId: String,
    var type: String,
    var status: String,
    var gasPrice: Long,
    var gas: Long,
    var nonce: Long,
    var value: Long,
    var to: String,
    var transationHash: String,
    var originId: String

    )
//{"createdAt":"2024-05-06T02:12:40.917685",
// "transactionRequest":
// {"id":"ca4acca7-72da-4340-bc05-d6b7cb1cc923",
// "walletId":"f4396d5d-05e8-4230-8b7f-6c99222e8904",
// "type":"ETH_TRANSACTION",
// "status":"SUCCEEDED",
// "gasPrice":1461195972,
// "gas":21000,
// "nonce":0,
// "value":7947972718667061,
// "to":"0xF5396423fF3A8e298c6cf2C01b3aA2CBa916DE4c"},
// "transactionHash":"0x4d662390949c2bb6d248996609dbff2318b9e5166ab014f704ea0dc5fb0f2763",
// "originId":"6726caf5-6cff-4c49-8a2c-1d7b8c1c1f56"}