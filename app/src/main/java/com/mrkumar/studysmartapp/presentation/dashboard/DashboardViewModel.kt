package com.mrkumar.studysmartapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkumar.studysmartapp.domain.repository.SessionRepository
import com.mrkumar.studysmartapp.domain.repository.SubjectRepository
import com.mrkumar.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val subjectRepository: SubjectRepository,
                                             sessionRepository: SessionRepository) :
    ViewModel() {

        private val _state = MutableStateFlow(DashboardState())
        val state = combine(
            _state,
            subjectRepository.getTotalSubjectCount(),
            subjectRepository.getTotalGoalHours(),
            subjectRepository.getAllSubjects(),
            sessionRepository.getTotalSessionsDuration()
        ){state,subjectCount,goalHours,subjects,totalSessionDuration ->
            state.copy(
                totalSubjectCount = subjectCount,
                totalGoalStudyHours = goalHours,
                subjects = subjects,
                totalStudiedHours = totalSessionDuration.toHours()
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState()
        )



}