package com.mrkumar.studysmartapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    val taskSubjectId: Int,
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority:Int,
    val relatedToSubject: String,
    val isCompleted: Boolean,
    @PrimaryKey(autoGenerate = true)
    val taskId: Int?=null
)
