package com.harshvardhan.quizapp.usecases.quizUseCase

import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.repos.QuizDBRepo

class LoadReviewQuestionsUseCase(
    private val quizDbRepo: QuizDBRepo
) {
    suspend operator fun invoke(topicId: String): List<Pair<Question, AnsweredQuestion>> {
        val savedQuestions = quizDbRepo.getAllQuestionsForTopic(topicId)

        if (savedQuestions.isEmpty()) {
            throw Exception("No saved questions found for topic $topicId")
        }

        return savedQuestions.map { entity ->
            val question = Question(
                id = entity.id,
                question = entity.question,
                options = listOf(), // Update to load full options if available
                correctOptionIndex = 0
            )
            val answeredQuestion = AnsweredQuestion(
                question = question,
                correctAnswer = entity.correctAnswer,
                userAnswer = entity.userAnswer ?: ""
            )
            Pair(question, answeredQuestion)
        }
    }
}