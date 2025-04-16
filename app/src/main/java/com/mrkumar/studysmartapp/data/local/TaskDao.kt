package com.mrkumar.studysmartapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(subject: Task)


    @Query("SELECT * FROM Task")
    fun getAllTasks(): Flow<List<Task>>


    @Query("SELECT * FROM Task WHERE taskSubjectId = :subjectId")
    fun getTaskForSubject(subjectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("DELETE  FROM Task WHERE taskSubjectId = :subjectId")
    suspend fun deleteTasksBySubject(subjectId: Int)

    @Query("DELETE  FROM Task WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)


}