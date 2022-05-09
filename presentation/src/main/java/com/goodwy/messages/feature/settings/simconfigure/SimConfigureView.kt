package com.goodwy.messages.feature.settings.simconfigure

import com.goodwy.messages.common.base.QkViewContract
import com.goodwy.messages.common.widget.PreferenceView
import io.reactivex.Observable

interface SimConfigureView : QkViewContract<SimConfigureState> {

    enum class Action { SIM1, SIM2, SIM3 }

    fun preferenceClicks(): Observable<PreferenceView>
    fun actionClicks(): Observable<Action>
    fun actionSelected(): Observable<Int>
    fun fabClicks(): Observable<*>

    fun showSimConfigure(selected: Int)

}