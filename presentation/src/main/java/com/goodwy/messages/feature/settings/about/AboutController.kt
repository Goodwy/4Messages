package com.goodwy.messages.feature.settings.about

import android.provider.Settings.Global.getString
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.goodwy.messages.BuildConfig
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkController
import com.goodwy.messages.common.util.Colors
import com.goodwy.messages.common.util.extensions.setBackgroundTint
import com.goodwy.messages.common.widget.PreferenceView
import com.goodwy.messages.injection.appComponent
import com.google.android.exoplayer2.mediacodec.MediaFormatUtil.setString
import io.reactivex.Observable
import kotlinx.android.synthetic.main.about_controller.*
import kotlinx.android.synthetic.main.settings_chevron_one_widget.*
import kotlinx.android.synthetic.main.settings_chevron_two_widget.*
import kotlinx.android.synthetic.main.settings_chevron_three_widget.*
import javax.inject.Inject

class AboutController : QkController<AboutView, Unit, AboutPresenter>(), AboutView {

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
    }

    override fun preferenceClicks(): Observable<PreferenceView> = (0 until preferences.childCount)
            .map { index -> preferences.getChildAt(index) }
            .mapNotNull { view -> view as? PreferenceView }
            .map { preference -> preference.clicks().map { preference } }
            .let { preferences -> Observable.merge(preferences) }

    override fun render(state: Unit) {
        // No special rendering required
        chevronOne.setBackgroundTint(colors.theme().textSecondary)
        chevronTwo.setBackgroundTint(colors.theme().textSecondary)
        chevronThree.setBackgroundTint(colors.theme().textSecondary)
    }

}