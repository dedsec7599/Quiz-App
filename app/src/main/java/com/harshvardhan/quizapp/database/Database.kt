package com.harshvardhan.quizapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.harshvardhan.quizapp.database.questions.QuestionDao
import com.harshvardhan.quizapp.database.questions.QuestionEntity
import com.harshvardhan.quizapp.database.topics.TopicDao
import com.harshvardhan.quizapp.database.topics.TopicEntity

@Database(
    entities = [TopicEntity::class, QuestionEntity::class],
    version = 1,
    exportSchema = false,
)

abstract class QuizDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}