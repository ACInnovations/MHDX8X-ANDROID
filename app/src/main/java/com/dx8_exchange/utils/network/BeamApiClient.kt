package com.dx8_exchange.utils.network

    import okhttp3.OkHttpClient
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

    object BeamApiClient {

        private const val BASE_URL = "https://api.sandbox.ansiblelabs.xyz/"

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.516b26f1-76da-4c3f-9a12-243041585bf5}")
                    .addHeader("accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService: BeamApiService = retrofit.create(BeamApiService::class.java)
    }