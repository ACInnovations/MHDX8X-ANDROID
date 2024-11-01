/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 *   @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *  All Rights Reserved.
 *  Proprietary and confidential :  All information contained herein is, and remains
 *  the property of ToXSL Technologies Pvt. Ltd. and its partners.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 */

@file:Suppress("OverrideDeprecatedMigration")

package com.dx8_exchange.ui.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.*
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dx8_exchange.BuildConfig
import com.dx8_exchange.R
import com.dx8_exchange.model.Data
import com.dx8_exchange.model.TransactionHistory
import com.dx8_exchange.model.UserDetail
import com.dx8_exchange.model.cryptoDetail.NewsData
import com.dx8_exchange.model.cryptoModel.CryptoData
import com.dx8_exchange.snackBar.ActionClickListener
import com.dx8_exchange.snackBar.Snackbar
import com.dx8_exchange.snackBar.SnackbarManager
import com.dx8_exchange.snackBar.SnackbarType
import com.dx8_exchange.ui.activity.LoginSignUpActivity
import com.dx8_exchange.ui.activity.MainActivity
import com.dx8_exchange.ui.activity.SplashActivity
import com.dx8_exchange.ui.fragment.CryptoFragment
import com.dx8_exchange.ui.fragment.CryptoWatchlistFragment
import com.dx8_exchange.ui.fragment.MainHomeFragment
import com.dx8_exchange.ui.fragment.SendEtherumFragment
import com.dx8_exchange.ui.fragment.SendPreviewFragment
import com.dx8_exchange.ui.fragment.TransactionDetailsFragment
import com.dx8_exchange.ui.fragment.TransactionFragment
import com.dx8_exchange.ui.fragment.WalletOneFragment
import com.dx8_exchange.utils.*
import com.dx8_exchange.utils.extensions.visibleView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toxsl.restfulClient.api.AppExpiredError
import com.toxsl.restfulClient.api.AppInMaintenance
import com.toxsl.restfulClient.api.RestFullClient
import com.toxsl.restfulClient.api.SyncEventListener
import cz.msebera.android.httpclient.HttpResponse
import cz.msebera.android.httpclient.HttpStatus
import cz.msebera.android.httpclient.client.HttpClient
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import cz.msebera.android.httpclient.util.EntityUtils
import link.magic.android.Magic

//import link.magic.android.Magic
import org.json.JSONException
import org.json.JSONObject
import org.web3j.protocol.Web3j
import org.web3j.protocol.geth.Geth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow


@Suppress("OverrideDeprecatedMigration")
open class BaseActivity : AppCompatActivity(), SyncEventListener, View.OnClickListener {
    var isLoading: Boolean = true
    private var mLastClickTime: Long = 0
    var restFullClient: RestFullClient? = null
    var api: API? = null
    var inflater: LayoutInflater? = null
    var store: PrefStore? = null
    var permCallback: PermCallback? = null
    private var progressDialog: Dialog? = null
    private var txtMsgTV: TextView? = null
    private var reqCode: Int = 0
    private var networksBroadcast: NetworksBroadcast? = null
    private val networkAlertDialog: AlertDialog? = null
    private var networkStatus: String? = null
    var googleApisHandle: GoogleApisHandle? = null
    private var inputMethodManager: InputMethodManager? = null
    private var failureDailog: android.app.AlertDialog.Builder? = null
    private var failureAlertDialog: android.app.AlertDialog? = null
    private var exit: Boolean = false

    var restFullClientCryptoSection: RestFullClient? = null
    var apiCryptoSection: API? = null
    var pageSize: Int = 20
    lateinit var magicLink: MagicLink
    fun isValidMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val uniqueDeviceId: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager
                .activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //      (applicationContext as BaseApp).magicLink!!// Magic(this, "pk_live_5E1D7C3718FE284B")

     //   magic = (applicationContext as BaseApp).magic
        inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        restFullClient = (application as BaseApp).initSyncManager()
        api = (application as BaseApp).getAPI()

        /*crypto section*/
        restFullClientCryptoSection = (application as BaseApp).initSyncManagerCrypto()
        apiCryptoSection = (application as BaseApp).getAPICrypto()

        this@BaseActivity.overridePendingTransition(
            R.anim.slide_in,
            R.anim.slide_out
        )
        inputMethodManager = this
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        store = PrefStore(this)
        googleApisHandle = GoogleApisHandle.getInstance(this)
        initializeNetworkBroadcast()
        store?.saveString(Const.PrefConst.DEVICE_TOKEN, uniqueDeviceId)
        strictModeThread()
        transitionSlideInHorizontal()
        progressDialog()
        
    //    magicLink.getIdToken()
        failureDailog = android.app.AlertDialog.Builder(this)


    }

    override fun onPause() {
        super.onPause()
        store?.setBoolean(Const.PrefConst.FOREGROUND, false)
    }

    fun initFCM() {
        if (checkPlayServices()) {
            if (restFullClient!!.getLoginStatus() != null) {
                checkApi()
            }
        }
    }

    fun initToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    fun setToolBar(title: String = "", isShowToolbar: Boolean = true, subTitle: String = "", isShowSubTitle:Boolean = false) {
        when (supportActionBar) {
            null -> initToolbar()
        }
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val titleTV = findViewById<TextView>(R.id.titleTV)
        val bottomTitleTV = findViewById<TextView>(R.id.titleBottomTV)

        titleTV.text = title
        if (isShowSubTitle){
            bottomTitleTV.visibleView(true)
            bottomTitleTV.text = subTitle
        }else{
            bottomTitleTV.visibleView(false)
        }
        toolbar?.visibleView(isShowToolbar)
    }

    fun openDrawer() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        drawer.openDrawer(GravityCompat.START)
        hideSoftKeyboard()
    }

    fun closeDrawer() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        drawer.openDrawer(GravityCompat.END)
        hideSoftKeyboard()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setSystemBarTheme(isStatusBarFontDark: Boolean) {

        window.decorView.systemUiVisibility =
            if (isStatusBarFontDark) {
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
    }

    fun updateStatusBarColor(@ColorRes colorId: Int, isStatusBarFontDark: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.deepBlack)
            window.setBackgroundDrawable(ContextCompat.getDrawable(this, colorId))
            setSystemBarTheme(isStatusBarFontDark)
        }
    }

    private fun checkApi() {
        val call = api!!.apiCheck()
        restFullClient!!.sendRequest(call, this)
    }

    fun LogoutApi() {
        val call = api!!.apiLogout()
        restFullClient!!.sendRequest(call, this)
    }


    fun gotoLoginSignUpActivity(str: Int = 0) {
        when (str) {
            Const.REGISTER -> {
                val intent = Intent(this, LoginSignUpActivity::class.java)
                intent.putExtra("keyValue", Const.REGISTER)
                startActivity(intent)
                finish()
            }
            Const.LOGIN -> {
                val intent = Intent(this, LoginSignUpActivity::class.java)
                intent.putExtra("keyValue", Const.LOGIN)
                startActivity(intent)
                finish()
            }
            else -> {
                val intent = Intent(this, LoginSignUpActivity::class.java)
                intent.putExtra("keyValue", Const.LOGIN)
                startActivity(intent)
//                startActivity(Intent(this, LoginSignUpActivity::class.java))
                finish()
            }
        }

    }

    fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun gotoSplashctivity() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    private fun initializeNetworkBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        networksBroadcast = NetworksBroadcast()
        registerReceiver(networksBroadcast, intentFilter)
    }


    private fun unregisterNetworkBroadcast() {
        try {
            when {
                networksBroadcast != null -> {
                    unregisterReceiver(networksBroadcast)
                }
            }
        } catch (e: IllegalArgumentException) {
            networksBroadcast = null
        }

    }

    private fun showNoNetworkDialog(status: String?) {
        networkStatus = status
        when {
            SnackbarManager.currentSnackbar != null -> {
                SnackbarManager.currentSnackbar!!.dismiss()
            }
        }
        SnackbarManager.create(
            Snackbar.with(this)
                .type(SnackbarType.SINGLE_LINE)
                .text(status!!).duration(
                    Snackbar
                        .SnackbarDuration.LENGTH_INDEFINITE
                )
                .actionLabel(getString(R.string.retry_caps))
                .actionListener(object : ActionClickListener {
                    override fun onActionClicked(snackbar: Snackbar) {
                        if (!isNetworkAvailable) {
                            showNoNetworkDialog(networkStatus)
                        } else
                            SnackbarManager.currentSnackbar!!.dismiss()
                    }
                })
        )!!.show()
    }

    fun changeDateFormat(
        dateString: String?,
        sourceDateFormat: String,
        targetDateFormat: String,
    ): String {
        when {
            dateString == null || dateString.isEmpty() -> {
                return ""
            }
            else -> {
                val inputDateFromat = SimpleDateFormat(sourceDateFormat, Locale.getDefault())
                var date = Date()
                try {
                    date = inputDateFromat.parse(dateString)
                } catch (e: ParseException) {
                    when {
                        BuildConfig.DEBUG -> {
                            e.printStackTrace()
                        }
                    }
                }

                val outputDateFormat = SimpleDateFormat(targetDateFormat, Locale.getDefault())
                return outputDateFormat.format(date)
            }
        }

    }

    fun changeDateFormatFromDate(sourceDate: Date?, targetDateFormat: String?): String {
        if (sourceDate == null || targetDateFormat == null || targetDateFormat.isEmpty()) {
            return ""
        }
        val outputDateFormat = SimpleDateFormat(targetDateFormat, Locale.getDefault())
        return outputDateFormat.format(sourceDate)
    }


    @SuppressLint("SimpleDateFormat")
    protected fun checkDate(checkDate: String) {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var serverDate: Date? = null
        try {
            serverDate = dateFormat.parse(checkDate)
            cal.time = serverDate
            val currentcal = Calendar.getInstance()
            if (currentcal.after(cal)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.contact_company_info))
                builder.setTitle(getString(R.string.demo_expired))
                builder.setCancelable(false)
                builder.setNegativeButton(getString(R.string.close)) { _, _ -> exitFromApp() }
                val alert = builder.create()
                alert.show()
            }
        } catch (e: ParseException) {
            handelException(e)
        }

    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        when {
            resultCode != ConnectionResult.SUCCESS -> {
                when {
                    apiAvailability.isUserResolvableError(resultCode) -> {
                        apiAvailability.getErrorDialog(this, resultCode, Const.PLAY_SERVICES_RESOLUTION_REQUEST)!!.show()
                    }
                    else -> {
                        log(getString(R.string.this_device_is_not_supported))
                        finish()
                    }
                }
                return false
            }
            else -> return true
        }
    }

    fun checkPermissions(
        perms: Array<String>,
        requestCode: Int,
        permCallback: PermCallback,
    ): Boolean {
        this.permCallback = permCallback
        this.reqCode = requestCode
        val permsArray = ArrayList<String>()
        var hasPerms = true
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permsArray.add(perm)
                hasPerms = false
            }
        }
        if (!hasPerms) {
            val permsString = arrayOfNulls<String>(permsArray.size)
            for (i in permsArray.indices) {
                permsString[i] = permsArray[i]
            }
            ActivityCompat.requestPermissions(this@BaseActivity, permsString, Const.PERMISSION_ID)
            return false
        } else
            return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permGrantedBool = false
        when (requestCode) {
            Const.PERMISSION_ID -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        showToast(
                            getString(R.string.not_sufficient_permissions)
                                    + getString(R.string.app_name)
                                    + getString(R.string.permissionss)
                        )
                        permGrantedBool = false
                        break
                    } else {
                        permGrantedBool = true
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool) {
                        permCallback!!.permGranted(reqCode)
                    } else {
                        permCallback!!.permDenied(reqCode)
                    }
                }
            }
        }
    }

    @Suppress("OverrideDeprecatedMigration")
    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun exitFromApp() {
        finish()
        finishAffinity()
    }

    fun hideSoftKeyboard(): Boolean {
        try {
            when {
                currentFocus != null -> {
                    inputMethodManager!!.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
                    return true
                }
            }
        } catch (e: Exception) {
            when {
                BuildConfig.DEBUG -> {
                    e.printStackTrace()
                }
            }
        }

        return false
    }


    @SuppressLint("PackageManagerGetSignatures")
    fun keyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (BuildConfig.DEBUG) {
                    Log.e(
                        "KeyHash:>>>>>>>>>>>>>>>",
                        "" + Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    )
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            handelException(e)
        } catch (e: NoSuchAlgorithmException) {
            handelException(e)
        }

    }

    fun log(string: String) {
        when {
            BuildConfig.DEBUG -> {
                Log.e("BaseActivity", string)
            }
        }

    }


    fun log(title: String, message: String?) {
        when {
            BuildConfig.DEBUG -> {
                Log.e(title, message ?: "")
            }
        }

    }

    private fun progressDialog() {
        progressDialog = Dialog(this)
        val view = View.inflate(this, R.layout.progress_dialog, null)
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.setContentView(view)
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //txtMsgTV = view.findViewById<View>(R.id.txtMsgTV) as TextView
        progressDialog!!.setCancelable(false)
    }

    fun startProgressDialog() {
        when {
            progressDialog != null && !progressDialog!!.isShowing -> {
                try {
                    progressDialog!!.show()
                } catch (e: Exception) {
                    handelException(e)
                }

            }
        }
    }

    fun stopProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                handelException(e)
            }

        }
    }

    override fun onSyncStart() {
        if(isLoading){            startProgressDialog()}
    }

    override fun onSyncFinish() {
        stopProgressDialog()
    }

    open fun errorSnackBar(
        errorString: String,
        call: Call<String>?,
        callBack: Callback<String>?,
    ): SnackbarManager? {
        val buttontext: String = if (call != null && callBack != null) {
            getString(R.string.retry_cap)
        } else {
            getString(R.string.exit_caps)
        }
        val snackBar: Snackbar = Snackbar.with(this)
            .type(SnackbarType.MULTI_LINE)
            .text(errorString)
            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
            .actionLabel(buttontext)
            .actionListener(object : ActionClickListener {
                override fun onActionClicked(snackbar: Snackbar) {
                    if (call != null && callBack != null) {
                        onSyncStart()
                        call.clone().enqueue(callBack)
                    } else {
                        finish()
                    }
                }

            })
        return SnackbarManager.create(snackBar)
    }

    override fun onSyncFailure(
        errorCode: Int,
        t: Throwable?,
        response: Response<String>?,
        call: Call<String>?,
        callBack: Callback<String>?,
    ) {
        log("error_message", if (response != null) response.message() else "")
        log("error_code", errorCode.toString())
        if (this.isFinishing) return
        if (failureAlertDialog != null && failureAlertDialog!!.isShowing) {
            failureAlertDialog!!.dismiss()
        }
        if (errorCode == HttpsResponseCode.FORBIDDEN_ERROR || errorCode == HttpsResponseCode.UN_AUTHORIZATION) {
            log(getString(R.string.error), getString(R.string.session_timeout_redirecting))
            showToast(getString(R.string.session_timeout_redirecting))
            restFullClient!!.setLoginStatus(null)
            store?.saveProfileData(null)
            gotoLoginSignUpActivity()
        } else if (errorCode == HttpsResponseCode.SERVER_ERROR) {
            if(response.toString().contains("check-user")){
                restFullClient!!.setLoginStatus(null)
                showToast("There was an error logging in with your account.\nPlease login again")
                finish()
            }else{

                showToast(getString(R.string.problem_connecting_to_the_server))
            }
        } else if (t is SocketTimeoutException || t is ConnectException) {
            log(getString(R.string.error), getString(R.string.request_timeout_slow_connection))
            errorSnackBar(
                getString(R.string.request_timeout_slow_connection),
                call,
                callBack
            )!!.show()
        } else if (t is AppInMaintenance) {
            log(getString(R.string.error), getString(R.string.api_is_in_maintenance))
            failureErrorDialog(t.message!!, call, callBack)!!.show()
        } else if (t is AppExpiredError) {
            log(getString(R.string.error), getString(R.string.api_is_expired))
            checkDate(t.message!!)
        } else {
            if (response != null) response.message() else if (t != null) t.message else log(
                getString(R.string.error),
                ""
            )
            var message = getString(R.string.problem_connecting_to_the_server)
            try {
                val json = JSONObject(
                    response?.body()
                        ?: response?.errorBody()?.string() ?: "{'message':'$message'}"
                )
                if (json.has("message")) {
                    message = if (json.get("message") is JSONObject) {
                        val keys: Iterator<String> = json.getJSONObject("message").keys()
                        val strName = keys.next()
                        json.getJSONObject("message").optString(strName)
                    } else {
                        json.getString("message")
                    }
                } else if (json.has("error")) {
                    message = if (json.get("error") is JSONObject) {
                        val keys: Iterator<String> = json.getJSONObject("error").keys()
                        val strName = keys.next()
                        json.getJSONObject("error").optString(strName)
                    } else {
                        json.getString("error")
                    }
                }
            } catch (e: java.lang.Exception) {
                handelException(e)
            }
            if (message.isNotEmpty()) {
                showToastOne(message.replace("[", "").replace("]", ""))
            }
        }

    }


    private fun failureErrorDialog(
        errorString: String,
        call: Call<String>?,
        callBack: Callback<String>?,
    ): android.app.AlertDialog? {
        if (call != null && callBack != null) {
            failureDailog!!.setMessage(errorString).setCancelable(false)
                .setNegativeButton(getString(R.string.exit_caps)) { dialog, which -> finish() }
                .setPositiveButton(getString(R.string.retry_cap)) { dialog, which ->
                    onSyncStart()
                    call.clone().enqueue(callBack)
                }
        } else failureDailog!!.setMessage(errorString).setCancelable(false)
            .setPositiveButton(getString(R.string.exit_caps)) { dialog, which -> finish() }
        failureAlertDialog = failureDailog!!.create()
        return failureAlertDialog
    }

    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?,
    ) {
        try {

            if ((responseUrl.contains(Const.CryptoApi.API_CRYPTO_NEWS) || responseUrl.contains(Const.CryptoApi.API_ALL_NEWS))
                && Const.CryptoApi.BASE_URL == Const.CryptoApi.BASE_URL_NEWS) {
                Const.CryptoApi.BASE_URL = "https://pro-api.coinmarketcap.com/"
                updateRestfulClient()
            }

            val jsonObject = JSONObject(response!!)
            when {
               responseUrl.contains(Const.Venly.PREVIEW_TOKEN_TRANSFER)->{
                   when(val fragment = supportFragmentManager.findFragmentById(R.id.container)){
                       is SendPreviewFragment -> {
                           fragment.setGasFee(jsonObject)
                       }
                   }


                }
                responseUrl.contains(Const.Venly.VERIFY_ADDRESS) -> {
                    when(val fragment = supportFragmentManager.findFragmentById(R.id.container)){
                        is SendEtherumFragment -> {
                            fragment.setFields(jsonObject)
                        }
                    }

                }
                responseUrl.contains(Const.Orders.GET_ORDER) -> {
                    when(val fragment = supportFragmentManager.findFragmentById(R.id.container)){
                        is TransactionDetailsFragment -> {
                            // set all the fields
                            fragment.setFields(jsonObject)
                        }
                    }
                }
                responseUrl.contains(Const.Authentication.API_CHECK) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            val data = Gson().fromJson(
                                jsonObject.getJSONObject("data").toString(),
                                Data::class.java
                            )
                            store?.saveProfileData(data)
                            gotoMainActivity()
                        }
                        else -> {
                            if (jsonObject.has("message")) {
                                showToastOne(jsonObject.getJSONObject("message").toString())
                            }
                        }
                    }
                }
                responseUrl.contains(Const.Authentication.API_LOGOUT) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            showToastOne(getString(R.string.logout_successfully))
                            restFullClient!!.setLoginStatus(null)
                            store?.saveProfileData(null)
                            gotoSplashctivity()
                        }
                        else -> {
                            if (jsonObject.has("message")) {
                                showToastOne(jsonObject.getJSONObject("message").toString())
                            }
                        }
                    }
                }
                responseUrl.contains(Const.CryptoApi.API_CRYPTO_INFO) ->{
                    when( responseCode){
                        Const.STATUS_OK -> {
                            if(jsonObject.has("data")){
                                val list: ArrayList<CryptoData> = Gson().fromJson(
                                    jsonObject.getJSONArray("data").toString(),
                                    object : TypeToken<ArrayList<CryptoData>>() {}.type
                                )
                                saveCryptoList(list)

                            }
                    }
                    }
                }
                responseUrl.contains(  Const.CryptoApi.API_TRENDING_CRYPTO) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            if (jsonObject.has("data")) {
                                val list: ArrayList<CryptoData> = Gson().fromJson(
                                    jsonObject.getJSONArray("data").toString(),
                                    object : TypeToken<ArrayList<CryptoData>>() {}.type
                                )
                                saveCryptoList(list)
                                when(val fragment = supportFragmentManager.findFragmentById(R.id.container)){
                                    is CryptoFragment -> {
                                        fragment.updateTrendingList(list)
                                    }

                                    is MainHomeFragment -> {
                                        try {
                                            fragment.updateTrendingList(list)
                                        }catch (ex:Exception ){}

                                    }
                                }
                            }
                        }
                    }
                }
                responseUrl.contains("api/transactions") ->{
                    when (responseCode){Const.STATUS_OK ->{
                        when(val fragment = supportFragmentManager.findFragmentById(R.id.container)) {
                            is TransactionFragment -> {
                                fragment.updateNewsList(
                                    Gson().fromJson(
                                        jsonObject.getJSONArray("data").toString(),
                                        object : TypeToken<ArrayList<TransactionHistory>>() {}.type
                                    )
                                )
                            }
                        }
                    }
                    }
                }
                responseUrl.contains(Const.CryptoApi.API_TOP_MOVER_CRYPTO) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            if (jsonObject.has("data")) {
                                val list: ArrayList<CryptoData> = Gson().fromJson(
                                    jsonObject.getJSONArray("data").toString(),
                                    object : TypeToken<ArrayList<CryptoData>>() {}.type
                                )
                                when(val fragment = supportFragmentManager.findFragmentById(R.id.container)){
                                    is CryptoFragment -> {
                                        fragment.updateTopMoverList(list)
                                    }

                                    is MainHomeFragment -> {
                                        fragment.updateTopMoverList(list)
                                    }
                                }
                            }
                        }
                    }
                }

                responseUrl.contains(Const.CryptoApi.API_CRYPTO_NEWS) || responseUrl.contains(Const.CryptoApi.API_ALL_NEWS) -> {
                    when (responseCode) {
                        Const.STATUS_OK -> {
                            if (jsonObject.has("data")) {

                                when(val fragment = supportFragmentManager.findFragmentById(R.id.container)){
                                    is CryptoWatchlistFragment -> {
                                        fragment.updateNewsList(Gson().fromJson(
                                            jsonObject.getJSONArray("data").toString(),
                                            object : TypeToken<ArrayList<NewsData>>() {}.type
                                        ))
                                    }

                                    is MainHomeFragment -> {
                                        fragment.updateNewsList(Gson().fromJson(
                                            jsonObject.getJSONArray("data").toString(),
                                            object : TypeToken<ArrayList<NewsData>>() {}.type
                                        ))
                                    }

                                    is WalletOneFragment -> {
                                        fragment.updateNewsList(Gson().fromJson(
                                            jsonObject.getJSONArray("data").toString(),
                                            object : TypeToken<ArrayList<NewsData>>() {}.type
                                        ))
                                    }

                                }
                            }
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            handelException(e)
        }
    }

    fun showToast(msg: String) {
        SnackbarManager.create(
            Snackbar.with(this).duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                .type(SnackbarType.MULTI_LINE)
                .text(msg)
        )!!.show()
    }

    fun showToastOne(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

    }

    private fun strictModeThread() {
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun transitionSlideInHorizontal() {
        this.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun onClick(v: View) {
        hideSoftKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkBroadcast()
    }

    fun backAction() {
        when {
            exit -> {
                finishAffinity()
            }
            else -> {
                showToastOne(getString(R.string.press_one_more_time))
                exit = true
                Handler().postDelayed({ exit = false }, (2 * 1000).toLong())
            }
        }
    }

    interface PermCallback {
        fun permGranted(resultCode: Int)

        fun permDenied(resultCode: Int)
    }

    inner class NetworksBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = NetworkUtil.getConnectivityStatusString(context)
            if (status == null && networkAlertDialog != null) {
                networkStatus = null
                networkAlertDialog.dismiss()
            } else if (status != null && networkStatus == null)
                showNoNetworkDialog(status)
        }
    }

    fun handelException(e: java.lang.Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        checkDate(Const.DATE_CHECK)
        store?.setBoolean(Const.PrefConst.FOREGROUND, true)
    }


    fun oneClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()

    }


    fun getDeviceToken(): String {
        return if (store!!.getString(Const.PrefConst.DEVICE_TOKEN, "")!!.isEmpty()) {
            uniqueDeviceId
        } else {
            store!!.getString(Const.PrefConst.DEVICE_TOKEN)!!
        }
    }

    fun getUserDetail(): UserDetail? {
        return Gson().fromJson(store!!.getString(Const.PrefConst.USER_DATA), UserDetail::class.java)
    }

    fun clickOne(): Boolean {
        hideSoftKeyboard()
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    fun cryptoCurrencyLogo(id: Int): String {
        return Const.CryptoApi.LOGO_URL + id + Const.PNG
    }

    open fun coolNumberFormat(count: Double): String? {
        if (count < 1000) return "" + count
        val exp = (ln(count) / ln(1000.0)).toInt()
        val format = DecimalFormat("0.##")
        val value: String = format.format(count / 1000.0.pow(exp.toDouble()))
        return String.format("%s %c", value, "kMBTPE"[exp - 1])
    }

    /*--------------------Save crypto watch list------------------------*/
    fun saveCryptoWatchList(data : CryptoData){
        val list = getCryptoWatchList()
        for(i in 0 until list.size){
            if(list[i].id == data.id){
                return
            }
        }

        list.add(data)
        store!!.saveString(Const.SAVE_CRYPTO_WATCHLIST,Gson().toJson(list))
    }

    fun getCryptoWatchList() : ArrayList<CryptoData>{
        return if(store!!.getString(Const.SAVE_CRYPTO_WATCHLIST) != null) {
            val json = store!!.getString(Const.SAVE_CRYPTO_WATCHLIST)
            Gson().fromJson(
                json,
                object : TypeToken<ArrayList<CryptoData?>?>() {}.type
            )
        }else{
            ArrayList()
        }
    }

    fun removeCryptoFromWatchlist(cryptoId : Int){
        val list = getCryptoWatchList()
        for(i in 0 until list.size){
            if(list[i].id == cryptoId){
                list.removeAt(i)
                break
            }
        }
        store!!.saveString(Const.SAVE_CRYPTO_WATCHLIST,Gson().toJson(list))
    }

    fun isCryptoExistInWatchlist(cryptoId : Int) :  Boolean{
        val list = getCryptoWatchList()
        for(i in 0 until list.size){
            if(list[i].id == cryptoId){
                return true
            }
        }

        return false
    }

    /*-------------------------Save crypto List-----------------------------*/
    fun saveCryptoList(list : ArrayList<CryptoData>){
     /*   var crList:ArrayList<CryptoData> = getCryptoList()
        for(i in list.indices){
            var x = crList.find{it.id ==list[i].id}

            if(x != null){
                crList[i]=x
            }else{
                crList.add(list[i])
            }
        }*/
        store!!.saveString(Const.SAVE_CRYPTO_LIST,Gson().toJson(list))
    }
    fun getMyCryptoList() : ArrayList<CryptoData>{
        return if(store!!.getString(Const.SAVE_MY_CRYPTO_LIST) != null) {
            val json = store!!.getString(Const.SAVE_MY_CRYPTO_LIST)
            Gson().fromJson(
                json,
                object : TypeToken<ArrayList<CryptoData?>?>() {}.type
            )
        }else{
            ArrayList()
        }
    }
    fun saveMyCryptoList(list: ArrayList<CryptoData>){
        store!!.saveString(Const.SAVE_MY_CRYPTO_LIST,Gson().toJson(list))
    }
    fun getCryptoList() : ArrayList<CryptoData>{
        return if(store!!.getString(Const.SAVE_CRYPTO_LIST) != null) {
            val json = store!!.getString(Const.SAVE_CRYPTO_LIST)
            Gson().fromJson(
                json,
                object : TypeToken<ArrayList<CryptoData?>?>() {}.type
            )
        }else{
            ArrayList()
        }
    }

    /*-------------------------Recent search crypto List-----------------------------*/
    fun saveRecentList(data : CryptoData){
        val list = getRecentList()
        for(i in 0 until list.size){
            if(list[i].id == data.id){
                return
            }
        }

        list.add(data)
        store!!.saveString(Const.SAVE_RECENT_SEARCH_CRYPTO,Gson().toJson(list))
    }

    fun getRecentList() : ArrayList<CryptoData>{
        return if(store!!.getString(Const.SAVE_RECENT_SEARCH_CRYPTO) != null) {
            val json = store!!.getString(Const.SAVE_RECENT_SEARCH_CRYPTO)
            Gson().fromJson(
                json,
                object : TypeToken<ArrayList<CryptoData?>?>() {}.type
            )
        }else{
            ArrayList()
        }
    }

    fun clearRecentList(list: ArrayList<CryptoData>){
        store!!.saveString(Const.SAVE_RECENT_SEARCH_CRYPTO,Gson().toJson(list))
    }

    /*--------------------------Api's handling----------------------------------*/
    fun getTrendingCryptoApi() {
        val call = apiCryptoSection!!.apiTrendingCrypto()
        restFullClientCryptoSection!!.sendRequest(call, this)
    }
    fun getTopMoverCryptoApi() {
//        val call = apiCryptoSection!!.apiTopMoverCrypto()
//        restFullClientCryptoSection!!.sendRequest(call, this)
    }


    private fun getCryptoHistoricalDataApi(cryptoId : Int) {
        val call = apiCryptoSection!!.apiCryptoHistoricalData(cryptoId)
        restFullClientCryptoSection!!.sendRequest(call, this)
    }

    fun getCryptoNewsApi(cryptoId: Int) {
        Const.CryptoApi.BASE_URL = Const.CryptoApi.BASE_URL_NEWS
        updateRestfulClient()
        val call = if(cryptoId != 0){
            pageSize = 10
            apiCryptoSection!!.apiCryptoNews(cryptoId,1,pageSize)
        }else{
//            pageSize = 20
            apiCryptoSection!!.apiAllNews(pageSize)
        }

        restFullClientCryptoSection!!.sendRequest(call, this)
    }

    private fun updateRestfulClient() {
        restFullClientCryptoSection =
            (application as BaseApp).initSyncManagerCrypto()
        apiCryptoSection = (application as BaseApp).getAPICrypto()
    }


    fun getFormattedNewsTime(createdAt : String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        var ago: CharSequence = ""
        try {
            val time = sdf.parse(createdAt).time
            val now = System.currentTimeMillis()
            ago =
                DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ago.toString()
    }

    private val srcLanguage = Locale.ENGLISH
    private val dstLanguage = Locale.ENGLISH
    open fun translate(text: String?): String? {
        var translated: String? = null
        try {
            val query: String = URLEncoder.encode(text, "UTF-8")
            val langpair: String = URLEncoder.encode(
                srcLanguage.language.toString() + "|" + dstLanguage.language, "UTF-8"
            )
            val url =
                "http://mymemory.translated.net/api/get?q=$query&langpair=$langpair"
            val hc: HttpClient = HttpClientBuilder.create().build()
            val hg = HttpGet(url)
            val hr: HttpResponse = hc.execute(hg)
            if (hr.statusLine.statusCode === HttpStatus.SC_OK) {
                val response = JSONObject(EntityUtils.toString(hr.entity))
                translated = response.getJSONObject("responseData").getString("translatedText")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return translated
    }
}
