/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 *   @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *  All Rights Reserved.
 *  Proprietary and confidential :  All information contained herein is, and remains
 *  the property of ToXSL Technologies Pvt. Ltd. and its partners.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.dx8_exchange.utils


import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface API {

    /*-----------------------------Authentication Apis--------------------------------*/

//    @GET(Const.Authentication.API_CHECK + "{token}")
//    fun apiCheck(@Path("token") userId: String?): Call<String>


    @GET(Const.Authentication.API_CHECK)
    fun apiCheck(): Call<String>

    @FormUrlEncoded
    @POST(Const.Authentication.API_LOGIN)
    fun apiLogin(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.Orders.GET_ORDER)
    fun apiGetOrder(@Query("id") orderId : String): Call<String>

    @GET(Const.Authentication.API_LOGOUT)
    fun apiLogout(): Call<String>

    @POST(Const.Authentication.GENERATE_OTP)
    fun apiSignUp(@Body serverHashMap: RequestBody): Call<String>

    @FormUrlEncoded
    @POST(Const.Authentication.VARIFY_OTP)
    fun apiVarifyOtp(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.Authentication.RESEND_OTP)
    fun apiResendOtp(@Query("mobile_no") mobile_N0 : String): Call<String>

    @POST(Const.CONTACT_US)
    fun apiContactUS(@Body serverHashMap: RequestBody): Call<String>

    @GET(Const.DELETE_USER)
    fun apiDeleteUser(): Call<String>

    @GET(Const.FLAT_PAGES)
    fun apiAboutUs(@Query("type_id") id: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.Authentication.FORGOT_PASSWORD)
    fun apiForgotPassword(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @POST(Const.Authentication.EDIT_PROFILE)
    fun apiEditProfile(@Body serverHashMap: RequestBody): Call<String>

    /*----------------------------------Crypto Section--------------------------------------*/

    @GET(Const.CryptoApi.API_CRYPTO_INFO)
    fun apiCryptoInfoBySymbol(@Query("symbol") symbol:String): Call<String>


    @GET(Const.CryptoApi.API_TRENDING_CRYPTO)
    fun apiTrendingCrypto(): Call<String>

    @GET(Const.CryptoApi.API_TOP_MOVER_CRYPTO)
    fun apiTopMoverCrypto(): Call<String>

    @GET(Const.CryptoApi.API_CRYPTO_INFO)
    fun apiCryptoInfo(@Query("id") id: Int): Call<String>

    @GET(Const.CryptoApi.API_CRYPTO_HISTORICAL_DATA)
//    @Query("time_period") timePeriod : String, @Query("time_start") timeStart:String, @Query("time_end") timeEnd: String
    fun apiCryptoHistoricalData(@Query("id") id: Int): Call<String>

    @GET(Const.CryptoApi.API_CRYPTO_MARKET)
    fun apiCryptoMarketData(@Query("id") id: Int): Call<String>

    @GET(Const.CryptoApi.API_CRYPTO_NEWS)
    fun apiCryptoNews(@Query("coins") coins: Int,@Query("page") page: Int,@Query("size") size: Int): Call<String>

    @GET(Const.CryptoApi.API_ALL_NEWS)
    fun apiAllNews(@Query("size") size: Int): Call<String>

    /*---------------------------Accounts-----------------------------*/
    @GET(Const.Payment.GENERATE_BANK_TOKEN)
    fun apiGenerateBankToken(): Call<String>

    @FormUrlEncoded
    @POST(Const.Payment.ADD_ACCOUNT)
    fun apiAddAccounts(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.Payment.GET_ACCOUNTS)
    fun apiGetAccounts(): Call<String>
    @POST(Const.Payment.GET_PERM_TOKEN)
    fun apiGetPermanentToken(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.Payment.GET_WALLET)
    fun apiGetWallet(@Query("user") user: Int?):Call <String>
    @GET(Const.Payment.GET_WALLETS)
    fun apiGetWallets(@Query("user") user: Int?):Call <String>

    @FormUrlEncoded
    @POST(Const.Payment.GENERATE_CHECKOUT_URL)
    fun apiGenerateCheckoutUrl(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.Payment.GET_ORDERS)
    fun apiGetOrders(@Query("user") user: Int?):Call <String>

    @POST(Const.Venly.CREATE_WALLET)
    fun apiCreateWallet(@Body serverHashMap: RequestBody):Call <String>


    @POST(Const.Authentication.SET_DID)
    fun apiSetDid(@Body serverHashMap: RequestBody ):Call <String>

    @POST(Const.Venly.PREVIEW_TOKEN_TRANSFER)
    fun apiPreviewTokenTransfer(@Body serverHashMap: RequestBody ): Call<String>
    @POST(Const.Venly.EXECUTE_TOKEN_TRANSFER)
    fun apiExecuteTokenTransfer(@Body serverHashMap: RequestBody ): Call<String>

    @GET(Const.Venly.VERIFY_ADDRESS)
    fun apiVerifyAddress(@Query("secrettype") secrettype: String?,@Query("address") address: String?):Call <String>
    @POST(Const.Venly.GET_TOKEN_HISTORY)
    fun apiGetTokenHistory(@Body serverHashMap: RequestBody ):Call <String>
    @POST(Const.Venly.GET_USERS_HISTORY)
    fun apiGetUsersHistory(@Body serverHashMap: RequestBody ):Call <String>
    @GET(Const.Venly.GET_TOKEN_PAIRS)
    fun apiGetTokenPairs(@Query("user") user : String? ):Call <String>
    @POST(Const.Venly.GET_EXCHANGE_ESTIMATE)
    fun apiGetExchangeEstimate(@Body serverHashMap: RequestBody ):Call <String>
    @POST(Const.Venly.EXECUTE_TOKEN_SWAP)
    fun apiExecuteTokenSwap(@Body serverHashMap: RequestBody ):Call <String>
}