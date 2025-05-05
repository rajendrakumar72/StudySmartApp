package com.mrkumar.studysmartapp.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task
import com.mrkumar.studysmartapp.domain.repository.SessionRepository
import com.mrkumar.studysmartapp.domain.repository.SubjectRepository
import com.mrkumar.studysmartapp.domain.repository.TaskRepository
import com.mrkumar.studysmartapp.util.SnackBarEvent
import com.mrkumar.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val subjectRepository: SubjectRepository,
                                             private val  sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository) :
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

    val tasks: StateFlow<List<Task>> = taskRepository.getAllUpcomingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    val recentSessions: StateFlow<List<Session>> = sessionRepository.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()


    fun onEvent(events: DashboardEvents){
        when(events){

            is DashboardEvents.OnDeleteSessionButtonClick ->  _state.update {
                it.copy(session = events.session)
            }
            is DashboardEvents.OnGoalStudyHoursChange ->  _state.update {
                it.copy(goalStudyHours = events.hours)
            }
            is DashboardEvents.OnSubjectCardColorChange ->  _state.update {
                it.copy(subjectCardColors = events.colors)
            }
            is DashboardEvents.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = events.name)
                }
            }
            is DashboardEvents.OnTaskIsCompleteChange -> {
                updateTask(events.task)
            }
            DashboardEvents.DeleteSession -> TODO()
            DashboardEvents.SaveSubject -> saveSubject()
        }

    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isCompleted = !task.isCompleted)
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(message = "Saved in completed tasks.")
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Couldn't update task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveSubject() {

        viewModelScope.launch {
            try {

                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull()?:1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )

                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectCardColors.random()
                    )
                }


                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Subject saved  successfully"
                    )
                )
            }catch (e: Exception){
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't save subject. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }

}