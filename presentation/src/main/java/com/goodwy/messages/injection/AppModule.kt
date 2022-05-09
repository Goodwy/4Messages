/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.goodwy.messages.injection

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.goodwy.messages.blocking.BlockingClient
import com.goodwy.messages.blocking.BlockingManager
import com.goodwy.messages.common.ViewModelFactory
import com.goodwy.messages.common.util.BillingManagerImpl
import com.goodwy.messages.common.util.NotificationManagerImpl
import com.goodwy.messages.common.util.ShortcutManagerImpl
import com.goodwy.messages.feature.conversationinfo.injection.ConversationInfoComponent
import com.goodwy.messages.feature.themepicker.injection.ThemePickerComponent
import com.goodwy.messages.listener.ContactAddedListener
import com.goodwy.messages.listener.ContactAddedListenerImpl
import com.goodwy.messages.manager.ActiveConversationManager
import com.goodwy.messages.manager.ActiveConversationManagerImpl
import com.goodwy.messages.manager.AlarmManager
import com.goodwy.messages.manager.AlarmManagerImpl
import com.goodwy.messages.manager.AnalyticsManager
import com.goodwy.messages.manager.AnalyticsManagerImpl
import com.goodwy.messages.manager.BillingManager
import com.goodwy.messages.manager.ChangelogManager
import com.goodwy.messages.manager.ChangelogManagerImpl
import com.goodwy.messages.manager.KeyManager
import com.goodwy.messages.manager.KeyManagerImpl
import com.goodwy.messages.manager.NotificationManager
import com.goodwy.messages.manager.PermissionManager
import com.goodwy.messages.manager.PermissionManagerImpl
import com.goodwy.messages.manager.RatingManager
import com.goodwy.messages.manager.ReferralManager
import com.goodwy.messages.manager.ReferralManagerImpl
import com.goodwy.messages.manager.ShortcutManager
import com.goodwy.messages.manager.WidgetManager
import com.goodwy.messages.manager.WidgetManagerImpl
import com.goodwy.messages.mapper.CursorToContact
import com.goodwy.messages.mapper.CursorToContactGroup
import com.goodwy.messages.mapper.CursorToContactGroupImpl
import com.goodwy.messages.mapper.CursorToContactGroupMember
import com.goodwy.messages.mapper.CursorToContactGroupMemberImpl
import com.goodwy.messages.mapper.CursorToContactImpl
import com.goodwy.messages.mapper.CursorToConversation
import com.goodwy.messages.mapper.CursorToConversationImpl
import com.goodwy.messages.mapper.CursorToMessage
import com.goodwy.messages.mapper.CursorToMessageImpl
import com.goodwy.messages.mapper.CursorToPart
import com.goodwy.messages.mapper.CursorToPartImpl
import com.goodwy.messages.mapper.CursorToRecipient
import com.goodwy.messages.mapper.CursorToRecipientImpl
import com.goodwy.messages.mapper.RatingManagerImpl
import com.goodwy.messages.repository.BackupRepository
import com.goodwy.messages.repository.BackupRepositoryImpl
import com.goodwy.messages.repository.BlockingRepository
import com.goodwy.messages.repository.BlockingRepositoryImpl
import com.goodwy.messages.repository.ContactRepository
import com.goodwy.messages.repository.ContactRepositoryImpl
import com.goodwy.messages.repository.ConversationRepository
import com.goodwy.messages.repository.ConversationRepositoryImpl
import com.goodwy.messages.repository.MessageRepository
import com.goodwy.messages.repository.MessageRepositoryImpl
import com.goodwy.messages.repository.ScheduledMessageRepository
import com.goodwy.messages.repository.ScheduledMessageRepositoryImpl
import com.goodwy.messages.repository.SyncRepository
import com.goodwy.messages.repository.SyncRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [
    ConversationInfoComponent::class,
    ThemePickerComponent::class])
class AppModule(private var application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRxPreferences(preferences: SharedPreferences): RxSharedPreferences {
        return RxSharedPreferences.create(preferences)
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Provides
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory = factory

    // Listener

    @Provides
    fun provideContactAddedListener(listener: ContactAddedListenerImpl): ContactAddedListener = listener

    // Manager

    @Provides
    fun provideBillingManager(manager: BillingManagerImpl): BillingManager = manager

    @Provides
    fun provideActiveConversationManager(manager: ActiveConversationManagerImpl): ActiveConversationManager = manager

    @Provides
    fun provideAlarmManager(manager: AlarmManagerImpl): AlarmManager = manager

    @Provides
    fun provideAnalyticsManager(manager: AnalyticsManagerImpl): AnalyticsManager = manager

    @Provides
    fun blockingClient(manager: BlockingManager): BlockingClient = manager

    @Provides
    fun changelogManager(manager: ChangelogManagerImpl): ChangelogManager = manager

    @Provides
    fun provideKeyManager(manager: KeyManagerImpl): KeyManager = manager

    @Provides
    fun provideNotificationsManager(manager: NotificationManagerImpl): NotificationManager = manager

    @Provides
    fun providePermissionsManager(manager: PermissionManagerImpl): PermissionManager = manager

    @Provides
    fun provideRatingManager(manager: RatingManagerImpl): RatingManager = manager

    @Provides
    fun provideShortcutManager(manager: ShortcutManagerImpl): ShortcutManager = manager

    @Provides
    fun provideReferralManager(manager: ReferralManagerImpl): ReferralManager = manager

    @Provides
    fun provideWidgetManager(manager: WidgetManagerImpl): WidgetManager = manager

    // Mapper

    @Provides
    fun provideCursorToContact(mapper: CursorToContactImpl): CursorToContact = mapper

    @Provides
    fun provideCursorToContactGroup(mapper: CursorToContactGroupImpl): CursorToContactGroup = mapper

    @Provides
    fun provideCursorToContactGroupMember(mapper: CursorToContactGroupMemberImpl): CursorToContactGroupMember = mapper

    @Provides
    fun provideCursorToConversation(mapper: CursorToConversationImpl): CursorToConversation = mapper

    @Provides
    fun provideCursorToMessage(mapper: CursorToMessageImpl): CursorToMessage = mapper

    @Provides
    fun provideCursorToPart(mapper: CursorToPartImpl): CursorToPart = mapper

    @Provides
    fun provideCursorToRecipient(mapper: CursorToRecipientImpl): CursorToRecipient = mapper

    // Repository

    @Provides
    fun provideBackupRepository(repository: BackupRepositoryImpl): BackupRepository = repository

    @Provides
    fun provideBlockingRepository(repository: BlockingRepositoryImpl): BlockingRepository = repository

    @Provides
    fun provideContactRepository(repository: ContactRepositoryImpl): ContactRepository = repository

    @Provides
    fun provideConversationRepository(repository: ConversationRepositoryImpl): ConversationRepository = repository

    @Provides
    fun provideMessageRepository(repository: MessageRepositoryImpl): MessageRepository = repository

    @Provides
    fun provideScheduledMessagesRepository(repository: ScheduledMessageRepositoryImpl): ScheduledMessageRepository = repository

    @Provides
    fun provideSyncRepository(repository: SyncRepositoryImpl): SyncRepository = repository

}