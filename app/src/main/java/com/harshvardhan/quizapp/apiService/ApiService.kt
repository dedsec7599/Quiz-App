package com.harshvardhan.quizapp.apiService

import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.dataModels.Topic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun fetchQuizTopics(@Url url: String): Response<List<Topic>>

    @GET
    suspend fun fetchTopicQuestions(@Url url: String): Response<List<Question>>
}