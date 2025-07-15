package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.usecases.CalculateStreakUseCase
import com.harshvardhan.quizapp.usecases.GetQuestionsUseCase
import org.koin.dsl.module

val domainModule = module {
    single { CalculateStreakUseCase() }
    single { GetQuestionsUseCase(get()) }
}