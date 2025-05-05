package com.mrkumar.studysmartapp.presentation.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkumar.studysmartapp.domain.model.Task
import com.mrkumar.studysmartapp.domain.repository.SubjectRepository
import com.mrkumar.studysmartapp.domain.repository.TaskRepository
import com.mrkumar.studysmartapp.presentation.navArgs
import com.mrkumar.studysmartapp.util.Priority
import com.mrkumar.studysmartapp.util.SnackBarEvent
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {


    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ) { state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.OnTitleChange -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }

            is TaskEvent.OnDescriptionChange -> {
                _state.update {
                    it.copy(description = event.description)
                }
            }

            is TaskEvent.OnDateChange -> {
                _state.update {
                    it.copy(dueDate = event.millis)
                }
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update {
                    it.copy(priority = event.priority)
                }
            }

            TaskEvent.OnIsCompleteChange -> {
                _state.update {
                    it.copy(isTaskComplete = !_state.value.isTaskComplete)
                }
            }

            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            TaskEvent.SaveTask -> saveTask()
            TaskEvent.DeleteTask -> deleteTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                val currentTaskId = state.value.currentTaskID
                if (currentTaskId != null) {
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(taskId = currentTaskId)
                    }
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "Task deleted successfully")
                    )
                    _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
                } else {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "No Task to delete")
                    )
                }
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete task. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            val state = _state.value
            if (state.subjectId == null || state.relatedToSubject == null) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Please select subject related to the task"
                    )
                )
                return@launch
            }
            try {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.title,
                        description = state.description,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        relatedToSubject = state.relatedToSubject,
                        priority = state.priority.value,
                        isCompleted = state.isTaskComplete,
                        taskSubjectId = state.subjectId,
                        taskId = state.currentTaskID
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(message = "Task Saved Successfully")
                )
                _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't save task. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchTask() {
        viewModelScope.launch {
            navArgs.taskId?.let { id ->
                taskRepository.getTaskById(id)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            isTaskComplete = task.isCompleted,
                            relatedToSubject = task.relatedToSubject,
                            priority = Priority.fromInt(task.priority),
                            subjectId = task.taskSubjectId,
                            currentTaskID = task.taskId
                        )
                    }
                }
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            navArgs.subjectId?.let { id ->
                subjectRepository.getSubjectById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subjectId,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }
}