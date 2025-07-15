package com.harshvardhan.quizapp.di

import com.harshvardhan.quizapp.ui.quizScreen.QuizViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::QuizViewModel)
}