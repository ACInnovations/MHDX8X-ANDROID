package com.toxsl.restfulClient.api

import retrofit2.Call
import retrofit2.Response

interface SyncEventListener {
    fun onSyncStart()
    fun onSyncFinish()
    fun onSyncSuccess(statusCode: Int, message: String, responseUrl: String, responseBody: String?)
    fun onSyncFailure(statusCode: Int, error: Throwable?, response: Response<String>?, call: Call<String>?, callback: retrofit2.Callback<String>?)
}
