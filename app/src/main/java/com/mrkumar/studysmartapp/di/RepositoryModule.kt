package com.mrkumar.studysmartapp.di

import com.mrkumar.studysmartapp.data.repository.SessionRepositoryImpl
import com.mrkumar.studysmartapp.data.repository.SubjectRepositoryImpl
import com.mrkumar.studysmartapp.data.repository.TaskRepositoryImpl
import com.mrkumar.studysmartapp.domain.repository.SessionRepository
import com.mrkumar.studysmartapp.domain.repository.SubjectRepository
import com.mrkumar.studysmartapp.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepository(
        impl: SubjectRepositoryImpl
    ): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        impl: SessionRepositoryImpl
    ): SessionRepository
}