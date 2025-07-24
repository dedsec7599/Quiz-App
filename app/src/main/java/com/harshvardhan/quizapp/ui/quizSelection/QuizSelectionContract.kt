package com.harshvardhan.quizapp.ui.quizSelection

import com.harshvardhan.quizapp.dataModels.Topic

sealed class QuizSelectionContract {
    sealed class Event {
        data class OnTopicSelected(val topic: Topic): Event()
    }

    data class State(
        val finishedQuizzes: List<String>,
        val isLoading: Boolean,
        val topics: List<Topic>
    ) {
        companion object {
            val initialState = State(
                finishedQuizzes = listOf(),
                isLoading = false,
                topics = listOf()
            )
        }

    }

    sealed class Effect {
        sealed class Navigation: Effect() {
            data class NavigateToQuiz(val topic: Topic): Navigation()
        }
        data object ShowError : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}