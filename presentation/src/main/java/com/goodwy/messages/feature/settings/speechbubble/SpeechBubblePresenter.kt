package com.goodwy.messages.feature.settings.speechbubble

import android.content.Context
import androidx.annotation.ColorRes
import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkPresenter
import com.goodwy.messages.common.util.extensions.getColorCompat
import com.goodwy.messages.common.util.extensions.makeToast
import com.goodwy.messages.manager.BillingManager
import com.goodwy.messages.manager.WidgetManager
import com.goodwy.messages.util.Preferences
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject

class SpeechBubblePresenter @Inject constructor(
    //context: Context,
    private val billingManager: BillingManager,
    private val context: Context,
    private val navigator: Navigator,
    private val prefs: Preferences,
    private val widgetManager: WidgetManager
) : QkPresenter<SpeechBubbleView, SpeechBubbleState>(SpeechBubbleState()) {

    init {
        disposables += prefs.bubbleColorInvert.asObservable()
            .subscribe { bubbleColorInvert -> newState { copy(bubbleColorInvert = bubbleColorInvert) }
                widgetManager.updateTheme()}

        val bubbleStyleLabels = context.resources.getStringArray(R.array.settings_bubble_style)
        val bubbleStyleIds = context.resources.getIntArray(R.array.settings_bubble_style_ids)
        disposables += prefs.bubbleStyle.asObservable()
            .subscribe { bubbleStyle ->
                val index = bubbleStyleIds.indexOf(bubbleStyle)
                newState { copy(bubbleStyleSummary = bubbleStyleLabels[index], bubbleStyleIds = bubbleStyle) }
            }

        disposables += billingManager.upgradeStatus
            .subscribe { upgraded -> newState { copy(upgraded = upgraded) } }

    }

    override fun bindIntents(view: SpeechBubbleView) {
        super.bindIntents(view)

        /*view.preferenceClicks()
            .autoDisposable(view.scope())
            .subscribe { preference ->
                when (preference.id) {
                    R.id.bubbleColorInvert -> prefs.bubbleColorInvert.set(!prefs.bubbleColorInvert.get())
                    R.id.bubbleStyle -> view.showBubbleStylePicker()
                }
            }*/

        view.preferenceClicks()
            .withLatestFrom(
                billingManager.upgradeStatus)
            { preference, upgraded ->
                when {
                    !upgraded -> context.makeToast(R.string.toast_messages_plus)
                    else -> when (preference.id) {
                        R.id.bubbleColorInvert -> prefs.bubbleColorInvert.set(!prefs.bubbleColorInvert.get())
                        R.id.bubbleStyle -> view.showBubbleStylePicker()
                    }
                }}
            .autoDisposable(view.scope())
            .subscribe()

        view.speechBubbleSelected()
            .autoDisposable(view.scope())
            .subscribe(prefs.bubbleStyle::set)

        view.styleOriginalSelected()
            .withLatestFrom(
                billingManager.upgradeStatus)
            { _, upgraded ->
                when {
                    !upgraded -> context.makeToast(R.string.toast_messages_plus)
                    else -> prefs.bubbleStyle.set(0)
                }}
            .autoDisposable(view.scope())
            .subscribe()

        view.styleIosSelected()
            .withLatestFrom(
                billingManager.upgradeStatus)
            { _, upgraded ->
                when {
                    !upgraded -> context.makeToast(R.string.toast_messages_plus)
                    else -> prefs.bubbleStyle.set(1)
                }}
            .autoDisposable(view.scope())
            .subscribe()

        view.styleSimpleSelected()
            .withLatestFrom(
                billingManager.upgradeStatus)
            { _, upgraded ->
                when {
                    !upgraded -> context.makeToast(R.string.toast_messages_plus)
                    else -> prefs.bubbleStyle.set(2)
                }}
            .autoDisposable(view.scope())
            .subscribe()

        view.styleTriangleSelected()
            .withLatestFrom(
                billingManager.upgradeStatus)
            { _, upgraded ->
                when {
                    !upgraded -> context.makeToast(R.string.toast_messages_plus)
                    else -> prefs.bubbleStyle.set(3)
                }}
            .autoDisposable(view.scope())
            .subscribe()

        view.fabClicks()
            .autoDisposable(view.scope())
            .subscribe { navigator.showQksmsPlusActivity("backup_fab") }
    }

}