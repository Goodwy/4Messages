package com.goodwy.messages.feature.themepicker

data class ThemePickerState(
    val recipientId: Long = 0,
    val applyThemeVisible: Boolean = false,
    val newColor: Int = -1,
    val newTextColor: Int = -1
)