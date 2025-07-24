package com.harshvardhan.quizapp.usecases.dbUseCase

import com.harshvardhan.quizapp.database.questions.QuestionEntity
import com.harshvardhan.quizapp.repos.QuizDBRepo

class FetchTopicQuestionsForReviewUseCase(
    private val quizDbRepo: QuizDBRepo
) {
    suspend operator fun invoke(topicId: String): List<QuestionEntity> {
        return quizDbRepo.getAllQuestionsForTopic(topicId)
    }

    suspend fun getQuestionsIfTopicCompleted(topicId: String): List<QuestionEntity>? {
        val topics = quizDbRepo.getAllTopics()
        val topic = topics.find { it.id == topicId }

        return if (topic?.isCompleted == true) {
            quizDbRepo.getAllQuestionsForTopic(topicId)
        } else {
            null
        }
    }
}