package com.harshvardhan.quizapp.usecases.quizSelectionUseCase

import com.harshvardhan.quizapp.dataModels.Topic
import com.harshvardhan.quizapp.repos.QuizDBRepo

class SyncDbTopicsUseCase(
    private val quizDbRepo: QuizDBRepo
) {
    suspend operator fun invoke(topics: List<Topic>): Result<Unit> {
        return try {
            quizDbRepo.syncTopics(topics)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}