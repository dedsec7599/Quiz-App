package com.harshvardhan.quizapp.usecases.dbUseCase

import com.harshvardhan.quizapp.database.topics.TopicEntity
import com.harshvardhan.quizapp.repos.QuizDBRepo

class FetchTopicsFromDbUseCase(
    private val quizDbRepo: QuizDBRepo
) {
    suspend operator fun invoke(): List<TopicEntity> {
        return quizDbRepo.getAllTopics()
    }
}