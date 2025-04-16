package com.mrkumar.studysmartapp.domain.repository

import com.mrkumar.studysmartapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentFiveSessions():Flow<List<Session>>

    fun getRecentTenSessionsForSubjects(subjectId: Int):Flow<List<Session>>

    fun getTotalSessionsDuration():Flow<Long>

    fun getTotalSessionsDurationBySubjectId(subjectId: Int): Flow<Long>


}