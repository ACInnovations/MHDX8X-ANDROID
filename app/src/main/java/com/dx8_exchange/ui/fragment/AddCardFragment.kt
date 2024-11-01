package com.dx8_exchange.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dx8_exchange.R
import com.dx8_exchange.adapter.AdapterCard
import com.dx8_exchange.databinding.BottomsheetAddCardBinding
import com.dx8_exchange.databinding.FragmentAddCardBinding
import com.dx8_exchange.model.Card
import com.dx8_exchange.ui.activity.MainActivity
import com.dx8_exchange.ui.base.BaseFragment
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.ViewClickHandler
import com.dx8_exchange.utils.extensions.checkString
import com.dx8_exchange.utils.extensions.visibleView
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddCardFragment : BaseFragment(), ViewClickHandler {
    private var binding: FragmentAddCardBinding? = null
    private var bottomSheetBinding: BottomsheetAddCardBinding? = null
    private val cardList = ArrayList<Card>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (binding == null) {
            binding = FragmentAddCardBinding.inflate(
                LayoutInflater.from(container!!.context),
                container,
                false
            )
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolBar("", false)
        binding!!.titleTV.text = baseActivity!!.getString(R.string.add_fund)
        (baseActivity as MainActivity).isShowBottomNavigation(false)
        baseActivity!!.updateStatusBarColor(R.color.deepBlack, true)
        binding!!.handleClick = this
        baseActivity!!.hideSoftKeyboard()
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.addCardTV -> showBottomSheet()
            R.id.backArrowIV -> baseActivity!!.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showBottomSheet() {
        bottomSheetBinding = null
        bottomSheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(baseActivity!!),
            R.layout.bottomsheet_add_card,
            null,
            false
        )
        val mBottomSheetDialog = BottomSheetDialog(baseActivity!!, R.style.SheetDialog)

        bottomSheetBinding!!.closeIV.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }
        bottomSheetBinding!!.inputCardNumET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty() && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (Const.symbol.space == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                if (s.isNotEmpty() && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(
                            s.toString(),
                            Const.symbol.space.toString()
                        ).size <= 3
                    ) {
                        s.insert(s.length - 1, Const.symbol.space.toString())
                    }
                }
                if (s.length == 19) {
                    bottomSheetBinding!!.expiryDateET.requestFocus()
                }
            }
        })

        bottomSheetBinding!!.expiryDateET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty() && editable.length % 3 === 0) {
                    val c: Char = editable[editable.length - 1]
                    if ('/' == c) {
                        editable.delete(editable.length - 1, editable.length)
                    }
                }
                if (editable.isNotEmpty() && editable.length % 3 === 0) {
                    val c: Char = editable[editable.length - 1]
                    if (Character.isDigit(c) && TextUtils.split(editable.toString(), "/").size <= 2
                    ) {
                        editable.insert(editable.length - 1, "/")
                    }
                }
                if (editable.length == 5) {
                    bottomSheetBinding!!.cvvET.requestFocus()
                }
            }
        })
        bottomSheetBinding!!.cvvET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 3) {
                    bottomSheetBinding!!.addCardTV.requestFocus()
                }
            }
        })
        bottomSheetBinding!!.addCardTV.setOnClickListener {
            val cardData = Card()
            cardData.cardType = "Credit Card"
            cardData.cardNumber = bottomSheetBinding!!.inputCardNumET.checkString()
            cardData.cardHolderName = bottomSheetBinding!!.inputNameET.checkString()
            cardData.expireDate = bottomSheetBinding!!.expiryDateET.checkString()
            cardList.add(cardData)
            binding!!.cardRV.adapter = AdapterCard(cardList)
            binding!!.cardImageIV.visibleView(false)
            binding!!.cardRV.visibleView(true)
            binding!!.addCardTxtTV.visibleView(false)
            mBottomSheetDialog.dismiss()
        }
        mBottomSheetDialog.setContentView(bottomSheetBinding!!.root)
        mBottomSheetDialog.show()
    }
}