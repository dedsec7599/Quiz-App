package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.repos.quizRepo.QuizRepo
import com.harshvardhan.quizapp.repos.quizRepo.QuizRepoImpl
import org.koin.dsl.module

val dataModule = module {
    single<QuizRepo> { QuizRepoImpl(get()) }
}