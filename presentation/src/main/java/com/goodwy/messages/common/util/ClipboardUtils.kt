package com.goodwy.messages.common.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {

    fun copy(context: Context, string: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SMS", string)
        clipboard.setPrimaryClip(clip)
    }

}
