package com.harshvardhan.quizapp.repos.quizRepo

import android.content.Context
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.utils.readJsonFromRaw
import kotlinx.serialization.json.Json
import com.harshvardhan.quizapp.R

class QuizRepoImpl(
    val context: Context
): QuizRepo {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun getQuestions(): Result<List<Question>> {
        return try {
            val jsonString = readJsonFromRaw(context, R.raw.questions)
            val userList = json.decodeFromString<List<Question>>(jsonString)
            Result.success(userList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}