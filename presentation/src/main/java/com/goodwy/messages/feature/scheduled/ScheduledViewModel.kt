/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
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
package com.goodwy.messages.feature.scheduled

import android.content.Context
import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkViewModel
import com.goodwy.messages.common.util.ClipboardUtils
import com.goodwy.messages.common.util.extensions.makeToast
import com.goodwy.messages.interactor.SendScheduledMessage
import com.goodwy.messages.manager.BillingManager
import com.goodwy.messages.repository.ScheduledMessageRepository
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class ScheduledViewModel @Inject constructor(
    billingManager: BillingManager,
    private val context: Context,
    private val navigator: Navigator,
    private val scheduledMessageRepo: ScheduledMessageRepository,
    private val sendScheduledMessage: SendScheduledMessage
) : QkViewModel<ScheduledView, ScheduledState>(ScheduledState(
        scheduledMessages = scheduledMessageRepo.getScheduledMessages()
)) {

    init {
        disposables += billingManager.upgradeStatus
                .subscribe { upgraded -> newState { copy(upgraded = upgraded) } }
    }

    override fun bindView(view: ScheduledView) {
        super.bindView(view)

        view.messageClickIntent
                .autoDisposable(view.scope())
                .subscribe { view.showMessageOptions() }

        view.messageMenuIntent
                .withLatestFrom(view.messageClickIntent) { itemId, messageId ->
                    when (itemId) {
                        0 -> sendScheduledMessage.execute(messageId)
                        1 -> scheduledMessageRepo.getScheduledMessage(messageId)?.let { message ->
                            ClipboardUtils.copy(context, message.body)
                            context.makeToast(R.string.toast_copied)
                        }
                        2 -> scheduledMessageRepo.deleteScheduledMessage(messageId)
                    }
                    Unit
                }
                .autoDisposable(view.scope())
                .subscribe()

        view.composeIntent
                .autoDisposable(view.scope())
                .subscribe { navigator.showCompose() }

        view.upgradeIntent
                .autoDisposable(view.scope())
                .subscribe { navigator.showQksmsPlusActivity("schedule_fab") }
    }

}