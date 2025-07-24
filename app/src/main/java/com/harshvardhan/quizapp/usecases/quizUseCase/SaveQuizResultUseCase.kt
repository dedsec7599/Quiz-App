package com.harshvardhan.quizapp.usecases.quizUseCase

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.repos.QuizDBRepo

class SaveQuizResultUseCase(
    private val quizDbRepo: QuizDBRepo
) {

    suspend operator fun invoke(
        topicId: String,
        questions: List<Question>,
        userAnswers: Map<Int, String>,
        bestStreak: Int
    ) {
        quizDbRepo.completeTopicAndSaveQuestions(topicId, questions, userAnswers, bestStreak)
    }
}