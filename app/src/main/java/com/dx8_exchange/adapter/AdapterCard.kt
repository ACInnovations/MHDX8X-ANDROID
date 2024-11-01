package com.dx8_exchange.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dx8_exchange.R
import com.dx8_exchange.databinding.AdapterCardBinding
import com.dx8_exchange.model.Card
import com.dx8_exchange.ui.base.BaseAdapter
import com.dx8_exchange.ui.base.BaseViewHolder

class AdapterCard( var cardList:ArrayList<Card>) : BaseAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterCardBinding>(LayoutInflater.from(parent.context), R.layout.adapter_card, parent, false)
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterCardBinding
        val data = cardList[position]
        binding.cardTypeTV.text = data.cardType
        binding.cardNumberTV.text = data.cardNumber
        binding.cardHolderNameTV.text = data.cardHolderName
        binding.expireDateTV.text = data.expireDate
        when {
            (position%2 == 0) -> {
                binding.masterCardIV.setBackgroundResource(R.mipmap.ic_mastercrd)
                binding.cardCL.setBackgroundResource(R.mipmap.mastercard)
            }
            else -> {
                binding.masterCardIV.setBackgroundResource(R.mipmap.ic_visa)
                binding.cardCL.setBackgroundResource(R.mipmap.visacard)
            }
        }
    }
}