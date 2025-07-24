package com.harshvardhan.quizapp.dataModels

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int = -1,
    val question: String = "",
    val options: List<String> = listOf(),
    val correctOptionIndex: Int = -1
)