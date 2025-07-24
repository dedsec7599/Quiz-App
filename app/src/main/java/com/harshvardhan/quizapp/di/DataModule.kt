package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.database.QuizDatabase
import com.harshvardhan.quizapp.repos.QuizDBRepo
import com.harshvardhan.quizapp.repos.QuizDBRepoImpl
import com.harshvardhan.quizapp.repos.QuizRepo
import com.harshvardhan.quizapp.repos.QuizRepoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<QuizRepo> { QuizRepoImpl(get()) }
    single<QuizDBRepo> { QuizDBRepoImpl(get()) }

    single { QuizDatabase.getDatabase(androidContext()) }
    single { get<QuizDatabase>().topicDao() }
    single { get<QuizDatabase>().questionDao() }


}