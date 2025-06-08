package com.mrkumar.studysmartapp.presentation.session

import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject

data class SessionState(
    val subjects: List<Subject> = emptyList(),
    val sessions: List<Session> =emptyList(),
    val  relatedToSubject: String?=null,
    val subjectId:Int?=null,
    val session: Session?=null
)
