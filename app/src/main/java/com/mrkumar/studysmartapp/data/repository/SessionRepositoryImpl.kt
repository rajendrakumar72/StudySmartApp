package com.mrkumar.studysmartapp.data.repository

import com.mrkumar.studysmartapp.data.local.SessionDao
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(private val sessionDao: SessionDao): SessionRepository {

    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        TODO("Not yet implemented")
    }

    override fun getAllSessions(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentTenSessionsForSubjects(subjectId: Int): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
       return sessionDao.getTotalSessionsDuration()
    }

    override fun getTotalSessionsDurationBySubjectId(subjectId: Int): Flow<Long> {
        TODO("Not yet implemented")
    }
}