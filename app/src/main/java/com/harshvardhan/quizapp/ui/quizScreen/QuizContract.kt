package com.harshvardhan.quizapp.ui.quizScreen

import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.dataModels.Topic

sealed class QuizContract {
    sealed class Event {
        data class SelectOption(val optionIndex: Int) : Event()
        object SkipQuestion : Event()
        object NextQuestion : Event()
        data class FetchQuestions(val topic: Topic): Event()
        data class ShowReview(val topic: Topic): Event()
        object OnBackPress: Event()
    }

    data class State(
        val currentTopic: Topic = Topic(
            id = "",
            title = "",
            description = "",
            url = ""
        ),
        val questions: List<Question> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val selectedOptionIndex: Int? = null,
        val isAnswerRevealed: Boolean = false,
        val answeredQuestions: List<AnsweredQuestion> = emptyList(),
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val isQuizCompleted: Boolean = false,
        val showReview: Boolean = false
    )

    sealed class Effect {
        sealed class Navigation: Effect() {
            data object OnBackPress: Navigation()
        }

        data object ShowError : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}