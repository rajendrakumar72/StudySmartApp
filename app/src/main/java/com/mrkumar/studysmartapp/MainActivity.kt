package com.mrkumar.studysmartapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task
import com.mrkumar.studysmartapp.presentation.dashboard.DashBoardScreen
import com.mrkumar.studysmartapp.presentation.session.SessionScreen
import com.mrkumar.studysmartapp.presentation.subject.SubjectScreen
import com.mrkumar.studysmartapp.presentation.task.TaskScreen
import com.mrkumar.studysmartapp.presentation.theme.StudySmartAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartAppTheme {
                SessionScreen()
            }
        }
    }
}

val subjectList = listOf(
    Subject(name = "Tamil", goalHours = 10f, colors = Subject.subjectCardColors[0], subjectId = 0),
    Subject(name = "English", goalHours = 10f, colors = Subject.subjectCardColors[1], subjectId = 0),
    Subject(name = "Maths", goalHours = 10f, colors = Subject.subjectCardColors[2], subjectId = 0),
    Subject(name = "Science", goalHours = 10f, colors = Subject.subjectCardColors[3], subjectId = 0),
    Subject(name = "Social", goalHours = 10f, colors = Subject.subjectCardColors[4], subjectId = 0),
)

val tasks = listOf(
    Task(
        title = "Prepare Notes",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isCompleted = false,
        taskSubjectId = 0,
        taskId = 0
    ),
    Task(
        title = "Do HomeWork",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isCompleted = true,
        taskSubjectId = 0,
        taskId = 0
    ),
    Task(
        title = "Coding Task",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isCompleted = true,
        taskSubjectId = 0,
        taskId = 0
    ),
    Task(
        title = "DSA",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isCompleted = true,
        taskSubjectId = 0,
        taskId = 0
    ),
    Task(
        title = "Kotlin",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isCompleted = true,
        taskSubjectId = 0,
        taskId = 0
    ),
    Task(
        title = "Spring",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isCompleted = true,
        taskSubjectId = 0,
        taskId = 0
    ),
)

val sessionList=listOf(
    Session(
        sessionSubjectId = 0,
        relatedToSubject = "Tamil",
        date = 0L,
        duration = 2,
        sessionId = 0
    ),
    Session(
        sessionSubjectId = 0,
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionId = 0
    ),
    Session(
        sessionSubjectId = 0,
        relatedToSubject = "Maths",
        date = 0L,
        duration = 2,
        sessionId = 0
    ),
    Session(
        sessionSubjectId = 0,
        relatedToSubject = "Science",
        date = 0L,
        duration = 2,
        sessionId = 0
    ),
    Session(
        sessionSubjectId = 0,
        relatedToSubject = "Social",
        date = 0L,
        duration = 2,
        sessionId = 0
    ),
)

