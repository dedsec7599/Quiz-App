package com.harshvardhan.quizapp.usecases.quizSelectionUseCase

import com.harshvardhan.quizapp.dataModels.Topic
import com.harshvardhan.quizapp.repos.QuizRepo

class GetTopicsUseCase(
    private val dataRepo: QuizRepo
) {
    suspend operator fun invoke(): Result<List<Topic>> {
        return dataRepo.fetchQuizTopics()
    }
}