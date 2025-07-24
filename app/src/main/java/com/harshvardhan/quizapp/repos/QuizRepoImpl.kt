package com.harshvardhan.quizapp.repos

import com.harshvardhan.harsh_vardhan.apiService.CustomException.ClientException
import com.harshvardhan.harsh_vardhan.apiService.CustomException.EmptyContentException
import com.harshvardhan.harsh_vardhan.apiService.CustomException.NotFoundException
import com.harshvardhan.harsh_vardhan.apiService.CustomException.ServerException
import com.harshvardhan.harsh_vardhan.apiService.CustomException.UnknownException
import com.harshvardhan.quizapp.apiService.ApiService
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.dataModels.Topic

class QuizRepoImpl(
    val apiService: ApiService
): QuizRepo {
    override suspend fun fetchTopicQuestions(url: String): Result<List<Question>> {
        return try {
            val response = apiService.fetchTopicQuestions(url)

            when {
                response.isSuccessful -> {
                    val topics = response.body()
                    if (topics.isNullOrEmpty()) {
                        Result.failure(EmptyContentException("No questions found"))
                    } else {
                        Result.success(topics)
                    }
                }
                response.code() == 404 -> {
                    Result.failure(NotFoundException("Url not found"))
                }
                response.code() in 400..499 -> {
                    Result.failure(ClientException("Invalid request: ${response.message()}"))
                }
                response.code() in 500..599 -> {
                    Result.failure(ServerException("Server error: ${response.message()}"))
                }
                else -> {
                    Result.failure(UnknownException("HTTP ${response.code()}: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchQuizTopics(): Result<List<Topic>> {
        return try {
            val url = "https://gist.githubusercontent.com/dr-samrat/ee986f16da9d8303c1acfd364ece22c5/raw"
            val response = apiService.fetchQuizTopics(url)

            when {
                response.isSuccessful -> {
                    val topics = response.body()
                    if (topics.isNullOrEmpty()) {
                        Result.failure(EmptyContentException("No topics found"))
                    } else {
                        Result.success(topics)
                    }
                }
                response.code() == 404 -> {
                    Result.failure(NotFoundException("Url not found"))
                }
                response.code() in 400..499 -> {
                    Result.failure(ClientException("Invalid request: ${response.message()}"))
                }
                response.code() in 500..599 -> {
                    Result.failure(ServerException("Server error: ${response.message()}"))
                }
                else -> {
                    Result.failure(UnknownException("HTTP ${response.code()}: ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}