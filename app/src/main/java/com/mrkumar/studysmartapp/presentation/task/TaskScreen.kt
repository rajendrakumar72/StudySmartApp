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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.mrkumar.studysmartapp.presentation.components.DeleteDialog
import com.mrkumar.studysmartapp.presentation.components.SubjectListBottomSheet
import com.mrkumar.studysmartapp.presentation.components.TaskCheckBox
import com.mrkumar.studysmartapp.presentation.components.TaskDatePicker
import com.mrkumar.studysmartapp.presentation.theme.Red
import com.mrkumar.studysmartapp.subjectList
import com.mrkumar.studysmartapp.util.FutureDatesOnlySelectableDates
import com.mrkumar.studysmartapp.util.Priority
import com.mrkumar.studysmartapp.util.changeMillsToDateString
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(modifier: Modifier = Modifier) {

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }


    val datePickerState= rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
        selectableDates = FutureDatesOnlySelectableDates()
    )

    val scope = rememberCoroutineScope()
    val sheetState= rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var taskTitleError by rememberSaveable { mutableStateOf<String?>(null) }



    taskTitleError = when {
        title.isBlank()-> "Please enter task title."
        title.length<4 -> "Task title is too short."
        title.length>30 -> "Task title is too long"
        else -> null
    }

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        onDismissRequest = { isDeleteDialogOpen=false },
        onConfirmButtonClick = {isDeleteDialogOpen=false},
        bodyText = "Are you sure, you want to delete this task? "+
                "This action can not be undone."
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen=false },
        onConfirmButtonClick = { isDatePickerDialogOpen=false }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjectList,
        onSubjectClicked = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible)isBottomSheetOpen=false
            }
        },
        onDismissRequest = {isBottomSheetOpen=false}
    )
    Scaffold(topBar = {
        TaskScreenTopBar(
            isTaskExist = true,
            isComplete = false,
            checkBoxBorderColor = Red,
            onBackButtonClick = { },
            onDeleteButtonClick = { isDeleteDialogOpen=true},
            onCheckBoxClick = { }
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
                value = title,
                onValueChange = {title=it},
                label = {Text(text = "Title")},
                singleLine = true,
                isError = taskTitleError !=null && title.isNotBlank(),
                supportingText = {Text(text = taskTitleError.orEmpty())},


            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = {description=it},
                label = {Text(text = "Description")},
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Due Date",
                style = MaterialTheme.typography.bodySmall)

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                Text(text = datePickerState.selectedDateMillis.changeMillsToDateString(),
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
                        borderColor = if (priority == Priority.HIGH) Color.White else Color.Transparent,
                        labelColor = if (priority == Priority.HIGH) Color.White else Color.White.copy(alpha = 0.7f),
                        onClick = {  }
                    )
                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Related To Subject",
                style = MaterialTheme.typography.bodySmall)

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                Text(text = "Tamil",
                    style = MaterialTheme.typography.bodyLarge)

                IconButton(onClick = {isBottomSheetOpen=true}) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )
                }
            }

            Button(onClick = {},
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