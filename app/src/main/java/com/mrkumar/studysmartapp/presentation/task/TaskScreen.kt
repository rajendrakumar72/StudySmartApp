package com.mrkumar.studysmartapp.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrkumar.studysmartapp.presentation.components.DeleteDialog
import com.mrkumar.studysmartapp.presentation.components.SubjectListBottomSheet
import com.mrkumar.studysmartapp.presentation.components.TaskCheckBox
import com.mrkumar.studysmartapp.presentation.components.TaskDatePicker
import com.mrkumar.studysmartapp.util.FutureDatesOnlySelectableDates
import com.mrkumar.studysmartapp.util.Priority
import com.mrkumar.studysmartapp.util.SnackBarEvent
import com.mrkumar.studysmartapp.util.changeMillsToDateString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant


data class TaskScreenNavArgs(
    val taskId: Int?,
    val subjectId: Int?
)
@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(navigator: DestinationsNavigator) {

    val viewModel: TaskViewModel= hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskScreen(
        onBackButtonClick = {
            navigator.navigateUp()
        },
        state = state,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackBarEventFlow
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(state: TaskState,
                       onEvent:(TaskEvent)-> Unit,
    onBackButtonClick:()-> Unit,
snackBarEvent: SharedFlow<SnackBarEvent>) {

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }


    val datePickerState= rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
        selectableDates = FutureDatesOnlySelectableDates()
    )

    val scope = rememberCoroutineScope()
    val sheetState= rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }



    var taskTitleError by rememberSaveable { mutableStateOf<String?>(null) }



    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when (event) {
                is SnackBarEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> {
                    onBackButtonClick()
                }

            }
        }
    }
    taskTitleError = when {
        state.title.isBlank()-> "Please enter task title."
        state.title.length<4 -> "Task title is too short."
        state.title.length>30 -> "Task title is too long"
        else -> null
    }

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        onDismissRequest = { isDeleteDialogOpen=false },
        onConfirmButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            isDeleteDialogOpen=false},
        bodyText = "Are you sure, you want to delete this task? "+
                "This action can not be undone."
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen=false },
        onConfirmButtonClick = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis))
            isDatePickerDialogOpen=false }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = {subject->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)isBottomSheetOpen=false
            }
            onEvent(TaskEvent.OnRelatedSubjectSelect(subject))
        },
        onDismissRequest = {isBottomSheetOpen=false}
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },

        topBar = {
        TaskScreenTopBar(
            isTaskExist = state.currentTaskID!=null,
            isComplete = state.isTaskComplete,
            checkBoxBorderColor = state.priority.color,
            onBackButtonClick = onBackButtonClick,
            onDeleteButtonClick = { isDeleteDialogOpen=true},
            onCheckBoxClick = {onEvent(TaskEvent.OnIsCompleteChange) }
        )

    }) { innerPadding ->

        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {onEvent(TaskEvent.OnTitleChange(it))},
                label = {Text(text = "Title")},
                singleLine = true,
                isError = taskTitleError !=null && state.title.isNotBlank(),
                supportingText = {Text(text = taskTitleError.orEmpty())},


            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = {onEvent(TaskEvent.OnDescriptionChange(it))},
                label = {Text(text = "Description")},
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Due Date",
                style = MaterialTheme.typography.bodySmall)

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                Text(text = state.dueDate.changeMillsToDateString(),
                    style = MaterialTheme.typography.bodyLarge)

                IconButton(onClick = {isDatePickerDialogOpen=true}) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date"
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Priority",
                style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Priority.entries.forEach { priority->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if (priority == state.priority) Color.White else Color.Transparent,
                        labelColor = if (priority == state.priority) Color.White else Color.White.copy(alpha = 0.7f),
                        onClick = {  onEvent(TaskEvent.OnPriorityChange(priority))}
                    )
                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Related To Subject",
                style = MaterialTheme.typography.bodySmall)

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                val firstSubject=state.subjects.firstOrNull()?.name ?:""
                Text(text = state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge)

                IconButton(onClick = {isBottomSheetOpen=true}) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )
                }
            }

            Button(onClick = { onEvent(TaskEvent.SaveTask) },
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)) {

                Text(text = "Save")


            }



        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    isTaskExist: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit,
) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        title = {
            Text(text = "Task", style = MaterialTheme.typography.headlineSmall)
        },
        actions = {
            if (isTaskExist) {
                TaskCheckBox(
                    isCompleted = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckedBoxClick = onCheckBoxClick
                )

                IconButton(onClick = onDeleteButtonClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task"
                    )
                }
            }
        }
    )

}

@Composable
private fun PriorityButton(modifier: Modifier = Modifier,
                           label: String,
                           backgroundColor:Color,
                           borderColor: Color,
                           labelColor: Color,
                           onClick:()-> Unit) {

    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(5.dp)
            .border(
                1.dp, borderColor,
                RoundedCornerShape(5.dp)
            )
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ){

        Text(text = label, color = labelColor)
    }
}