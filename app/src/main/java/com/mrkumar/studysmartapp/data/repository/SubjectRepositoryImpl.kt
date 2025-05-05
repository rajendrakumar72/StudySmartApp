package com.mrkumar.studysmartapp.data.repository

import com.mrkumar.studysmartapp.data.local.SessionDao
import com.mrkumar.studysmartapp.data.local.SubjectDao
import com.mrkumar.studysmartapp.data.local.TaskDao
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(private val subjectDao: SubjectDao
,private val taskDao: TaskDao, private val sessionDao: SessionDao): SubjectRepository {


    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTask(subjectId)
        sessionDao.deleteSessionBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }
}