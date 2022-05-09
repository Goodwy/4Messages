package com.goodwy.messages.feature.settings

import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkThemedActivity
import com.goodwy.messages.common.util.extensions.resolveThemeColor
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.container_activity.*
import kotlinx.android.synthetic.main.container_activity.toolbar
import kotlinx.android.synthetic.main.toolbar.*

class SettingsActivity : QkThemedActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
        // Добавил иконку и цвет
        toolbar.navigationIcon?.setTint(resolveThemeColor(android.R.attr.textColorSecondary))

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(SettingsController()))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}