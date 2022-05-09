package com.goodwy.messages.manager

import io.reactivex.Observable

interface RatingManager {

    val shouldShowRating: Observable<Boolean>

    /**
     * Whether or not we should show the rating UI should depend on the number of sessions
     */
    fun addSession()

    fun rate()

    fun dismiss()

}