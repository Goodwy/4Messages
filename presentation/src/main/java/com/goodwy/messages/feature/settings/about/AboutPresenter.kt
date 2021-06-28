package com.goodwy.messages.feature.settings.about

import com.goodwy.messages.R
import com.goodwy.messages.common.Navigator
import com.goodwy.messages.common.base.QkPresenter
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import javax.inject.Inject

class AboutPresenter @Inject constructor(
    private val navigator: Navigator
) : QkPresenter<AboutView, Unit>(Unit) {

    override fun bindIntents(view: AboutView) {
        super.bindIntents(view)

        view.preferenceClicks()
                .autoDisposable(view.scope())
                .subscribe { preference ->
                    when (preference.id) {
                        R.id.rateTitle -> navigator.showRating()

                        R.id.developer -> navigator.showDeveloper()

                        R.id.source -> navigator.showSourceCode()

                        R.id.changelog -> navigator.showChangelog()

                        R.id.contact -> navigator.showSupport()

                        R.id.license -> navigator.showLicense()
                    }
                }
    }

}