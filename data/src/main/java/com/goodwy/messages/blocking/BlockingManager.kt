package com.goodwy.messages.blocking

import com.goodwy.messages.util.Preferences
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Delegates requests to the active blocking client
 */
@Singleton
class BlockingManager @Inject constructor(
    private val prefs: Preferences,
    private val callBlockerBlockingClient: CallBlockerBlockingClient,
    private val callControlBlockingClient: CallControlBlockingClient,
    private val qksmsBlockingClient: QksmsBlockingClient,
    private val shouldIAnswerBlockingClient: ShouldIAnswerBlockingClient
) : BlockingClient {

    private val client: BlockingClient
        get() = when (prefs.blockingManager.get()) {
            Preferences.BLOCKING_MANAGER_CB -> callBlockerBlockingClient
            Preferences.BLOCKING_MANAGER_SIA -> shouldIAnswerBlockingClient
            Preferences.BLOCKING_MANAGER_CC -> callControlBlockingClient
            else -> qksmsBlockingClient
        }

    override fun isAvailable(): Boolean = client.isAvailable()

    override fun getClientCapability(): BlockingClient.Capability = client.getClientCapability()

    override fun shouldBlock(address: String): Single<BlockingClient.Action> = client.shouldBlock(address)

    override fun isBlacklisted(address: String): Single<BlockingClient.Action> = client.isBlacklisted(address)

    override fun getActionFromContent(content: String): Single<BlockingClient.Action> = client.getActionFromContent(content)

    override fun blockAddresses(addresses: List<String>): Completable = client.blockAddresses(addresses)

    override fun unblockAddresses(addresses: List<String>): Completable = client.unblockAddresses(addresses)

    override fun blockRegexps(regexps: List<String>): Completable = client.blockRegexps(regexps)

    override fun unblockRegexps(regexps: List<String>): Completable = client.unblockRegexps(regexps)

    override fun openSettings() = client.openSettings()

}
