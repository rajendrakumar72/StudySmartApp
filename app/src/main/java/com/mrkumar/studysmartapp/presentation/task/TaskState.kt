package com.mrkumar.studysmartapp.presentation.task

import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.util.Priority

data class TaskState(
    val title: String ="",
    val description: String ="",
    val dueDate: Long?=null,
    val isTaskComplete: Boolean=false,
    val priority: Priority= Priority.LOW,
    val relatedToSubject: String?=null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: Int?=null,
    val currentTaskID: Int?=null

)
