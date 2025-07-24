package com.harshvardhan.quizapp.dataModels

data class QuizResult(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val skippedQuestions: Int,
    val longestStreak: Int,
    val answeredQuestions: List<AnsweredQuestion>
)

data class AnsweredQuestion(
    val question: Question,
    val correctAnswer: String,
    val userAnswer: String,
)