package com.goodwy.messages.feature.themepicker.injection

import com.goodwy.messages.feature.themepicker.ThemePickerController
import com.goodwy.messages.injection.scope.ControllerScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ThemePickerModule(private val controller: ThemePickerController) {

    @Provides
    @ControllerScope
    @Named("recipientId")
    fun provideThreadId(): Long = controller.recipientId

}