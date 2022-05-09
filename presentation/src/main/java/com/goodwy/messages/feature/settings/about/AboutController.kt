package com.goodwy.messages.feature.settings.about

import android.content.res.ColorStateList
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.iterator
import com.jakewharton.rxbinding2.view.clicks
import com.goodwy.messages.BuildConfig
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkController
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.extensions.resolveThemeColor
import com.goodwy.messages.common.util.extensions.setBackgroundTint
import com.goodwy.messages.common.util.extensions.setVisible
import com.goodwy.messages.common.widget.PreferenceView
import com.goodwy.messages.injection.appComponent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.about_controller.*
import kotlinx.android.synthetic.main.about_controller.preferences
import kotlinx.android.synthetic.main.preference_view.view.*
import kotlinx.android.synthetic.main.settings_chevron_widget.view.*
import javax.inject.Inject

class AboutController : QkController<AboutView, Unit, AboutPresenter>(), AboutView {

    override val optionsItemIntent: Subject<Int> = PublishSubject.create()
    @Inject lateinit var colors: Colors
    @Inject override lateinit var presenter: AboutPresenter

    init {
        appComponent.inject(this)
        layoutRes = R.layout.about_controller
    }

    override fun onViewCreated() {
        versionCode.text = " " + BuildConfig.VERSION_NAME
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.bindIntents(this)
        setTitle(R.string.about_title)
        showBackButton(true)
        setHasOptionsMenu(true)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_activated),
            intArrayOf(-android.R.attr.state_activated))
        val textTertiary = view.context.resolveThemeColor(android.R.attr.textColorTertiary)
        val imageTintList = ColorStateList(states, intArrayOf(colors.theme().theme, textTertiary))
        source.chevron.imageTintList = imageTintList
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.about, menu)
        val textSecondary = activity!!.resolveThemeColor(android.R.attr.textColorSecondary)
        menu.iterator().forEach { menuItem ->
            menuItem.icon = menuItem.icon?.apply { setTint(textSecondary) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        optionsItemIntent.onNext(item.itemId)
        return true
    }

    override fun preferenceClicks(): Observable<PreferenceView> = (0 until preferences.childCount)
            .map { index -> preferences.getChildAt(index) }
            .mapNotNull { view -> view as? PreferenceView }
            .map { preference -> preference.clicks().map { preference } }
            .let { preferences -> Observable.merge(preferences) }

    override fun render(state: Unit) {
        // No special rendering required
        val theme = colors.theme().theme
        rateButton.setBackgroundTint(theme)
        otherAppButton.setBackgroundTint(theme)

        source.icon.setVisible(true)
        source.chevron.setImageResource(R.drawable.ic_chevron_right_black_24dp)
    }

    override fun ratingClicks(): Observable<*> = rateButton.clicks()

    override fun otherAppsClicks(): Observable<*> = otherAppButton.clicks()

    override fun sourceCodeClicks(): Observable<*> = otherAppButton.clicks()

}