package com.dx8_exchange.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.*
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dx8_exchange.R
import com.dx8_exchange.databinding.*
import com.dx8_exchange.ui.activity.MainActivity
import com.dx8_exchange.ui.base.BaseFragment
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.ViewClickHandler


class WebViewFragment : BaseFragment(), ViewClickHandler {

    private var binding: FragmentWebviewBinding? = null
    private var url : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            binding = FragmentWebviewBinding.inflate(inflater, container, false)

        }
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        url = arguments?.getString(Const.URL)
        initUI()
        binding!!.webview.settings.domStorageEnabled=true
        binding!!.webview.loadUrl(url!!)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolBar("", false)
        binding!!.titleTV.text = baseActivity!!.getString(R.string.web_view)
        (baseActivity as MainActivity).isShowBottomNavigation(false)
        if(baseActivity!!.isNetworkAvailable){
            binding!!.webview.settings.javaScriptCanOpenWindowsAutomatically = true
            binding!!.webview.settings.javaScriptEnabled = true
            binding!!.webview.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    baseActivity!!.startProgressDialog()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    baseActivity!!.stopProgressDialog()
                    when {
                        url.startsWith(Const.URL_TRANSAK_SUCCESS) -> {
                            showToastOne(baseActivity!!.getString(R.string.transaction_has_been_done_successfully))
                            baseActivity!!.onBackPressedDispatcher.onBackPressed()
                        }
                        url.contains(Const.URL_WYRE_FAILURE) -> {
                            showToastOne(baseActivity!!.getString(R.string.transaction_has_been_failed))
                            baseActivity!!.onBackPressedDispatcher.onBackPressed()
                        }
                    }

                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return false
                }

                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    baseActivity!!.showToastOne(description)
                    baseActivity!!.stopProgressDialog()
                }

                override fun onReceivedSslError(
                    view: WebView,
                    handler: SslErrorHandler,
                    error: SslError
                ) {
                    super.onReceivedSslError(view, handler, error)
                    baseActivity!!.log(error.toString())
                }
            }
            //"250cf6c9-3000-4e8d-8426-f4d81ff74dfb" andrews solutions key
       //     var apikey="6786afc4-dfb3-49c1-a5c3-e63a3fe5beab"
         //   var t_url="https://global-stg.transak.com?apiKey=" + apikey + "&environment=STAGING&themeColor=2575fc" +
        //            "&redirectURL=https://dx8.dekconsulting.com/" +
        //            "&walletAddress=" +  baseActivity!!.store!!.getProfileData()!!.wallet!!.depositAddresses!!.eth
        //    binding!!.webview.loadUrl(Const.TRANSAK_URL +  baseActivity!!.store!!.getProfileData()!!.wallet!!.depositAddresses!!.eth)

        }else{
            binding!!.webview.loadData(
                """<!DOCTYPE html>
            <html>
            <head>
            <title>${baseActivity!!.getString(R.string.errors)}</title>
            </head>
            <body>

            <h1>${baseActivity!!.getString(R.string.no_internet_connection)}</h1>
            <p>${baseActivity!!.getString(R.string.page_cannot_be_loaded)} </p>

            </body>
            </html>""", "text/html", "UTF-8"
            )
        }
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.backArrowIV-> requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

}