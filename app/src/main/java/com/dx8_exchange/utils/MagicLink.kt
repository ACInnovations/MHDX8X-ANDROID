package com.dx8_exchange.utils

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.dx8_exchange.ui.base.BaseFragment
import com.toxsl.restfulClient.api.SyncEventListener
import link.magic.android.Magic
import link.magic.android.core.relayer.urlBuilder.*
import link.magic.android.core.relayer.urlBuilder.network.CustomNodeConfiguration
import link.magic.android.modules.auth.requestConfiguration.LoginWithEmailOTPConfiguration
import link.magic.android.modules.auth.response.DIDToken
import link.magic.android.modules.user.requestConfiguration.GetIdTokenConfiguration
import link.magic.android.modules.user.response.GetIdTokenResponse
import link.magic.android.modules.user.response.IsLoggedInResponse
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthAccounts
import org.web3j.protocol.geth.Geth
import java.util.*

/* Add function for calling the api to update the token and wallet addresses using khttp */
class MagicLink(): BaseFragment() {
    val TAG: String="MagicLink"
    lateinit var web3j: Web3j
    lateinit var gethWeb3j: Geth
    lateinit var magic: Magic
    var logintokenresp: String? = null
    var ethAddress: String? = null
    var ethBalance: Double? = null
    fun startUp(ctx: Context){
        try{
            magic = Magic( ctx.applicationContext, "pk_live_5E1D7C3718FE284B", CustomNodeConfiguration("https://rpc2.sepolia.org/","11155111"))
            gethWeb3j = Geth.build(magic.rpcProvider)

        }catch(ex: Exception){
            Log.d(TAG, "startUp: $ex")
        }



     //   doLogin(ctx)
        //need to do login 1 time and store it

    }
    fun showWallet(){
    /*    val completable = magic.wallet.showUI(this.requireContext())

        completable.whenComplete { response: ShowWalletResponse?, error: Throwable? ->
            if (error != null) {
                Log.d("error", error.localizedMessage)
            }
            ⁠
            if (response != null) {
                tabActivity.toastAsync("show Wallet:" + response.result)
            }
        }*/
    }
    fun getAddresses(listener: SyncEventListener){
        try{
            //Test
           // web3j = Web3j.build(magic.rpcProvider)

            try {
                val accounts = gethWeb3j.ethAccounts().sendAsync()

                accounts.whenComplete { accRepsonse: EthAccounts?, error: Throwable? ->
                    if (error != null) {
                        Log.e("MagicError", error.localizedMessage)
                    }
                    if (accRepsonse != null && !accRepsonse.hasError()) {
                        ethAddress = accRepsonse.accounts[0]
                        Log.d("Magic", "Your address is $ethAddress")
                        listener.onSyncSuccess(1974,ethAddress!!,"",ethAddress!!)
                        return@whenComplete
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.localizedMessage)
            }


        }catch(ex: Exception){
            Log.d(TAG, "getAddresses: $ex")
        }
    }
    fun getIdToken(listener: SyncEventListener){
        val completeable =  magic.user.getIdToken(this.requireContext(),  GetIdTokenConfiguration(lifespan=900))
        if (completeable != null) {
            completeable.whenComplete{response: GetIdTokenResponse,error: Throwable? ->
                if (error != null)
                {

                    Log.d(TAG, "Magic Error: $error")

                    return@whenComplete
                }
                if(response != null ){
                    try {
                        //update the DidToken on the server
                        // var ttoken=  String(Base64.decode(response.result,1))
                        logintokenresp = response.result.toString()

                        listener.onSyncSuccess(1973, logintokenresp!!,"",logintokenresp)

                        return@whenComplete

                    }catch (ex: Exception){
                        Log.d(TAG, "getIdToken: $ex")
                    }

                }
            }
        }
    }
    fun doLogin(ctx: Context){

        val completeable = magic.user.isLoggedIn(baseActivity!!)
        completeable.whenComplete { response: IsLoggedInResponse?, error: Throwable? ->
            if (response != null && response.result) {
                web3j = Web3j.build(magic.rpcProvider)
                gethWeb3j = Geth.build(magic.rpcProvider)

                Log.d(TAG, "You're Logged In")

            }else if (response != null && response.result == false) {
                var otpConfiguration = LoginWithEmailOTPConfiguration("sales@dekconsulting.com")
//              get email from profile

                val logincomplete= magic.auth.loginWithEmailOTP(ctx,otpConfiguration)
                logincomplete.whenComplete { action: DIDToken?, error: Throwable ->
                    if(action != null){
                        web3j = Web3j.build(magic.rpcProvider)
                        gethWeb3j = Geth.build(magic.rpcProvider)

                        Log.d(TAG, "onCreate: " + action.toString())
                        //pass the DIDToken to the server for this account
                    }
                }
            }
            if (error != null) {

                // handle Error
            }
        }
    }
    fun getAllAccounts(): MutableList<String>? {
        var allAccounts: MutableList<String>? = null
        try {
            val accounts = web3j.ethAccounts().sendAsync()

            accounts.whenComplete { accRepsonse: EthAccounts?, error: Throwable? ->
                if (error != null) {
                    Log.e(TAG, error.localizedMessage)
                }
                if (accRepsonse != null && !accRepsonse.hasError()) {
                    allAccounts = accRepsonse.accounts
                    Log.d(TAG, "Returning Accounts:  " + allAccounts.toString())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
        return allAccounts;
    }

    fun getEthAccount(): String {
        var account: String =""
        try {
            val accounts = web3j.ethAccounts().sendAsync()

            accounts.whenComplete { accRepsonse: EthAccounts?, error: Throwable? ->
                if (error != null) {
                    Log.e(TAG, error.localizedMessage)
                }
                if (accRepsonse != null && !accRepsonse.hasError()) {
                    account = accRepsonse.accounts[0]
                    Log.d(TAG, "Your address is $account")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
        return account;
    }

}