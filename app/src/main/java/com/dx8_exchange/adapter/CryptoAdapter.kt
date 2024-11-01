package com.dx8_exchange.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dx8_exchange.R
import com.dx8_exchange.databinding.AdapterCryptoBinding
import com.dx8_exchange.model.cryptoModel.CryptoData
import com.dx8_exchange.ui.base.BaseActivity
import com.dx8_exchange.ui.base.BaseAdapter
import com.dx8_exchange.ui.base.BaseViewHolder
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.extensions.loadGlideImageUrl
import com.dx8_exchange.utils.extensions.setColor

class CryptoAdapter(val baseActivity: BaseActivity, val type: Int = 0 , private var list : ArrayList<CryptoData> = ArrayList()) : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterCryptoBinding>(LayoutInflater.from(parent.context), R.layout.adapter_crypto, parent, false)

        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterCryptoBinding
        val data = list[position]

        binding.coinIV.loadGlideImageUrl(
            baseActivity.cryptoCurrencyLogo(data.id!!),
            R.drawable.bg_crypto_placeholder
        )
        binding.coinNameTV.text = data.name
        binding.coinCodeTV.text = data.symbol
        if(data.quote!!.usd !=null){
            if(data.quote!!.usd!!.price!!.toBigDecimal().toPlainString().startsWith("0.00")){
                binding.priceTV.text = String.format(baseActivity.getString(R.string.dollar_price),String.format("%6f", data.quote!!.usd!!.price!!.toBigDecimal().toPlainString().toDouble()))
                binding.priceTV.setTextSize(TypedValue.COMPLEX_UNIT_PX,38f)
            }else{
                binding.priceTV.text = String.format(baseActivity.getString(R.string.dollar_price),String.format("%.2f", data.quote!!.usd!!.price!!.toBigDecimal().toPlainString().toDouble()))
                binding.priceTV.setTextSize(TypedValue.COMPLEX_UNIT_PX,44f)
            }
            if(type==999)
                {
                binding.changesTV.text =  data.quote!!.usd!!.percentChange24h!!.toDouble().toString()
                binding.changesTV.setColor(R.color.greenn)
                }
            else
                {
                    binding.changesTV.text = String.format(baseActivity.getString(R.string.percentage),String.format("%.2f", data.quote!!.usd!!.percentChange24h!!.toDouble()))
                    when {
                        data.quote!!.usd!!.percentChange24h!!.toString().startsWith("-") -> {
                            binding.changesTV.setColor(R.color.Carnation)
                        }
                        else -> {
                            binding.changesTV.setColor(R.color.greenn)
                        }
                    }
                }


        }


        when (type) {
            Const.REMOVE_WATCHLIST -> {
                binding.root.setOnClickListener {
                    onItemClick(position,Const.REMOVE_WATCHLIST)
                }
            }
            else -> binding.root.setOnClickListener {
                onItemClick(position,Const.CRYPTO_LIST)
            }
        }
    }

    fun notifyList(cryptoList: ArrayList<CryptoData>) {
        this.list = cryptoList
        notifyDataSetChanged()
    }
}