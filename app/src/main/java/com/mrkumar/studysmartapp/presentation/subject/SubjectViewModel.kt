package com.mrkumar.studysmartapp.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task
import com.mrkumar.studysmartapp.domain.repository.SessionRepository
import com.mrkumar.studysmartapp.domain.repository.SubjectRepository
import com.mrkumar.studysmartapp.domain.repository.TaskRepository
import com.mrkumar.studysmartapp.presentation.navArgs
import com.mrkumar.studysmartapp.util.SnackBarEvent
import com.mrkumar.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository, private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle): ViewModel() {

        private val navArgs: SubjectScreenNavArgs =savedStateHandle.navArgs()

    private val _state = MutableStateFlow(SubjectState())


    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubjects(navArgs.subjectId),
        sessionRepository.getTotalSessionsDurationBySubject(navArgs.subjectId)
    ){state, upcomingTask, completedTask, recentSession, totalSessionsDuration ->

        state.copy(
            upcomingTasks = upcomingTask,
            completedTasks = completedTask,
            recentSessions = recentSession,
            studiedHours = totalSessionsDuration.toHours()
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()
    init {
        fetchSubject()

    }

    fun onEvent(event: SubjectEvent){
        when(event){

            is SubjectEvent.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.DeleteSession -> {

            }
            SubjectEvent.DeleteSubject -> deleteSubject()

            is SubjectEvent.OnDeleteSessionButtonClick -> {

            }
            is SubjectEvent.OnTaskIsCompleteChange ->{updateTask(event.task)}
            SubjectEvent.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f,1f)
                    )
                }
            }
        }
    }

    private fun updateSubject() {

            viewModelScope.launch {
                try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )

                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Subject updated successfully"
                    )
                )

            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update subject. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
            }

    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isCompleted = !task.isCompleted)
                )
                if (task.isCompleted) {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "Saved in upcoming tasks.")
                    )
                } else {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "Saved in completed tasks.")
                    )
                }
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update task. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
    private fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let { subject ->

                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) },
                        currentSubjectId = subject.subjectId
                    )
                }

            }
        }
    }

    private fun deleteSubject(){
        viewModelScope.launch {
            try {
                val currentSubjectId= state.value.currentSubjectId

                if (currentSubjectId!=null){
                    withContext(Dispatchers.IO) {
                        state.value.currentSubjectId?.let {
                            subjectRepository.deleteSubject(subjectId = it)
                        }
                    }

                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Subject deleted successfully"
                        )
                    )
                    _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
                }else{
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "No subject to delete"
                        )
                    )
                }


            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete subject. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }
}