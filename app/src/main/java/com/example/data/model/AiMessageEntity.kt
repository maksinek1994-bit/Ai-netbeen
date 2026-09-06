package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_messages")
data class AiMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val role: String, // "user", "model"
    val content: String,
    val mode: String = "chat", // "chat", "debug", "generate", "explain"
    val timestamp: Long = System.currentTimeMillis()
)
