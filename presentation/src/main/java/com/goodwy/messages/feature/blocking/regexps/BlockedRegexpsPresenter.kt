package com.goodwy.messages.feature.blocking.regexps

import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkPresenter
import com.goodwy.messages.interactor.MarkUnblocked
import com.goodwy.messages.repository.BlockingRepository
import com.goodwy.messages.repository.ConversationRepository
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BlockedRegexpsPresenter @Inject constructor(
    private val blockingRepo: BlockingRepository,
    private val conversationRepo: ConversationRepository,
    private val navigator: Navigator,
    private val markUnblocked: MarkUnblocked
) : QkPresenter<BlockedRegexpsView, BlockedRegexpsState>(
    BlockedRegexpsState(regexps = blockingRepo.getBlockedRegexps())
) {

    override fun bindIntents(view: BlockedRegexpsView) {
        super.bindIntents(view)

        view.unblockRegex()
            .doOnNext { id ->
                blockingRepo.getBlockedRegex(id)?.regex
                    ?.let(conversationRepo::getThreadId)
                    ?.let { threadId -> markUnblocked.execute(listOf(threadId)) }
            }
            .doOnNext(blockingRepo::unblockRegex)
            .subscribeOn(Schedulers.io())
            .autoDisposable(view.scope())
            .subscribe()

        view.addRegex()
            .autoDisposable(view.scope())
            .subscribe { view.showAddDialog() }

        view.bannerRegexps()
            .autoDisposable(view.scope())
            .subscribe { navigator.showWikiRegexps() }

        view.saveRegex()
            .subscribeOn(Schedulers.io())
            .autoDisposable(view.scope())
            .subscribe { regex -> blockingRepo.blockRegex(regex) }
    }

}
