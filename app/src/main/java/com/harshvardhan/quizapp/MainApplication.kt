package com.harshvardhan.quizapp

import android.app.Application
import com.harshvardhan.quizapp.di.appModule
import com.harshvardhan.quizapp.di.dataModule
import com.harshvardhan.quizapp.di.domainModule
import com.harshvardhan.quizapp.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initializing modules
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                appModule,
                dataModule,
                domainModule,
                networkModule
            )
        }
    }
}