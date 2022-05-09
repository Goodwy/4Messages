package com.goodwy.messages.feature.settings

import com.goodwy.messages.repository.SyncRepository
import com.goodwy.messages.util.Preferences

data class SettingsState(
    val theme: Int = 0,
    val nightModeSummary: String = "",
    val nightModeId: Int = Preferences.NIGHT_MODE_OFF,
    val nightStart: String = "",
    val nightEnd: String = "",
    val black: Boolean = false,
    val gray: Boolean = false,
    val autoColor: Boolean = true,
    val grayAvatar: Boolean = true,
    val simColor: Boolean = true,
    val separator: Boolean = true,
    val autoEmojiEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val sendDelaySummary: String = "",
    val sendDelayId: Int = 0,
    val deliveryEnabled: Boolean = false,
    val signature: String = "",
    val textSizeSummary: String = "",
    val textSizeId: Int = Preferences.TEXT_SIZE_NORMAL,
    val systemFontEnabled: Boolean = false,
    val splitSmsEnabled: Boolean = false,
    val stripUnicodeEnabled: Boolean = false,
    val mobileOnly: Boolean = false,
    val autoDelete: Int = 0,
    val longAsMms: Boolean = false,
    val maxMmsSizeSummary: String = "100KB",
    val maxMmsSizeId: Int = 100,
    val maxSearchElevationSummary: String = "Normal",
    val maxSearchElevationId: Int = 32,
    val syncProgress: SyncRepository.SyncProgress = SyncRepository.SyncProgress.Idle
)