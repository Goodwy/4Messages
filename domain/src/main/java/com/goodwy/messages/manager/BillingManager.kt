package com.goodwy.messages.manager

import android.app.Activity
import io.reactivex.Observable

interface BillingManager {

    companion object {
        const val SKU_PLUS = "messages_plus"
        const val SKU_PLUS_DONATE = "messages_plus_donate"
    }

    data class Product(
        val sku: String,
        val price: String,
        val priceCurrencyCode: String
    )

    val products: Observable<List<Product>>
    val upgradeStatus: Observable<Boolean>

    suspend fun checkForPurchases()

    suspend fun queryProducts()

    suspend fun initiatePurchaseFlow(activity: Activity, sku: String)

}
