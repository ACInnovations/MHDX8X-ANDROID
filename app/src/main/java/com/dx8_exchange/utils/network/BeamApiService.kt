package com.dx8_exchange.utils.network

    import com.yourpackage.model.beam.AccountRequest
    import com.yourpackage.model.beam.AccountResponse
    import com.yourpackage.model.beam.WebhookRequest
    import com.yourpackage.model.beam.WebhookResponse
    import retrofit2.Call
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.Path

    interface BeamApiService {

        // 1. Create an Account
        @POST("accounts/individuals")
        fun createAccount(@Body request: AccountRequest): Call<AccountResponse>

        // 2. Get a Single Account
        @GET("accounts/individuals/{accountId}")
        fun getAccount(@Path("accountId") accountId: String): Call<AccountResponse>

        // 3. Register Webhook
        @POST("webhooks")
        fun registerWebhook(@Body request: WebhookRequest): Call<WebhookResponse>
    }