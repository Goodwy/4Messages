package com.goodwy.messages.injection

import com.goodwy.messages.common.Application

internal lateinit var appComponent: AppComponent
    private set

internal object AppComponentManager {

    fun init(application: Application) {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .build()
    }

}