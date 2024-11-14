package com.dx8_exchange.ui.activity

    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import com.yourpackage.model.beam.AccountRequest
    import com.yourpackage.model.beam.WebhookRequest
    import com.yourpackage.utils.network.BeamApiClient
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            createAccount()
            registerWebhook("https://your-webhook-url.com/beam-events")
        }

        private fun createAccount() {
            val request = AccountRequest(userType = "individual")

            BeamApiClient.apiService.createAccount(request).enqueue(object : Callback<AccountResponse> {
                override fun onResponse(call: Call<AccountResponse>, response: Response<AccountResponse>) {
                    if (response.isSuccessful) {
                        val onboardingUrl = response.body()?.onboardingUrl
                        Log.d("BeamAPI", "Onboarding URL: $onboardingUrl")
                        // Load the onboarding URL in a WebView, for example
                    } else {
                        Log.e("BeamAPI", "Account creation failed: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AccountResponse>, t: Throwable) {
                    Log.e("BeamAPI", "Network error: ${t.message}")
                }
            })
        }

        private fun registerWebhook(url: String) {
            val request = WebhookRequest(url = url)

            BeamApiClient.apiService.registerWebhook(request).enqueue(object : Callback<WebhookResponse> {
                override fun onResponse(call: Call<WebhookResponse>, response: Response<WebhookResponse>) {
                    if (response.isSuccessful) {
                        Log.d("BeamAPI", "Webhook registered: ${response.body()?.id}")
                    } else {
                        Log.e("BeamAPI", "Webhook registration failed: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<WebhookResponse>, t: Throwable) {
                    Log.e("BeamAPI", "Network error: ${t.message}")
                }
            })
        }
    }

