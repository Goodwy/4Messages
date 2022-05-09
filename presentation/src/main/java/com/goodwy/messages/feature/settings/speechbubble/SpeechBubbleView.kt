package com.goodwy.messages.feature.settings.speechbubble

import com.goodwy.messages.common.base.QkViewContract
import com.goodwy.messages.common.widget.PreferenceView
import io.reactivex.Observable

interface SpeechBubbleView : QkViewContract<SpeechBubbleState> {

    fun preferenceClicks(): Observable<PreferenceView>
    fun fabClicks(): Observable<*>
    fun speechBubbleSelected(): Observable<Int>

    fun showBubbleStylePicker()
    fun styleOriginalSelected(): Observable<*>
    fun styleIosSelected(): Observable<*>
    fun styleSimpleSelected(): Observable<*>
    fun styleTriangleSelected(): Observable<*>

}