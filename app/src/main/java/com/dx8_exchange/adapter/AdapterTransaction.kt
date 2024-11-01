package com.dx8_exchange.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dx8_exchange.R
import com.dx8_exchange.databinding.AdapterTransactionBinding
import com.dx8_exchange.ui.base.BaseAdapter
import com.dx8_exchange.ui.base.BaseViewHolder

class AdapterTransaction(val size: Int = 12) : BaseAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterTransactionBinding>(LayoutInflater.from(parent.context), R.layout.adapter_transaction, parent, false)
        return BaseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterTransactionBinding

    }

}