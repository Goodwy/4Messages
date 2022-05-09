package com.goodwy.messages.feature.settings.simconfigure

data class SimConfigureState(
    val simColor: Boolean = true,
    val sim1Color: Int = 0,
    val sim1Label: String = "",
    val sim2Color: Int = 0,
    val sim2Label: String = "",
    val sim3Color: Int = 0,
    val sim3Label: String = "",
    val upgraded: Boolean = false
)