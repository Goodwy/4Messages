package com.goodwy.messages.feature.settings

import android.content.Context
import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkPresenter
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.DateFormatter
import com.goodwy.messages.common.util.extensions.makeToast
import com.goodwy.messages.interactor.DeleteOldMessages
import com.goodwy.messages.interactor.SyncMessages
import com.goodwy.messages.manager.AnalyticsManager
import com.goodwy.messages.manager.BillingManager
import com.goodwy.messages.manager.WidgetManager
import com.goodwy.messages.repository.MessageRepository
import com.goodwy.messages.repository.SyncRepository
import com.goodwy.messages.service.AutoDeleteService
import com.goodwy.messages.util.NightModeManager
import com.goodwy.messages.util.Preferences
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    colors: Colors,
    syncRepo: SyncRepository,
    private val analytics: AnalyticsManager,
    private val context: Context,
    private val billingManager: BillingManager,
    private val dateFormatter: DateFormatter,
    private val deleteOldMessages: DeleteOldMessages,
    private val messageRepo: MessageRepository,
    private val navigator: Navigator,
    private val nightModeManager: NightModeManager,
    private val widgetManager: WidgetManager,
    private val prefs: Preferences,
    private val syncMessages: SyncMessages
) : QkPresenter<SettingsView, SettingsState>(SettingsState(
        nightModeId = prefs.nightMode.get()
)) {

    init {
        disposables += colors.themeObservable()
                .subscribe { theme -> newState { copy(theme = theme.theme) } }

        val nightModeLabels = context.resources.getStringArray(R.array.night_modes)
        disposables += prefs.nightMode.asObservable()
                .subscribe { nightMode ->
                    newState { copy(nightModeSummary = nightModeLabels[nightMode], nightModeId = nightMode) }
                }

        disposables += prefs.nightStart.asObservable()
                .map { time -> nightModeManager.parseTime(time) }
                .map { calendar -> calendar.timeInMillis }
                .map { millis -> dateFormatter.getTimestamp(millis) }
                .subscribe { nightStart -> newState { copy(nightStart = nightStart) } }

        disposables += prefs.nightEnd.asObservable()
                .map { time -> nightModeManager.parseTime(time) }
                .map { calendar -> calendar.timeInMillis }
                .map { millis -> dateFormatter.getTimestamp(millis) }
                .subscribe { nightEnd -> newState { copy(nightEnd = nightEnd) } }

        disposables += prefs.black.asObservable()
                .subscribe { black -> newState { copy(black = black) } }

        disposables += prefs.gray.asObservable()
                .subscribe { gray -> newState { copy(gray = gray) } }

        disposables += prefs.notifications().asObservable()
                .subscribe { enabled -> newState { copy(notificationsEnabled = enabled) } }

        disposables += prefs.autoEmoji.asObservable()
                .subscribe { enabled -> newState { copy(autoEmojiEnabled = enabled) } }

        val delayedSendingLabels = context.resources.getStringArray(R.array.delayed_sending_labels)
        disposables += prefs.sendDelay.asObservable()
                .subscribe { id -> newState { copy(sendDelaySummary = delayedSendingLabels[id], sendDelayId = id) } }

        disposables += prefs.delivery.asObservable()
                .subscribe { enabled -> newState { copy(deliveryEnabled = enabled) } }

        disposables += prefs.signature.asObservable()
                .subscribe { signature -> newState { copy(signature = signature) } }

        val textSizeLabels = context.resources.getStringArray(R.array.text_sizes)
        disposables += prefs.textSize.asObservable()
                .subscribe { textSize ->
                    newState { copy(textSizeSummary = textSizeLabels[textSize], textSizeId = textSize) }
                }

        disposables += prefs.autoColor.asObservable()
                .subscribe { autoColor -> newState { copy(autoColor = autoColor) } }

        disposables += prefs.grayAvatar.asObservable()
                .subscribe { grayAvatar -> newState { copy(grayAvatar = grayAvatar) }
                    widgetManager.updateTheme()}

        disposables += prefs.separator.asObservable()
                .subscribe { separator -> newState { copy(separator = separator) } }

        disposables += prefs.systemFont.asObservable()
                .subscribe { enabled -> newState { copy(systemFontEnabled = enabled) } }

        disposables += prefs.unicode.asObservable()
                .subscribe { enabled -> newState { copy(stripUnicodeEnabled = enabled) } }

        disposables += prefs.mobileOnly.asObservable()
                .subscribe { enabled -> newState { copy(mobileOnly = enabled) } }

        disposables += prefs.autoDelete.asObservable()
                .subscribe { autoDelete -> newState { copy(autoDelete = autoDelete) } }

        disposables += prefs.longAsMms.asObservable()
                .subscribe { enabled -> newState { copy(longAsMms = enabled) } }

        val mmsSizeLabels = context.resources.getStringArray(R.array.mms_sizes)
        val mmsSizeIds = context.resources.getIntArray(R.array.mms_sizes_ids)
        disposables += prefs.mmsSize.asObservable()
                .subscribe { maxMmsSize ->
                    val index = mmsSizeIds.indexOf(maxMmsSize)
                    newState { copy(maxMmsSizeSummary = mmsSizeLabels[index], maxMmsSizeId = maxMmsSize) }
                }

        disposables += syncRepo.syncProgress
                .sample(16, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe { syncProgress -> newState { copy(syncProgress = syncProgress) } }

        disposables += syncMessages
    }

    override fun bindIntents(view: SettingsView) {
        super.bindIntents(view)

        view.preferenceClicks()
                .autoDisposable(view.scope())
                .subscribe {
                    Timber.v("Preference click: ${context.resources.getResourceName(it.id)}")

                    when (it.id) {
                        R.id.theme -> view.showThemePicker()

                        R.id.night -> view.showNightModeDialog()

                        R.id.nightStart -> {
                            val date = nightModeManager.parseTime(prefs.nightStart.get())
                            view.showStartTimePicker(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE))
                        }

                        R.id.nightEnd -> {
                            val date = nightModeManager.parseTime(prefs.nightEnd.get())
                            view.showEndTimePicker(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE))
                        }

                        R.id.black -> prefs.black.set(!prefs.black.get())

                        R.id.gray -> prefs.gray.set(!prefs.gray.get())

                        R.id.autoEmoji -> prefs.autoEmoji.set(!prefs.autoEmoji.get())

                        R.id.notifications -> navigator.showNotificationSettings()

                        R.id.swipeActions -> view.showSwipeActions()

                        R.id.delayed -> view.showDelayDurationDialog()

                        R.id.delivery -> prefs.delivery.set(!prefs.delivery.get())

                        R.id.signature -> view.showSignatureDialog(prefs.signature.get())

                        R.id.textSize -> view.showTextSizePicker()

                        R.id.autoColor -> {
                            analytics.setUserProperty("Preference: Auto Color", !prefs.autoColor.get())
                            prefs.autoColor.set(!prefs.autoColor.get())
                        }

                        R.id.grayAvatar -> {
                            prefs.grayAvatar.set(!prefs.grayAvatar.get())
                        }

                        R.id.separator -> prefs.separator.set(!prefs.separator.get())

                        R.id.systemFont -> prefs.systemFont.set(!prefs.systemFont.get())

                        R.id.unicode -> prefs.unicode.set(!prefs.unicode.get())

                        R.id.mobileOnly -> prefs.mobileOnly.set(!prefs.mobileOnly.get())

                        R.id.autoDelete -> view.showAutoDeleteDialog(prefs.autoDelete.get())

                        R.id.longAsMms -> prefs.longAsMms.set(!prefs.longAsMms.get())

                        R.id.mmsSize -> view.showMmsSizePicker()

                        R.id.sync -> syncMessages.execute(Unit)

                        R.id.about -> view.showAbout()
                    }
                }

        view.aboutLongClicks()
                .map { !prefs.logging.get() }
                .doOnNext { enabled -> prefs.logging.set(enabled) }
                .autoDisposable(view.scope())
                .subscribe { enabled ->
                    context.makeToast(when (enabled) {
                        true -> R.string.settings_logging_enabled
                        false -> R.string.settings_logging_disabled
                    })
                }

        view.nightModeSelected()
                .withLatestFrom(billingManager.upgradeStatus) { mode, upgraded ->
                    if (!upgraded && mode == Preferences.NIGHT_MODE_AUTO) {
                        view.showQksmsPlusSnackbar()
                    } else {
                        nightModeManager.updateNightMode(mode)
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()

        view.viewQksmsPlusClicks()
                .autoDisposable(view.scope())
                .subscribe { navigator.showQksmsPlusActivity("settings_night") }

        view.nightStartSelected()
                .autoDisposable(view.scope())
                .subscribe { nightModeManager.setNightStart(it.first, it.second) }

        view.nightEndSelected()
                .autoDisposable(view.scope())
                .subscribe { nightModeManager.setNightEnd(it.first, it.second) }

        view.textSizeSelected()
                .autoDisposable(view.scope())
                .subscribe(prefs.textSize::set)

        view.sendDelaySelected()
                .withLatestFrom(billingManager.upgradeStatus) { duration, upgraded ->
                    if (!upgraded && duration != 0) {
                        view.showQksmsPlusSnackbar()
                    } else {
                        prefs.sendDelay.set(duration)
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()

        view.signatureChanged()
                .doOnNext(prefs.signature::set)
                .autoDisposable(view.scope())
                .subscribe()

        view.autoDeleteChanged()
                .observeOn(Schedulers.io())
                .filter { maxAge ->
                    if (maxAge == 0) {
                        return@filter true
                    }

                    val counts = messageRepo.getOldMessageCounts(maxAge)
                    if (counts.values.sum() == 0) {
                        return@filter true
                    }

                    runBlocking { view.showAutoDeleteWarningDialog(counts.values.sum()) }
                }
                .doOnNext { maxAge ->
                    when (maxAge == 0) {
                        true -> AutoDeleteService.cancelJob(context)
                        false -> {
                            AutoDeleteService.scheduleJob(context)
                            deleteOldMessages.execute(Unit)
                        }
                    }
                }
                .doOnNext(prefs.autoDelete::set)
                .autoDisposable(view.scope())
                .subscribe()

        view.mmsSizeSelected()
                .autoDisposable(view.scope())
                .subscribe(prefs.mmsSize::set)
    }

}