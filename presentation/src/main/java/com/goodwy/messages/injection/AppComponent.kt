package com.goodwy.messages.injection

import com.goodwy.messages.common.Application
import com.goodwy.messages.common.Dialog
import com.goodwy.messages.common.util.QkChooserTargetService
import com.goodwy.messages.common.widget.*
import com.goodwy.messages.feature.backup.BackupController
import com.goodwy.messages.feature.blocking.BlockingController
import com.goodwy.messages.feature.blocking.manager.BlockingManagerController
import com.goodwy.messages.feature.blocking.messages.BlockedMessagesController
import com.goodwy.messages.feature.blocking.numbers.BlockedNumbersController
import com.goodwy.messages.feature.blocking.regexps.BlockedRegexpsController
import com.goodwy.messages.feature.compose.editing.DetailedChipView
import com.goodwy.messages.feature.conversationinfo.injection.ConversationInfoComponent
import com.goodwy.messages.feature.settings.SettingsController
import com.goodwy.messages.feature.settings.about.AboutController
import com.goodwy.messages.feature.settings.simconfigure.SimConfigureController
import com.goodwy.messages.feature.settings.speechbubble.SpeechBubbleController
import com.goodwy.messages.feature.settings.swipe.SwipeActionsController
import com.goodwy.messages.feature.themepicker.injection.ThemePickerComponent
import com.goodwy.messages.feature.widget.WidgetAdapter
import com.goodwy.messages.injection.android.ActivityBuilderModule
import com.goodwy.messages.injection.android.BroadcastReceiverBuilderModule
import com.goodwy.messages.injection.android.ServiceBuilderModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    BroadcastReceiverBuilderModule::class,
    ServiceBuilderModule::class])
interface AppComponent {

    fun conversationInfoBuilder(): ConversationInfoComponent.Builder
    fun themePickerBuilder(): ThemePickerComponent.Builder

    fun inject(application: Application)

    fun inject(controller: AboutController)
    fun inject(controller: BackupController)
    fun inject(controller: BlockedMessagesController)
    fun inject(controller: BlockedNumbersController)
    fun inject(controller: BlockedRegexpsController)
    fun inject(controller: BlockingController)
    fun inject(controller: BlockingManagerController)
    fun inject(controller: SettingsController)
    fun inject(controller: SimConfigureController)
    fun inject(controller: SpeechBubbleController)
    fun inject(controller: SwipeActionsController)

    fun inject(dialog: Dialog)

    fun inject(service: WidgetAdapter)

    /**
     * This can't use AndroidInjection, or else it will crash on pre-marshmallow devices
     */
    fun inject(service: QkChooserTargetService)

    fun inject(view: AvatarView)
    fun inject(view: AvatarBigView)
    fun inject(view: AvatarBiggerView)
    fun inject(view: BubbleImageView)
    fun inject(view: DetailedChipView)
    fun inject(view: PagerTitleView)
    fun inject(view: PreferenceView)
    fun inject(view: RadioPreferenceView)
    fun inject(view: QkEditText)
    fun inject(view: QkSwitch)
    fun inject(view: QkTextView)

}
