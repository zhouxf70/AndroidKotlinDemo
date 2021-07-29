package com.example.androidkotlindemo.animator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.androidkotlindemo.common.KLog

/**
 * Created by zxf on 2021/7/1
 */
class MyTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.textViewStyle)

    constructor(context: Context) : this(context, null)

    override fun onDraw(canvas: Canvas?) {
        KLog.d("onDraw")
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        KLog.d("dispatchDraw")
        super.dispatchDraw(canvas)
    }

    override fun setAlpha(alpha: Float) {
        KLog.d("set alpha : $alpha")
        super.setAlpha(alpha)
    }
}