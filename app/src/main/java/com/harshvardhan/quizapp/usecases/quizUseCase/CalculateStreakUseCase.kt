package com.harshvardhan.quizapp.usecases.quizUseCase

import com.harshvardhan.quizapp.dataModels.AnsweredQuestion

class CalculateStreakUseCase {
    fun calculateLongestStreak(answeredQuestions: List<AnsweredQuestion>): Int {
        var currentStreak = 0
        var longestStreak = 0

        answeredQuestions.forEach { question ->
            if (question.isCorrect && !question.isSkipped) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 0
            }
        }

        return longestStreak
    }

    fun calculateCurrentStreak(answeredQuestions: List<AnsweredQuestion>): Int {
        var currentStreak = 0

        // Count from the end to get current streak
        for (i in answeredQuestions.indices.reversed()) {
            val question = answeredQuestions[i]
            if (question.isCorrect && !question.isSkipped) {
                currentStreak++
            } else {
                break
            }
        }

        return currentStreak
    }
}