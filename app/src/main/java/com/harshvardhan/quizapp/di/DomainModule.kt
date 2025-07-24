package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.usecases.quizSelectionUseCase.FetchTopicsFromDbUseCase
import com.harshvardhan.quizapp.usecases.quizSelectionUseCase.GetTopicsUseCase
import com.harshvardhan.quizapp.usecases.quizSelectionUseCase.SyncDbTopicsUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.CalculateStreakUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.GetQuestionsUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.LoadReviewQuestionsUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.SaveQuizResultUseCase
import org.koin.dsl.module

val domainModule = module {
    // Quiz Topic Selection Screen
    single { GetTopicsUseCase(get()) }
    single { FetchTopicsFromDbUseCase(get()) }
    single { SyncDbTopicsUseCase(get()) }

    // Quiz Screen
    single { CalculateStreakUseCase() }
    single { GetQuestionsUseCase(get()) }
    single { LoadReviewQuestionsUseCase(get()) }
    single { SaveQuizResultUseCase(get()) }
}