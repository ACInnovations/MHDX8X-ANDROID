package com.dx8_exchange.ui.fragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dx8_exchange.R
import com.dx8_exchange.adapter.AdapterCryptoHistoricalData
import com.dx8_exchange.adapter.AdapterCryptoNewsData
import com.dx8_exchange.adapter.AdapterTransaction
import com.dx8_exchange.databinding.FragmentWalletOneBinding
import com.dx8_exchange.model.cryptoDetail.CryptoInfo
import com.dx8_exchange.model.cryptoDetail.HistoricalData
import com.dx8_exchange.model.cryptoDetail.MarketPair
import com.dx8_exchange.model.cryptoDetail.NewsData
import com.dx8_exchange.model.cryptoModel.CryptoData
import com.dx8_exchange.ui.activity.MainActivity
import com.dx8_exchange.ui.base.BaseApp
import com.dx8_exchange.ui.base.BaseFragment
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.ViewClickHandler
import com.dx8_exchange.utils.extensions.checkString
import com.dx8_exchange.utils.extensions.loadGlideImageUrl
import com.dx8_exchange.utils.extensions.replaceFragment
import com.dx8_exchange.utils.extensions.setColor
import com.dx8_exchange.utils.extensions.visibleView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WalletOneFragment : BaseFragment(), ViewClickHandler, OnChartValueSelectedListener {
    private var binding: FragmentWalletOneBinding? = null
    private var adapter: AdapterTransaction? = null
    private var isSaved: Boolean = true
    private var adapterNews: AdapterCryptoNewsData? = null
    private var newsDataList: ArrayList<NewsData> = arrayListOf()
    private var cryptoId: Int? = 0
    private var isLoading = true
    private var adapterCryptoHistoricalData: AdapterCryptoHistoricalData? = null
    private var historicalDataList: ArrayList<HistoricalData> = arrayListOf()
    private var cryptoData : CryptoData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            binding = FragmentWalletOneBinding.inflate(inflater, container, false)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            cryptoData = it.getParcelable( Const.CRYPTO_DATA)!!
            cryptoId = cryptoData?.id
        }
        if (Const.CryptoApi.BASE_URL != "https://pro-api.coinmarketcap.com/") {
            Const.CryptoApi.BASE_URL = "https://pro-api.coinmarketcap.com/"
            updateRestfulClient()
        }
        initUI()
    }

    private fun updateRestfulClient() {
        baseActivity!!.restFullClientCryptoSection =
            (activity?.application as BaseApp).initSyncManagerCrypto()
        baseActivity!!.apiCryptoSection = (activity?.application as BaseApp).getAPICrypto()

        restFullClientCrypto = baseActivity!!.restFullClientCryptoSection
        apiCrypto = baseActivity!!.apiCryptoSection
    }

    private fun getCryptoHistoricalDataApi() {
        val call = apiCrypto!!.apiCryptoHistoricalData(cryptoId!!)
        restFullClientCrypto!!.sendRequest(call, this)
    }
    private fun initUI() {
        baseActivity!!.setToolBar("", false)
        (baseActivity as MainActivity).isShowBottomNavigation(false)
        binding!!.handleClick = this
        adapter = AdapterTransaction(0)
        binding!!.transactionRV.adapter = adapter
        baseActivity!!.getCryptoNewsApi(cryptoId!!)
        binding!!.btcTV.text=cryptoData?.name!!
        binding!!.coinValueTV.text=baseActivity!!.coolNumberFormat(cryptoData?.quote?.usd?.percentChange24h!!)
        binding!!.coinTV.text="$" + "%.2f".format(cryptoData?.quote?.usd?.price!!)
        binding!!.coinIV.loadGlideImageUrl( baseActivity!!.cryptoCurrencyLogo(cryptoId!!),R.drawable.bg_crypto_placeholder)
        binding!!.coinDropDownTV.text=cryptoData!!.symbol + "/USD"
        //binding!!.coinDataTV
        setUpChart()
        getCryptoHistoricalDataApi()
        getTokenHistory()
    }
    fun getTokenHistory(){
        val param = Api3MultipartByte()
        param.put("user", baseActivity!!.store!!.getProfileData()?.id.toString())
        param.put("token", cryptoData?.symbol!!)
        val call = api!!.apiGetTokenHistory(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)
    }
    private fun setUpChart() {
        val xAxis: XAxis = binding!!.graphIV.xAxis
        xAxis.setDrawAxisLine(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.textColor = ContextCompat.getColor(baseActivity!!, R.color.colorCyanLight)
        binding!!.graphIV.axisLeft.isEnabled = true
        binding!!.graphIV.axisLeft.setDrawGridLines(false)
        binding!!.graphIV.xAxis.setDrawGridLines(false)
        binding!!.graphIV.axisRight.isEnabled = false
        binding!!.graphIV.legend.isEnabled = false
        binding!!.graphIV.isDoubleTapToZoomEnabled = false
        binding!!.graphIV.setScaleEnabled(false)
        binding!!.graphIV.description.isEnabled = false
        binding!!.graphIV.contentDescription = ""
        binding!!.graphIV.setNoDataText("No Chart Data")
        binding!!.graphIV.setNoDataTextColor(R.color.darkRed)
        binding!!.graphIV.setOnChartValueSelectedListener(this)

        val yAxis: YAxis = binding!!.graphIV.axisLeft
        yAxis.textColor = ContextCompat.getColor(baseActivity!!, R.color.colorCyanLight)

        binding!!.graphIV.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
            }

            override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
            }

            override fun onChartLongPressed(me: MotionEvent) {}
            override fun onChartDoubleTapped(me: MotionEvent) {}
            override fun onChartSingleTapped(me: MotionEvent) {}
            override fun onChartFling(
                me1: MotionEvent,
                me2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ) {
            }

            override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {}
            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {}
        }

    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.sendIV, R.id.sendTV -> {
                val bundle = Bundle()
                bundle.putInt("SwapAndSend", Const.SEND)
                baseActivity!!.replaceFragment(SendFragment(), bundle)

            }
            R.id.recieveIV, R.id.recieveTV -> {
                baseActivity!!.replaceFragment(PurchaseMethodFragment())
            }
            R.id.buyIV, R.id.buyTV -> {
                val bundle = Bundle()
                bundle.putInt("SwapAndSend", Const.SEND)
                baseActivity!!.replaceFragment(SwapCoinFragment(), bundle)
            }
            R.id.backArrowIV -> baseActivity!!.onBackPressedDispatcher.onBackPressed()
            R.id.withdrawIV, R.id.withdrawTV -> {
                val builder = AlertDialog.Builder(baseActivity!!)
                builder.setTitle("Withdraw")
                builder.setMessage("To Withraw Crypto to your bank account\n Please transfer The crypto to a wallet on an exchange that handles ACH withdrawal")
                builder.setPositiveButton("OK") { dialog, _ ->
                    // Handle OK button click (e.g., dismiss the dialog)
                    dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
                //val bundle = Bundle()
                //bundle.putInt("SwapAndSend", Const.SEND)
                //baseActivity!!.replaceFragment(WithdrawDetailFragment(), bundle)
            }
            R.id.saveToWatchListTV -> {
                when {
                    isSaved -> {
                        binding!!.saveToWatchListTV.text = baseActivity!!.getString(R.string.saved)
                        binding!!.saveToWatchListTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_star_filled, 0)
                        isSaved = false
                    }
                    else -> {
                        binding!!.saveToWatchListTV.text = baseActivity!!.getString(R.string.save_to_watchlist)
                        binding!!.saveToWatchListTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_star_empty, 0)
                        isSaved = true
                    }
                }
            }

            R.id.sellTV -> {
                Toast.makeText(baseActivity, "CURRENTLY NOT FINISHED", Toast.LENGTH_LONG).show()
                /*
                val bundle = Bundle()
                bundle.putInt("ScanAndSend",Const.SEND)
                baseActivity!!.replaceFragment(SendEtherumFragment(), bundle)
                */
            }
        }
    }

    fun updateNewsList(list: ArrayList<NewsData>) {
        newsDataList = list
        setAdapterCryptoNewsData()
    }

    private fun setAdapterCryptoNewsData() {
        adapterNews = AdapterCryptoNewsData(baseActivity!!, newsDataList)
        binding!!.newsDataRV.adapter = adapterNews
    }
    private fun setGraphValue(closePrices: ArrayList<Entry>) {
        val dataSet: LineDataSet = setUpLineDataSet(closePrices)
        val lineData = LineData(dataSet)
        binding!!.graphIV.data = lineData
        binding!!.graphIV.animateX(800)
    }

    private fun setUpLineDataSet(entries: List<Entry?>?): LineDataSet {
        val dataSet = LineDataSet(entries, "Price")
        dataSet.color = ContextCompat.getColor(baseActivity!!, R.color.colorOrange)
        dataSet.fillColor = ContextCompat.getColor(baseActivity!!, R.color.colorOrangeGray)
        dataSet.setDrawHighlightIndicators(false)
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(true)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 2.0f
        dataSet.circleRadius = 1f
        dataSet.highlightLineWidth = 2f
        dataSet.isHighlightEnabled = true
        dataSet.setDrawHighlightIndicators(false)
        return dataSet
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
    }

    override fun onNothingSelected() {
    }

    override fun onSyncFailure(
        errorCode: Int,
        t: Throwable?,
        response: Response<String>?,
        call: Call<String>?,
        callBack: Callback<String>?
    ) {
        super.onSyncFailure(errorCode, t, response, call, callBack)
    }

    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        isLoading = true
        val jsonObject = JSONObject(response!!)
        try {
            when {
                responseUrl.contains(Const.Venly.GET_TOKEN_HISTORY) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            adapter = AdapterTransaction(6)
                            Log.d("RECENTTRANSACTIONS", "onSyncSuccess: " + jsonObject.toString(5))
                        }
                    }
                }

                responseUrl.contains(Const.CryptoApi.API_CRYPTO_HISTORICAL_DATA) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            if (jsonObject.has("data")) {
                                val obj = jsonObject.getJSONObject("data")
                                historicalDataList = Gson().fromJson(
                                    obj.getJSONArray("quotes").toString(),
                                    object : TypeToken<ArrayList<HistoricalData>>() {}.type
                                )
                            }

                            val closePrices: ArrayList<Entry> = ArrayList()
                            for (i in 0 until historicalDataList.size) {
                                closePrices.add(
                                    Entry(
                                        i.toFloat(),
                                        historicalDataList[i].quote!!.usd!!.open!!.toFloat()
                                    )
                                )
                            }
                            setGraphValue(closePrices)


                        }
                    }
                }



            }
        } catch (e: Exception) {
            baseActivity!!.handelException(e)
        }
    }


}