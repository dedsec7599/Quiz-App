package com.harshvardhan.quizapp.usecases

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.repos.QuizRepo

class GetQuestionsUseCase(
    private val dataRepo: QuizRepo
) {
    suspend operator fun invoke(): Result<List<Question>> {
        return dataRepo.getQuestions().map { questions ->
            questions.shuffled()
        }
    }
}