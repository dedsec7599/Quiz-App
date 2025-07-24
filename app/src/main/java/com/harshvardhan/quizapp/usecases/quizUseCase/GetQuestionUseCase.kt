package com.harshvardhan.quizapp.usecases.quizUseCase

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.repos.QuizRepo

class GetQuestionsUseCase(
    private val quizRepo: QuizRepo
) {
    suspend operator fun invoke(url: String): Result<List<Question>> {
        return quizRepo.fetchTopicQuestions(url).map { questions ->
            questions.shuffled()
        }
    }
}