package com.harshvardhan.quizapp.repos.quizRepo

import com.harshvardhan.quizapp.dataModels.Question

interface QuizRepo {
    suspend fun getQuestions(): Result<List<Question>>
}