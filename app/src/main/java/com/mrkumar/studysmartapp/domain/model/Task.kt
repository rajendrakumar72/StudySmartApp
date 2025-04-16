package com.mrkumar.studysmartapp.domain.model

data class Task(
    val taskSubjectId: Int,
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority:Int,
    val relatedToSubject: String,
    val isCompleted: Boolean,
    val taskId: Int
)
