package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.repos.QuizRepo
import com.harshvardhan.quizapp.repos.QuizRepoImpl
import org.koin.dsl.module

val dataModule = module {
    single<QuizRepo> { QuizRepoImpl(get()) }
}