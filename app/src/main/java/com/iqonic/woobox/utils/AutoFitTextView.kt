package com.iqonic.woobox.utils

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView

class AutoFitTextView : TextView {
    var minTextSize: Float = 0.toFloat()
    var maxTextSize: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        maxTextSize = this.textSize
        if (maxTextSize < 35) {
            maxTextSize = 30f
        }
        minTextSize = 27f
    }

    private fun refitText(text: String, textWidth: Int) {
        if (textWidth > 0) {
            val availableWidth = (textWidth - this.paddingLeft - this.paddingRight)
            var trySize = maxTextSize
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize)
            while (((trySize > minTextSize) && (this.paint.measureText(text) > availableWidth))) {
                trySize -= 1f
                if (trySize <= minTextSize) {
                    trySize = minTextSize
                    break
                }
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize)
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize)
        }
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        refitText(text.toString(), this.width)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw) {
            refitText(this.text.toString(), w)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        refitText(this.text.toString(), parentWidth)
    }

    fun setMinTextSize(minTextSize: Int) {
        this.minTextSize = minTextSize.toFloat()
    }

    fun setMaxTextSize(minTextSize: Int) {
        this.maxTextSize = minTextSize.toFloat()
    }
}