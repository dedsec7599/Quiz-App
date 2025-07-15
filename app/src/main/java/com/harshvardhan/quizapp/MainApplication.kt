package com.harshvardhan.quizapp

import android.app.Application
import com.harshvardhan.quizapp.di.domainModule
import com.harshvardhan.quizapp.di.appModule
import com.harshvardhan.quizapp.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initializing modules
        GlobalContext.startKoin {
            androidContext(this@MainApplication)
            modules(
                appModule,
                dataModule,
                domainModule
            )
        }
    }
}