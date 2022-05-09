package com.goodwy.messages.feature.conversationinfo

import android.os.Build
import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.goodwy.messages.R
import com.goodwy.messages.common.base.QkThemedActivity
import com.goodwy.messages.common.util.extensions.getColorCompat
import com.goodwy.messages.common.util.extensions.resolveThemeColor
import com.goodwy.messages.util.Preferences
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.container_activity.*

class ConversationInfoActivity : QkThemedActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
        // Добавил иконку и цвет
        toolbar.navigationIcon?.setTint(resolveThemeColor(android.R.attr.textColorSecondary))

        if (!isNightMode()) {
            val backgroundGray = getColorCompat(R.color.backgroundGray)
            toolbar.setBackgroundColor(backgroundGray)
            viewContainer.setBackgroundColor(backgroundGray)
            window.navigationBarColor = backgroundGray
            window.statusBarColor = backgroundGray
        }

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            val threadId = intent.extras?.getLong("threadId") ?: 0L
            router.setRoot(RouterTransaction.with(ConversationInfoController(threadId)))
        }
    }

    private fun isNightMode(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (prefs.nightMode.get() == Preferences.NIGHT_MODE_SYSTEM) resources.configuration.isNightModeActive
            else prefs.night.get()
        } else {
            prefs.night.get()
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

}