package com.goodwy.messages.blocking

import io.reactivex.Completable
import io.reactivex.Single

interface BlockingClient {

    enum class Capability {
        BLOCK_WITHOUT_PERMISSION,
        BLOCK_WITH_PERMISSION ,
        CANT_BLOCK
    }

    sealed class Action {
        class Block(val reason: String? = null) : Action()
        object Unblock : Action()

        // We only need these for Should I Answer, because they don't allow us to block numbers in their app directly.
        // This means there's a good chance that if a number is blocked in QK, it won't be blocked there, so we
        // shouldn't unblock the conversation in that case
        object DoNothing : Action()

        override fun toString(): String {
            return when (this) {
                is Block -> "Block"
                is Unblock -> "Unblock"
                is DoNothing -> "DoNothing"
            }
        }
    }

    /**
     * Returns true if the target blocking client is available for use, ie. it is installed
     */
    fun isAvailable(): Boolean

    /**
     * Returns the level of access that the given blocking client provides to QKSMS
     */
    fun getClientCapability(): Capability

    /**
     * Returns the recommendation action to perform given a message from the [address]
     */
    fun shouldBlock(address: String): Single<Action> = Single.fromCallable {
        BlockingClient.Action.Unblock
    }

    /**
     * Returns whether or not the [address] is in the blocking manager's blacklist
     * In most cases this will return the same result as [shouldBlock], but it's possible for an app's blacklist
     * to be temporarily deactivated, in which case the results will differ
     */
    fun isBlacklisted(address: String): Single<Action>

    /**
     * Returns the recommendation action to perform given a message from the [content]
     */
    fun getActionFromContent(content: String): Single<Action> = Single.fromCallable {
        BlockingClient.Action.Unblock
    }

    /**
     * Blocks the numbers or opens the manager
     */
    fun blockAddresses(addresses: List<String>): Completable

    /**
     * Unblocks the numbers or opens the manager
     */
    fun unblockAddresses(addresses: List<String>): Completable

    /**
     * Blocks the regexps
     */
    fun blockRegexps(regexps: List<String>): Completable = Completable.fromCallable {
        // Do nothing by default
    }

    /**
     * Unblocks the regexps
     */
    fun unblockRegexps(regexps: List<String>): Completable = Completable.fromCallable {
        // Do nothing by default
    }

    /**
     * Opens the settings page for the blocking manager
     */
    fun openSettings()

}
