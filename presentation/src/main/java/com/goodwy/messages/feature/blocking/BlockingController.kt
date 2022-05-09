package com.goodwy.messages.feature.blocking

import android.content.res.ColorStateList
import android.view.View
import com.bluelinelabs.conductor.RouterTransaction
import com.jakewharton.rxbinding2.view.clicks
import com.goodwy.messages.R
import com.goodwy.messages.common.ChangeHandler
import com.goodwy.messages.common.base.QkController
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.extensions.animateLayoutChanges
import com.goodwy.messages.common.util.extensions.resolveThemeColor
import com.goodwy.messages.feature.blocking.manager.BlockingManagerController
import com.goodwy.messages.feature.blocking.messages.BlockedMessagesController
import com.goodwy.messages.feature.blocking.numbers.BlockedNumbersController
import com.goodwy.messages.feature.blocking.regexps.BlockedRegexpsController
import com.goodwy.messages.injection.appComponent
import com.goodwy.messages.model.BlockedNumber
import com.goodwy.messages.model.BlockedRegex
import com.goodwy.messages.model.Conversation
import io.realm.OrderedRealmCollection
import io.realm.Realm
import kotlinx.android.synthetic.main.blocking_controller.*
import kotlinx.android.synthetic.main.settings_chevron_widget.view.*
import kotlinx.android.synthetic.main.settings_switch_widget.view.*
import javax.inject.Inject

class BlockingController : QkController<BlockingView, BlockingState, BlockingPresenter>(), BlockingView {

    override val blockingManagerIntent by lazy { blockingManager.clicks() }
    override val blockedNumbersIntent by lazy { blockedNumbers.clicks() }
    override val blockedMessagesIntent by lazy { blockedMessages.clicks() }
    override val blockedRegexpsIntent by lazy { blockedRegexps.clicks() }
    override val dropClickedIntent by lazy { drop.clicks() }

    @Inject lateinit var colors: Colors
    @Inject override lateinit var presenter: BlockingPresenter

    init {
        appComponent.inject(this)
        retainViewMode = RetainViewMode.RETAIN_DETACH
        layoutRes = R.layout.blocking_controller
    }

    override fun onViewCreated() {
        super.onViewCreated()
        parent.postDelayed({ parent?.animateLayoutChanges = true }, 100)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.blocking_title)
        showBackButton(true)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_activated),
            intArrayOf(-android.R.attr.state_activated))
        val textTertiary = view.context.resolveThemeColor(android.R.attr.textColorTertiary)
        val imageTintList = ColorStateList(states, intArrayOf(colors.theme().theme, textTertiary))

        blockedNumbers.chevron.imageTintList = imageTintList
        blockedRegexps.chevron.imageTintList = imageTintList
        blockedMessages.chevron.imageTintList = imageTintList
    }

    override fun render(state: BlockingState) {
        blockedNumbers.chevron.setImageResource(R.drawable.ic_chevron_right_black_24dp)
        blockedRegexps.chevron.setImageResource(R.drawable.ic_chevron_right_black_24dp)
        blockedMessages.chevron.setImageResource(R.drawable.ic_chevron_right_black_24dp)
        //blockingManager.isVisible = false
        blockingManager.value = state.blockingManager
        drop.checkbox.isChecked = state.dropEnabled
        blockedMessages.isEnabled = !state.dropEnabled

        val blockedNumber: OrderedRealmCollection<BlockedNumber> = Realm.getDefaultInstance().where(BlockedNumber::class.java).findAll()
        blockedNumbers.value = if (state.blockingManager == activity!!.getString(R.string.blocking_manager_messages_title)) blockedNumber.size.toString() else ""
        Realm.getDefaultInstance().close()

        val blockedRegexp: OrderedRealmCollection<BlockedRegex> = Realm.getDefaultInstance().where(BlockedRegex::class.java).findAll()
        blockedRegexps.value = if (state.blockingManager == activity!!.getString(R.string.blocking_manager_messages_title)) blockedRegexp.size.toString() else ""
        Realm.getDefaultInstance().close()

        val blockedConversation: OrderedRealmCollection<Conversation> = Realm.getDefaultInstance().where(Conversation::class.java).equalTo("blocked", true).findAll()
        blockedMessages.value = blockedConversation.size.toString()
        Realm.getDefaultInstance().close()
    }

    override fun openBlockedNumbers() {
        router.pushController(RouterTransaction.with(BlockedNumbersController())
                .pushChangeHandler(ChangeHandler())
                .popChangeHandler(ChangeHandler()))
    }

    override fun openBlockedRegexps() {
        router.pushController(RouterTransaction.with(BlockedRegexpsController())
            .pushChangeHandler(ChangeHandler())
            .popChangeHandler(ChangeHandler()))
    }

    override fun openBlockedMessages() {
        router.pushController(RouterTransaction.with(BlockedMessagesController())
                .pushChangeHandler(ChangeHandler())
                .popChangeHandler(ChangeHandler()))
    }

    override fun openBlockingManager() {
        router.pushController(RouterTransaction.with(BlockingManagerController())
                .pushChangeHandler(ChangeHandler())
                .popChangeHandler(ChangeHandler()))
    }

}