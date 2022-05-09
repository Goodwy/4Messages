package com.goodwy.messages.feature.settings.about

import com.goodwy.messages.common.base.QkViewContract
import com.goodwy.messages.common.widget.PreferenceView
import io.reactivex.Observable

interface AboutView : QkViewContract<Unit> {

    val optionsItemIntent: Observable<Int>
    fun preferenceClicks(): Observable<PreferenceView>
    fun ratingClicks(): Observable<*>
    fun otherAppsClicks(): Observable<*>
    fun sourceCodeClicks(): Observable<*>

}