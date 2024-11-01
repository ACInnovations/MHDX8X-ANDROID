package com.dx8_exchange.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dx8_exchange.R
import com.dx8_exchange.ui.activity.MainActivity
import com.dx8_exchange.ui.base.BaseActivity
import com.toxsl.restfulClient.api.SyncEventListener
import link.magic.android.Magic
import link.magic.android.modules.user.requestConfiguration.GetIdTokenConfiguration
import link.magic.android.modules.user.response.GetIdTokenResponse
import link.magic.android.modules.wallet.response.ShowWalletResponse

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



/**
 * A simple [Fragment] subclass.
 * Use the [magicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class magicFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var magic: Magic
    private lateinit var mainAct: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainAct=requireActivity() as MainActivity
        magic=mainAct.magic as Magic
        // Inflate the layout for this fragment
        val completable =  magic.wallet.showUI(
                this.requireContext()
            )

        completable.whenComplete { response: ShowWalletResponse?, error: Throwable? ->
            if (error != null) {
                Log.d("error", error.localizedMessage)
            }
            if (response != null) {
                Log.d("Wallet",  "show Wallet:" + response.result)

            }
        }
        return inflater.inflate(R.layout.fragment_magic, container, false)
    }

}