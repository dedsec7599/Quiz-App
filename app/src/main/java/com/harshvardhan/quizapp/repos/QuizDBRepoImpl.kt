package com.harshvardhan.quizapp.repos

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.dataModels.Topic
import com.harshvardhan.quizapp.database.QuizDatabase
import com.harshvardhan.quizapp.database.questions.QuestionEntity
import com.harshvardhan.quizapp.database.topics.TopicEntity

class QuizDBRepoImpl(database: QuizDatabase) : QuizDBRepo {
    private val topicDao = database.topicDao()
    private val questionDao = database.questionDao()

    override suspend fun syncTopics(topics: List<Topic>) {
        topicDao.syncTopics(topics)
    }

    override suspend fun getAllTopics(): List<TopicEntity> {
        return topicDao.getAllTopics()
    }

    override suspend fun completeTopicAndSaveQuestions(
        topicId: String,
        questions: List<Question>,
        userAnswers: Map<Int, String>, // questionId to userAnswer mapping
        bestStreak: Int
    ) {
        // Mark topic as completed
        topicDao.markTopicCompleted(topicId, bestStreak)

        // Delete old questions for this topic to avoid stale data
        questionDao.deleteQuestionsByTopic(topicId)

        // Convert and insert fresh questions
        val questionEntities = questions.map { question ->
            val userAnswer = userAnswers[question.id]
            QuestionEntity(
                id = question.id,
                topicId = topicId,
                question = question.question,
                correctAnswer = question.options[question.correctOptionIndex],
                userAnswer = userAnswer
            )
        }
        questionDao.insertQuestions(questionEntities)
    }

    // Get all questions for a topic
    override suspend fun getAllQuestionsForTopic(topicId: String): List<QuestionEntity> {
        return questionDao.getAllQuestionsByTopic(topicId)
    }
}