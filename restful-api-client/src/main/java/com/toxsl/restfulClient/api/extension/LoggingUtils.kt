package com.toxsl.restfulClient.api.extension

import android.util.Log

fun log(tag: String, message: String) {
    Log.d(tag, message)
}

fun handleException(e: Exception) {
    Log.e("ExceptionHandler", e.message, e)
}
