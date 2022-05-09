package com.goodwy.messages.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.goodwy.messages.blocking.BlockingClient
import com.goodwy.messages.interactor.MarkBlocked
import com.goodwy.messages.repository.ConversationRepository
import com.goodwy.messages.util.Preferences
import dagger.android.AndroidInjection
import javax.inject.Inject

class BlockThreadReceiver : BroadcastReceiver() {

    @Inject lateinit var blockingClient: BlockingClient
    @Inject lateinit var conversationRepo: ConversationRepository
    @Inject lateinit var markBlocked: MarkBlocked
    @Inject lateinit var prefs: Preferences

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        val pendingResult = goAsync()
        val threadId = intent.getLongExtra("threadId", 0)
        val conversation = conversationRepo.getConversation(threadId)!!
        val blockingManager = prefs.blockingManager.get()

        blockingClient
                .blockAddresses(conversation.recipients.map { it.address })
                .andThen(markBlocked.buildObservable(MarkBlocked.Params(listOf(threadId), blockingManager, null)))
                .subscribe { pendingResult.finish() }
    }

}
