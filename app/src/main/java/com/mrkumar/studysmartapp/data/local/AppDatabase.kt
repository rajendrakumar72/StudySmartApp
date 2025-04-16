package com.mrkumar.studysmartapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task


@Database(entities = [Subject::class, Session::class, Task::class],
    version = 1)
@TypeConverters(ColorListConverter::class)
abstract class AppDatabase:RoomDatabase(){

    abstract fun subjectDao(): SubjectDao

    abstract fun taskDao(): TaskDao

    abstract fun sessionDao(): SessionDao
}