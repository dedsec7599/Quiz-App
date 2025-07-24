package com.harshvardhan.quizapp.ui.quizSelection

import com.harshvardhan.quizapp.dataModels.Topic

sealed class QuizSelectionContract {
    sealed class Event {
        data class OnTopicSelected(val topic: Topic): Event()
        data class OnReviewClicked(val topic: Topic): Event()

        data object UpdateTopicStatus: Event()
        data object OnBackPress: Event()

    }

    data class State(
        val finishedQuizzes: List<String>,
        val isLoading: Boolean,
        val hasApiFailed: Boolean,
        val topics: List<Topic>
    ) {
        companion object {
            val initialState = State(
                finishedQuizzes = listOf(),
                isLoading = false,
                hasApiFailed = false,
                topics = listOf()
            )
        }

    }

    sealed class Effect {
        sealed class Navigation: Effect() {
            data class NavigateToQuiz(val topic: Topic, val isReviewing: Boolean): Navigation()
            data object OnBackPress: Navigation()
        }
        data object ShowError : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}