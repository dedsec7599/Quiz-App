package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.usecases.dbUseCase.FetchTopicQuestionsForReviewUseCase
import com.harshvardhan.quizapp.usecases.dbUseCase.FetchTopicsFromDbUseCase
import com.harshvardhan.quizapp.usecases.dbUseCase.SyncDbTopicsUseCase
import com.harshvardhan.quizapp.usecases.quizSelectionUseCase.GetTopicsUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.CalculateStreakUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.GetQuestionsUseCase
import org.koin.dsl.module

val domainModule = module {
    single { CalculateStreakUseCase() }
    single { GetQuestionsUseCase(get()) }
    single { GetTopicsUseCase(get()) }


    single { SyncDbTopicsUseCase(get()) }
    single { FetchTopicsFromDbUseCase(get()) }
    single { FetchTopicQuestionsForReviewUseCase(get()) }
}