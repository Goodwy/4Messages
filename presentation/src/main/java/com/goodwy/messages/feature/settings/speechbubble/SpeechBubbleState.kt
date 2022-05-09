package com.goodwy.messages.feature.settings.speechbubble

data class SpeechBubbleState(
    val bubbleColorInvert: Boolean = true,
    val bubbleStyleIds: Int = 0,
    val bubbleStyleSummary: String = "Original",
    val upgraded: Boolean = false
)