package com.harshvardhan.quizapp.database.topics

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import com.harshvardhan.quizapp.dataModels.Topic

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "questions_url") val url: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "best_streak") val bestStreak: Int = 0
)

@Dao
interface TopicDao {
    // Get all topics
    @Query("SELECT * FROM topics")
    suspend fun getAllTopics(): List<TopicEntity>

    // Get a specific topic by ID
    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopicById(topicId: String): TopicEntity?

    // Insert/update topics
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    // Mark topic as completed
    @Query("UPDATE topics SET is_completed = 1, best_streak = :bestStreak WHERE id = :topicId")
    suspend fun markTopicCompleted(topicId: String, bestStreak: Int)

    // Delete topics that are not in the provided list from api
    @Query("DELETE FROM topics WHERE id NOT IN (:topicIds)")
    suspend fun deleteTopicsNotInList(topicIds: List<String>)

    @Transaction
    suspend fun syncTopics(newTopics: List<Topic>, deleteMissing: Boolean = false) {
        val topicEntities = newTopics.map { topic ->
            val existing = getTopicById(topic.id)
            TopicEntity(
                id = topic.id,
                title = topic.title,
                description = topic.description,
                url = topic.url,
                isCompleted = existing?.isCompleted == true
            )
        }

        insertTopics(topicEntities)

        deleteTopicsNotInList(newTopics.map { it.id })
    }
}