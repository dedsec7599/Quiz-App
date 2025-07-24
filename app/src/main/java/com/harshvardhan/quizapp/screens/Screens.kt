package com.harshvardhan.quizapp.screens

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    data object QuizSelection: Screens()

    @Serializable
    data class Quiz(val id: String, val title: String, val description: String, val url: String): Screens()
}