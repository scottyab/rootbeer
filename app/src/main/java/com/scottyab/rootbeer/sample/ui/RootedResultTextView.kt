package com.scottyab.rootbeer.sample.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.scottyab.rootbeer.sample.R
import timber.log.Timber

class RootedResultTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setCustomFont(context, "SubwayNovella.ttf")
    }

    private fun setCustomFont(ctx: Context, asset: String): Boolean {
        return try {
            val typeface = Typeface.createFromAsset(ctx.assets, asset)
            setTypeface(typeface)
            true
        } catch (e: Exception) {
            Timber.e(e, "Unable to load typeface: ${e.message}")
            false
        }
    }

    fun update(isRooted: Boolean) {
        if (isRooted) {
            rooted()
        } else {
            notRooted()
        }
    }

    private fun rooted() {
        setText(R.string.result_rooted)
        setTextColor(ContextCompat.getColor(context, R.color.fail))
    }

    private fun notRooted() {
        setText(R.string.result_not_rooted)
        setTextColor(ContextCompat.getColor(context, R.color.pass))
    }
}
