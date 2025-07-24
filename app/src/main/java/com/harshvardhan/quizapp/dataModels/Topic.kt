package com.harshvardhan.quizapp.dataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @SerialName("questions_url") val url: String = "",
    val isFinished: Boolean = false,
    val bestStreak: Int = 0
)