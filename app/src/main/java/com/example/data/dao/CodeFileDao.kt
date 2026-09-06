package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CodeFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeFileDao {
    @Query("SELECT * FROM code_files WHERE projectId = :projectId ORDER BY isMain DESC, fileName ASC")
    fun getFilesForProject(projectId: Long): Flow<List<CodeFileEntity>>

    @Query("SELECT * FROM code_files WHERE id = :fileId LIMIT 1")
    fun getFileById(fileId: Long): Flow<CodeFileEntity?>

    @Query("SELECT * FROM code_files WHERE id = :fileId LIMIT 1")
    suspend fun getFileByIdOnce(fileId: Long): CodeFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: CodeFileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<CodeFileEntity>)

    @Update
    suspend fun updateFile(file: CodeFileEntity)

    @Delete
    suspend fun deleteFile(file: CodeFileEntity)

    @Query("DELETE FROM code_files WHERE id = :fileId")
    suspend fun deleteFileById(fileId: Long)
}
