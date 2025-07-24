package com.harshvardhan.quizapp.database.questions

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.harshvardhan.quizapp.database.topics.TopicEntity

@Entity(
    tableName = "questions",
    primaryKeys = ["id", "topic_id"],
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
        )
    ],
    indices = [Index(value = ["topic_id"])]
)
data class QuestionEntity(
    val id: Int,
    @ColumnInfo(name = "topic_id") val topicId: String,
    val question: String,
    @ColumnInfo(name = "correct_answer") val correctAnswer: String,
    @ColumnInfo(name = "user_answer") val userAnswer: String? = null
)

@Dao
interface QuestionDao {
    // Get all questions with their answers for a specific topic
    @Query("SELECT * FROM questions WHERE topic_id = :topicId")
    suspend fun getAllQuestionsByTopic(topicId: String): List<QuestionEntity>

    // Insert/update questions for a topic
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    // Delete all questions for a specific topic
    @Query("DELETE FROM questions WHERE topic_id = :topicId")
    suspend fun deleteQuestionsByTopic(topicId: String)

    // Update user's answer for a specific question
    @Query("UPDATE questions SET user_answer = :userAnswer WHERE id = :questionId")
    suspend fun updateUserAnswer(questionId: Int, userAnswer: String)
}