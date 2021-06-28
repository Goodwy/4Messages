/*
 * Copyright (C) 2019 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.goodwy.messages.migration

import android.content.Context
import com.goodwy.messages.blocking.QksmsBlockingClient
import com.goodwy.messages.common.util.extensions.versionCode
import com.goodwy.messages.repository.ConversationRepository
import com.goodwy.messages.util.Preferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class QkMigration @Inject constructor(
    private val context: Context,
    private val conversationRepo: ConversationRepository,
    private val prefs: Preferences,
    private val qksmsBlockingClient: QksmsBlockingClient
) {

    fun performMigration() {
        GlobalScope.launch {
            val oldVersion = prefs.version.get()

            if (oldVersion < 2199) {
                upgradeTo370()
            }

            prefs.version.set(context.versionCode)
        }
    }

    private fun upgradeTo370() {
        // Migrate changelog version
        prefs.changelogVersion.set(prefs.version.get())

        // Migrate from old SIA preference to blocking manager preference
        if (prefs.sia.get()) {
            prefs.blockingManager.set(Preferences.BLOCKING_MANAGER_SIA)
            prefs.sia.delete()
        }

        // Migrate blocked conversations into QK blocking client
        val addresses = conversationRepo.getBlockedConversations()
                .flatMap { conversation -> conversation.recipients }
                .map { recipient -> recipient.address }
                .distinct()

        qksmsBlockingClient.block(addresses).blockingAwait()
    }

}
