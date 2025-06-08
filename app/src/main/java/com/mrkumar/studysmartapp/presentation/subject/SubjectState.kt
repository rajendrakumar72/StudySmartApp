package com.mrkumar.studysmartapp.presentation.subject

import androidx.compose.ui.graphics.Color
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task

data class SubjectState(
    val currentSubjectId: Int?=null,
    val subjectName: String="",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val goalStudyHours: String="",
    val studiedHours: Float=0f,
    val progress: Float=0f,
    val recentSessions: List<Session> =emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session?=null,
)
