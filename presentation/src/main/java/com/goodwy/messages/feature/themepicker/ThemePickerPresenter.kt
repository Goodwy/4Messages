package com.goodwy.messages.feature.themepicker

import com.f2prateek.rx.preferences2.Preference
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkPresenter
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.manager.BillingManager
import com.goodwy.messages.manager.WidgetManager
import com.goodwy.messages.util.Preferences
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject
import javax.inject.Named

class ThemePickerPresenter @Inject constructor(
    prefs: Preferences,
    @Named("recipientId") private val recipientId: Long,
    private val billingManager: BillingManager,
    private val colors: Colors,
    private val navigator: Navigator,
    private val widgetManager: WidgetManager
) : QkPresenter<ThemePickerView, ThemePickerState>(ThemePickerState(recipientId = recipientId)) {

    private val theme: Preference<Int> = prefs.theme(recipientId)

    override fun bindIntents(view: ThemePickerView) {
        super.bindIntents(view)

        theme.asObservable()
                .autoDisposable(view.scope())
                .subscribe { color -> view.setCurrentTheme(color) }

        // Update the theme when a material theme is clicked
        view.themeSelected()
                .autoDisposable(view.scope())
                .subscribe { color ->
                    theme.set(color)
                    if (recipientId == 0L) {
                        widgetManager.updateTheme()
                    }
                }

        val color_1 : Int = android.graphics.Color.parseColor("#ff453a")
        val color_2 : Int = android.graphics.Color.parseColor("#ff3b30")
        val color_3 : Int = android.graphics.Color.parseColor("#ff9f0a")
        val color_4 : Int = android.graphics.Color.parseColor("#ff9500")
        val color_5 : Int = android.graphics.Color.parseColor("#ffd60a")
        val cols = listOf(color_1, color_2, color_3, color_4, color_5)
        for(col in cols)
        view.themeIosSelected()
                .withLatestFrom(billingManager.upgradeStatus) { color, upgraded ->
                    if (!upgraded /*&& color != col*/) {
                        view.showQksmsPlusSnackbar()
                    } else {
                        theme.set(color)
                        if (recipientId == 0L) {
                            widgetManager.updateTheme()
                        }
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()

        view.themeMessagesSelected()
                .autoDisposable(view.scope())
                .subscribe { color ->
                    theme.set(color)
                    if (recipientId == 0L) {
                        widgetManager.updateTheme()
                    }
                }

        // Update the color of the apply button
        view.hsvThemeSelected()
                .doOnNext { color -> newState { copy(newColor = color) } }
                .map { color -> colors.textPrimaryOnThemeForColor(color) }
                .doOnNext { color -> newState { copy(newTextColor = color) } }
                .autoDisposable(view.scope())
                .subscribe()

        // Toggle the visibility of the apply group
        Observables.combineLatest(theme.asObservable(), view.hsvThemeSelected()) { old, new -> old != new }
                .autoDisposable(view.scope())
                .subscribe { themeChanged -> newState { copy(applyThemeVisible = themeChanged) } }

        // Update the theme, when apply is clicked
        view.applyHsvThemeClicks()
                .withLatestFrom(view.hsvThemeSelected()) { _, color -> color }
                .withLatestFrom(billingManager.upgradeStatus) { color, upgraded ->
                    if (!upgraded) {
                        view.showQksmsPlusSnackbar()
                    } else {
                        theme.set(color)
                        if (recipientId == 0L) {
                            widgetManager.updateTheme()
                        }
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()

        // Show QKSMS+ activity
        view.viewQksmsPlusClicks()
                .autoDisposable(view.scope())
                .subscribe { navigator.showQksmsPlusActivity("settings_theme") }

        // Reset the theme
        view.clearHsvThemeClicks()
                .withLatestFrom(theme.asObservable()) { _, color -> color }
                .autoDisposable(view.scope())
                .subscribe { color -> view.setCurrentTheme(color) }
    }

}