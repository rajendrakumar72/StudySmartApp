package com.mrkumar.studysmartapp.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrkumar.studysmartapp.R
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Subject
import com.mrkumar.studysmartapp.domain.model.Task
import com.mrkumar.studysmartapp.presentation.components.AddSubjectDialog
import com.mrkumar.studysmartapp.presentation.components.CountCard
import com.mrkumar.studysmartapp.presentation.components.DeleteDialog
import com.mrkumar.studysmartapp.presentation.components.SubjectCard
import com.mrkumar.studysmartapp.presentation.components.studySessionList
import com.mrkumar.studysmartapp.presentation.components.taskList
import com.mrkumar.studysmartapp.presentation.destinations.SessionScreenRouteDestination
import com.mrkumar.studysmartapp.presentation.destinations.SubjectScreenRouteDestination
import com.mrkumar.studysmartapp.presentation.destinations.TaskScreenRouteDestination
import com.mrkumar.studysmartapp.presentation.subject.SubjectScreenNavArgs
import com.mrkumar.studysmartapp.presentation.task.TaskScreenNavArgs
import com.mrkumar.studysmartapp.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@RootNavGraph(start = true)
@Destination()
@Composable
fun DashBoardRoute(navigator: DestinationsNavigator) {

    val viewModel: DashboardViewModel= hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    DashBoardScreen(
        onSubjectCardClick = {subjectId->
            subjectId?.let {
                val navArg= SubjectScreenNavArgs(subjectId=subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))
            }
        },
        onTaskCardClick = {taskId ->
            val navArg= TaskScreenNavArgs(taskId=taskId,subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onStartSessionButtonClick = {navigator.navigate(SessionScreenRouteDestination)},
        state = state,
        onEvent = viewModel::onEvent,
        tasks = tasks,
        recentSessions = recentSessions,
        snackBarEvent = viewModel.snackBarEventFlow
    )
}


@Composable
private fun DashBoardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentSessions: List<Session>,
    onEvent:(DashboardEvents)-> Unit,
    onSubjectCardClick:(Int?)-> Unit,
    onTaskCardClick:(Int?)-> Unit,
    onStartSessionButtonClick:()-> Unit,
    snackBarEvent : SharedFlow<SnackBarEvent>
) {



    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest{event ->
            when(event){
                is SnackBarEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> {}
            }
        }
    }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvents.SaveSubject)
            isAddSubjectDialogOpen = false },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(DashboardEvents.OnSubjectCardColorChange(it))},
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(DashboardEvents.OnSubjectNameChange(it)) },
        onGoalHoursChange = { onEvent(DashboardEvents.OnGoalStudyHoursChange(it)) },
    )

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Session",
        onDismissRequest = {isDeleteDialogOpen=false},
        onConfirmButtonClick = {
            onEvent(DashboardEvents.DeleteSession)
            isDeleteDialogOpen=false },
        bodyText = "Are you sure, you want to delete this session? Your studied hours will be reduced " + "" +
                "by this session time. This action can not be undo."
    )

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) }, topBar = { DashBoardScreenTopBar() }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            item {
                CountCardSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString()
                )
            }

            item {
                SubjectCardSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClicked = {isAddSubjectDialogOpen = true},
                    onSubjectCardClick = onSubjectCardClick

                    )
            }

            item {
                Button(
                    onClick = onStartSessionButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {

                    Text(text = "Start Study Session")
                }
            }
            taskList(
                sectionTitle = "Upcoming Tasks",
                emptyListText = "You don't have any upcoming tasks.\n " + "Click the + button in subject screen to add new task.",
                tasks = tasks,
                onCheckBoxClick = {onEvent(DashboardEvents.OnTaskIsCompleteChange(it))},
                onTaskCardClicked = onTaskCardClick
            )

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            studySessionList(
                sectionTitle = "Recent Study Session",
                emptyListText = "You don't have any study tasks.\n "
                        + "Start a study session to begin recording your progress.",
                sessions = recentSessions,
                onDeleteIconClick = {isDeleteDialogOpen=true}
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashBoardScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "StudySmart App",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
private fun CountCardSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String
) {
    Row(modifier = modifier) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount"
        )

        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )

    }
}

@Composable
private fun SubjectCardSection(
    modifier: Modifier,
    subjectList: List<Subject>,
    emptyListText: String = "You don't have any subjects.\n  Click the + button to add new subject.",
    onAddIconClicked:()-> Unit,
    onSubjectCardClick:(Int?)-> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )

            IconButton(onClick = onAddIconClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }

        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_books),
                contentDescription = emptyListText
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        LazyRow(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) { subject ->
                SubjectCard(
                    subjectName = subject.name,
                    gradientColor = subject.colors.map { Color(it) },
                    onClick = {onSubjectCardClick(subject.subjectId)}
                )
            }

        }

    }

}