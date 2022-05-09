package com.goodwy.messages.mapper

import android.database.Cursor
import com.goodwy.messages.model.MmsPart

interface CursorToPart : Mapper<Cursor, MmsPart> {

    fun getPartsCursor(messageId: Long? = null): Cursor?

}