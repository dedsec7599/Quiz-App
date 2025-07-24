package com.harshvardhan.quizapp.repos

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.dataModels.Topic
import com.harshvardhan.quizapp.database.questions.QuestionEntity
import com.harshvardhan.quizapp.database.topics.TopicEntity

interface QuizDBRepo {
    suspend fun syncTopics(topics: List<Topic>)
    suspend fun getAllTopics(): List<TopicEntity>
    suspend fun completeTopicAndSaveQuestions(
        topicId: String,
        questions: List<Question>,
        userAnswers: Map<Int, String>, // questionId to userAnswer mapping
        bestStreak: Int
    )
    suspend fun getAllQuestionsForTopic(topicId: String): List<QuestionEntity>
}