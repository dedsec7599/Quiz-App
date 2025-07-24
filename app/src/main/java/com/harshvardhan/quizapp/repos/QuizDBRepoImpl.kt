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
        userAnswers: Map<Int, String> // questionId to userAnswer mapping
    ) {
        // Mark topic as completed
        topicDao.markTopicCompleted(topicId)

        // Save questions with user answers to database
        val questionEntities = questions.map { question ->
            val userAnswer = userAnswers[question.id] // Get user's answer for this question
            QuestionEntity(
                id = question.id,
                topicId = topicId,
                question = question.question,
                correctAnswer = question.options[question.correctOptionIndex], // Store actual correct answer
                userAnswer = userAnswer // Store user's answer (can be null if not answered)
            )
        }
        questionDao.insertQuestions(questionEntities)
    }

    // Get all questions for a topic (with user answers)
    override suspend fun getAllQuestionsForTopic(topicId: String): List<QuestionEntity> {
        return questionDao.getAllQuestionsByTopic(topicId)
    }

    // Submit user answer
    override suspend fun submitAnswer(questionId: Int, userAnswer: String) {
        questionDao.updateUserAnswer(questionId, userAnswer)
    }
}