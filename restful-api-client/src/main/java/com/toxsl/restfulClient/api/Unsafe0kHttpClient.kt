package com.toxsl.restfulClient.api

import android.content.Context
import okhttp3.OkHttpClient

class UnsafeOkHttpClient {
    fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
        // Implement the logic for returning an unsafe OkHttpClient (e.g., for testing only)
        return OkHttpClient.Builder().build()
    }
}