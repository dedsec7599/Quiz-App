package com.harshvardhan.quizapp.ui.quizScreen

import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.dataModels.Question

sealed class QuizContract {
    sealed class Event {
        data class SelectOption(val optionIndex: Int) : Event()
        object SkipQuestion : Event()
        object NextQuestion : Event()
        object RestartQuiz : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val questions: List<Question> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val selectedOptionIndex: Int? = null,
        val isAnswerRevealed: Boolean = false,
        val answeredQuestions: List<AnsweredQuestion> = emptyList(),
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val isQuizCompleted: Boolean = false,
    )

    sealed class Effect {
        data object ShowError : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}