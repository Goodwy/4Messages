package com.goodwy.messages.feature.main

import com.goodwy.messages.model.Conversation
import com.goodwy.messages.model.SearchResult
import com.goodwy.messages.repository.SyncRepository
import io.realm.RealmResults

data class MainState(
    val hasError: Boolean = false,
    val page: MainPage = Inbox(),
    val drawerOpen: Boolean = false,
    val upgraded: Boolean = true,
    val showRating: Boolean = false,
    val syncing: SyncRepository.SyncProgress = SyncRepository.SyncProgress.Idle,
    val defaultSms: Boolean = true,
    val smsPermission: Boolean = true,
    val contactPermission: Boolean = true
)

sealed class MainPage

data class Inbox(
    val addContact: Boolean = false,
    val markPinned: Boolean = true,
    val markRead: Boolean = false,
    val data: RealmResults<Conversation>? = null,
    val selected: Int = 0
) : MainPage()

data class Searching(
    val loading: Boolean = false,
    val data: List<SearchResult>? = null
) : MainPage()

data class Archived(
    val addContact: Boolean = false,
    val markPinned: Boolean = true,
    val markRead: Boolean = false,
    val data: RealmResults<Conversation>? = null,
    val selected: Int = 0
) : MainPage()
