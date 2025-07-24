package com.harshvardhan.quizapp.repos

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.dataModels.Topic

interface QuizRepo {
    suspend fun fetchQuizTopics(): Result<List<Topic>>
    suspend fun fetchTopicQuestions(url: String): Result<List<Question>>
}