package com.goodwy.messages.blocking

import com.goodwy.messages.repository.BlockingRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class QksmsBlockingClient @Inject constructor(
    private val blockingRepo: BlockingRepository
) : BlockingClient {

    override fun isAvailable(): Boolean = true

    override fun getClientCapability() = BlockingClient.Capability.BLOCK_WITHOUT_PERMISSION

    override fun shouldBlock(address: String): Single<BlockingClient.Action> = isBlacklisted(address)

    override fun isBlacklisted(address: String): Single<BlockingClient.Action> = Single.fromCallable {
        when (blockingRepo.isBlockedAddress(address)) {
            true -> BlockingClient.Action.Block()
            false -> BlockingClient.Action.Unblock
        }
    }

    override fun getActionFromContent(content: String): Single<BlockingClient.Action> = Single.fromCallable {
        when (blockingRepo.isBlockedContent(content)) {
            true -> BlockingClient.Action.Block("Blocked for content")
            false -> BlockingClient.Action.Unblock
        }
    }

    override fun blockAddresses(addresses: List<String>): Completable = Completable.fromCallable {
        blockingRepo.blockNumber(*addresses.toTypedArray())
    }

    override fun unblockAddresses(addresses: List<String>): Completable = Completable.fromCallable {
        blockingRepo.unblockNumbers(*addresses.toTypedArray())
    }

    override fun blockRegexps(regexps: List<String>): Completable = Completable.fromCallable  {
        blockingRepo.blockRegex(*regexps.toTypedArray())
    }

    override fun unblockRegexps(regexps: List<String>): Completable = Completable.fromCallable  {
        blockingRepo.unblockRegexps(*regexps.toTypedArray())
    }

    override fun openSettings() = Unit // TODO: Do this here once we implement AndroidX navigation

}
