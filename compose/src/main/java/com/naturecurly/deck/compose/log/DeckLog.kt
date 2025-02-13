package com.naturecurly.deck.compose.log

import android.util.Log

private const val TAG = "DeckLog"

internal object DeckLog {
    fun e(msg: String, throwable: Throwable? = null) {
        Log.e(TAG, msg, throwable)
    }

    fun w(msg: String) {
        Log.w(TAG, msg)
    }
}
