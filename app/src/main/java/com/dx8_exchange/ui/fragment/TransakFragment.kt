package com.dx8_exchange.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dx8_exchange.R
import com.dx8_exchange.databinding.FragmentTransakBinding
import com.dx8_exchange.ui.activity.MainActivity
import com.dx8_exchange.ui.base.BaseFragment
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.ViewClickHandler
import com.dx8_exchange.utils.extensions.replaceFragment
import com.mynameismidori.currencypicker.CurrencyPicker

class TransakFragment : BaseFragment(), ViewClickHandler {
    private var binding: FragmentTransakBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null){
            binding = FragmentTransakBinding.inflate(inflater, container, false)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolBar("", false)
        binding!!.titleTV.text = baseActivity!!.getString(R.string.buy_crype_to_your_wallet)
        (baseActivity as MainActivity).isShowBottomNavigation(false)
        binding!!.handleClick = this
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.backArrowIV->baseActivity?.onBackPressedDispatcher!!.onBackPressed()
            R.id.nextTV -> {
                val bundle = Bundle()
                bundle.putInt("Transak", Const.TRANSAK)
                baseActivity!!.replaceFragment(SendPreviewFragment(), bundle)
            }
            R.id.selectCurrencyTV, R.id.flagIV->{
                val picker = CurrencyPicker.newInstance("Select Currency")
                picker.setListener { _, code, _, flagDrawableResID ->
                    binding!!.flagIV.setImageResource(flagDrawableResID)
                    binding!!.selectCurrencyTV.text = code.toString()
                    picker.dismiss()
                    // Implement your code here
                }
                picker.show(baseActivity!!.supportFragmentManager, "CURRENCY_PICKER")
            }
        }
    }
}