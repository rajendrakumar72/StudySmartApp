package com.mrkumar.studysmartapp.presentation.session

import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject

sealed class SessionEvent {

    data class OnRelatedSubjectChange(val subject: Subject): SessionEvent()

    data class SaveSession(val duration: Long): SessionEvent()

    data class OnDeleteSessionButtonClick(val session: Session): SessionEvent()

    data object DeleteSession: SessionEvent()

    data object CheckSubjectId: SessionEvent()

    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: Int?,
        val relatedToSubject: String?
    ): SessionEvent()

    data object NotifyToUpdateSubject : SessionEvent()

}