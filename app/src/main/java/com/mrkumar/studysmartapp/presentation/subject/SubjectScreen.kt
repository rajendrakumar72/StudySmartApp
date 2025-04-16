package com.mrkumar.studysmartapp.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.presentation.components.AddSubjectDialog
import com.mrkumar.studysmartapp.presentation.components.CountCard
import com.mrkumar.studysmartapp.presentation.components.DeleteDialog
import com.mrkumar.studysmartapp.presentation.components.studySessionList
import com.mrkumar.studysmartapp.presentation.components.taskList
import com.mrkumar.studysmartapp.presentation.destinations.TaskScreenRouteDestination
import com.mrkumar.studysmartapp.presentation.destinations.TaskScreenRouteDestination.invoke
import com.mrkumar.studysmartapp.presentation.task.TaskScreenNavArgs
import com.mrkumar.studysmartapp.sessionList
import com.mrkumar.studysmartapp.tasks
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

data class SubjectScreenNavArgs(
    val subjectId: Int
)


@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(navigator: DestinationsNavigator) {

    val viewModel: SubjectViewModel= hiltViewModel()
    SubjectScreen(
        onBackButtonClick = { navigator.navigateUp() },
        onAddTaskButtonClick = {
            val navArg= TaskScreenNavArgs(taskId=null,subjectId = -1)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onTaskCardClick = {taskId->
            val navArg= TaskScreenNavArgs(taskId=taskId,subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
    onBackButtonClick:()-> Unit,
    onAddTaskButtonClick:()-> Unit,
    onTaskCardClick:(Int?)-> Unit
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    val isFABExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    var isEditSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteSessionDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }
    var selectedColors by remember { mutableStateOf(Subject.subjectCardColors.random()) }
    AddSubjectDialog(
        isOpen = isEditSubjectDialogOpen,
        onDismissRequest = { isEditSubjectDialogOpen = false },
        onConfirmButtonClick = { isEditSubjectDialogOpen = false },
        selectedColors = selectedColors,
        onColorChange = { selectedColors=it },
        subjectName = subjectName,
        goalHours = goalHours,
        onSubjectNameChange = { subjectName=it },
        onGoalHoursChange = { goalHours=it },
    )

    DeleteDialog(
        isOpen =isDeleteSubjectDialogOpen ,
        title = "Delete Subject?",
        onDismissRequest = {isDeleteSubjectDialogOpen=false},
        onConfirmButtonClick = { isDeleteSubjectDialogOpen=false },
        bodyText = "Are you sure, you want to delete this Subject? All related "+
                "tasks and study session will be permanently removed. This action can not be undone."
    )
    DeleteDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        onDismissRequest = {isDeleteSessionDialogOpen=false},
        onConfirmButtonClick = { isDeleteSessionDialogOpen=false },
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced " + "" +
                "by this session time. This action can not be undone."
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopAppBar(
                title = "Tamil",
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = {isDeleteSubjectDialogOpen=true },
                onEditButtonClick = {isEditSubjectDialogOpen=true },
                scrollBehavior=scrollBehavior

            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskButtonClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                },
                text = { Text(text = "Add Task") },
                expanded = isFABExpanded )
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue),
            state = listState
        ) {


            item {
                SubjectOverViewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = "10",
                    goalHours = "15",
                    progress = 0.75f
                )
            }

            taskList(
                sectionTitle = "Upcoming Tasks",
                emptyListText = "You don't have any upcoming tasks.\n " + "Click the + button in subject screen to add new task.",
                tasks = tasks,
                onCheckBoxClick = {},
                onTaskCardClicked =  onTaskCardClick
            )

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            taskList(
                sectionTitle = "Completed Tasks",
                emptyListText = "You don't have any Completed tasks.\n " + "Click the check box on completion of task.",
                tasks = tasks,
                onCheckBoxClick = {},
                onTaskCardClicked =  onTaskCardClick
            )
            studySessionList(
                sectionTitle = "Recent Study Session",
                emptyListText = "You don't have any study tasks.\n "
                        + "Start a study session to begin recording your progress.",
                sessions = sessionList,
                onDeleteIconClick = {isDeleteSessionDialogOpen=true}
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreenTopAppBar(
    title: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "navigate back"
                )
            }
        }, title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(onClick = onDeleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete subject"
                )
            }

            IconButton(onClick = onEditButtonClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit subject"
                )
            }
        }
    )
}

@Composable
private fun SubjectOverViewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHours: String,
    progress: Float
) {

    val percentageProgress = remember(progress) {
        (progress * 100).toInt().coerceIn(0, 100)
    }

    Row(
        modifier = modifier, horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Study Hours",
            count = studiedHours
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )

            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )

            Text(text = "$percentageProgress%")
        }

    }

}