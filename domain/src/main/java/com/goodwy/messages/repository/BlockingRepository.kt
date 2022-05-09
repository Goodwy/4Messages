package com.goodwy.messages.repository

import com.goodwy.messages.model.BlockedNumber
import com.goodwy.messages.model.BlockedRegex
import io.realm.RealmResults

interface BlockingRepository {

    fun blockNumber(vararg addresses: String)

    fun blockRegex(vararg regexps: String)

    fun getBlockedNumbers(): RealmResults<BlockedNumber>

    fun getBlockedNumber(id: Long): BlockedNumber?

    fun getBlockedRegexps(): RealmResults<BlockedRegex>

    fun getBlockedRegex(id: Long): BlockedRegex?

    fun isBlockedAddress(address: String): Boolean

    fun isBlockedContent(content: String): Boolean

    fun unblockNumber(id: Long)

    fun unblockNumbers(vararg addresses: String)

    fun unblockRegex(id: Long)

    fun unblockRegexps(vararg regexps: String)

}
