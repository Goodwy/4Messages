package com.goodwy.messages.feature.blocking.regexps

import com.goodwy.messages.model.BlockedRegex
import io.realm.RealmResults

data class BlockedRegexpsState(
    val regexps: RealmResults<BlockedRegex>? = null
)
