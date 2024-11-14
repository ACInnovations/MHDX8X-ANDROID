/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.toxsl.restfulClient.api

import android.content.Context
import com.toxsl.restfulClient.api.extension.handleException
import com.toxsl.restfulClient.api.extension.log
import com.toxsl.restfulClient.utils.SingletonHolder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.*


/*
* Created by parkash on 19/11/19.
* */

class RestFullClient private constructor(context: Context) {
    private var apiInstance: Retrofit? = null
    private var BASE_URL: String? = null
    private val PREFS_NAME = "RestFullClient"
    private var mContext: Context = context


    companion object : SingletonHolder<RestFullClient, Context>(::RestFullClient)


    fun getRetrofitInstance(BASE_URL: String?): Retrofit {
        this.BASE_URL = BASE_URL
        apiInstance = Retrofit.Builder()
                .baseUrl(BASE_URL!!)
                .client(UnsafeOkHttpClient().getUnsafeOkHttpClient(mContext))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return apiInstance!!
    }


    fun sendRequest(call: Call<String>, mSyncEventListener: SyncEventListener) {
        log("URL", call.request().url.toString())
        mSyncEventListener.onSyncStart()
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                mSyncEventListener.onSyncFinish()
                mSyncEventListener.onSyncFailure(0, t, null, call, this)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                mSyncEventListener.onSyncFinish()
                log("API RESPONSE >>>", "${response.code()}")
//                if (checkAppInMaintenance(response.body().toString(), mSyncEventListener)) return
//                if (checkDateExpire(response.body().toString(), mSyncEventListener)) return

                if (response.isSuccessful) {
                    response.body()?.reader()?.let { log("RestFull Response", it.readText()) }
                    var responseUrl = response.raw().request.url.toString()
                    responseUrl = responseUrl.replace(BASE_URL!!, "")
                    if (responseUrl.contains("?"))
                        responseUrl = responseUrl.substring(0, responseUrl.indexOf("?"))
                    log("url_new", responseUrl)
                  mSyncEventListener.onSyncSuccess(response.code(), response.message(), responseUrl, response.body()
                        ?.reader()
                        ?.readText())
                } else {
                    mSyncEventListener.onSyncFailure(response.code(), null, response, call, this)
                }

            }

        })

    }

    fun checkAppInMaintenance(data: String, mSyncEventListener: SyncEventListener): Boolean {
        try {
            val jsonObject = JSONObject(data)
            if (jsonObject.has("maintainence")) {
                if (jsonObject.getString("maintainence") != "null") {
                    mSyncEventListener.onSyncFailure(
                            0,
                            AppInMaintenance(jsonObject.getString("maintainence")),
                            null,
                            null,
                            null
                    )
                    return true
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
        return false
    }

    private fun checkDateExpire(data: String, mSyncEventListener: SyncEventListener): Boolean {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            val jsonObject = JSONObject(data)
            if (jsonObject.has("datecheck")) {
                val d = dateFormat.parse(jsonObject.getString("datecheck"))
                if (d != null) {
                    cal.time = d
                    val currentcal = Calendar.getInstance()
                    if (currentcal.after(cal)) {
                        mSyncEventListener.onSyncFailure(
                            0,
                            AppExpiredError(jsonObject.getString("datecheck")),
                            null,
                            null,
                            null
                        )
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
        return false
    }

    fun setLoginStatus(loginValid: String?) {
//        if (loginValid != null) {
//            if (loginValid.length != 32)
//                throw RuntimeException("Auth Code is not valid")
//        }
        val settings = mContext.getSharedPreferences(PREFS_NAME, 0)
        val editor = settings.edit()
        editor.putString("access_token", loginValid)
        editor.putLong("time_stamp", System.currentTimeMillis())
        editor.apply()
    }

    fun getLoginStatus(): String? {
        val settings = mContext.getSharedPreferences(PREFS_NAME, 0)
        return settings.getString("access_token", null)
    }
}