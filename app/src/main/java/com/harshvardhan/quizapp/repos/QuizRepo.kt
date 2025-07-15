package com.harshvardhan.quizapp.repos

import com.harshvardhan.quizapp.dataModels.Question

interface QuizRepo {
    suspend fun getQuestions(): Result<List<Question>>
}