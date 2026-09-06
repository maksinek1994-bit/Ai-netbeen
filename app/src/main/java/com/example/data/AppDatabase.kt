package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AiMessageDao
import com.example.data.dao.CodeFileDao
import com.example.data.dao.ProjectDao
import com.example.data.model.AiMessageEntity
import com.example.data.model.CodeFileEntity
import com.example.data.model.ProjectEntity

@Database(
    entities = [ProjectEntity::class, CodeFileEntity::class, AiMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun codeFileDao(): CodeFileDao
    abstract fun aiMessageDao(): AiMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "netbeans_studio.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
