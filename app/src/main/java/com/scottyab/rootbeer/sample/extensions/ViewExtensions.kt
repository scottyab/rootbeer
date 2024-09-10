package com.scottyab.rootbeer.sample.extensions

import android.view.View

internal fun View.show() {
    visibility = View.VISIBLE
}

internal fun View.hide() {
    visibility = View.GONE
}
