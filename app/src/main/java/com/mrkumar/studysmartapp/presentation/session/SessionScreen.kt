package com.mrkumar.studysmartapp.presentation.session

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrkumar.studysmartapp.presentation.components.DeleteDialog
import com.mrkumar.studysmartapp.presentation.components.SubjectListBottomSheet
import com.mrkumar.studysmartapp.presentation.components.studySessionList
import com.mrkumar.studysmartapp.presentation.subject.ServiceHelper
import com.mrkumar.studysmartapp.sessionList
import com.mrkumar.studysmartapp.subjectList
import com.mrkumar.studysmartapp.util.Constants.ACTION_SERVICE_CANCEL
import com.mrkumar.studysmartapp.util.Constants.ACTION_SERVICE_START
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun SessionScreenRoute(navigator: DestinationsNavigator) {

    val viewModel: SessionViewModel= hiltViewModel()
    SessionScreen(
        onBackButtonClick = {
            navigator.navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(onBackButtonClick: () -> Unit) {

    val scope = rememberCoroutineScope()
    val sheetState= rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    val context= LocalContext.current


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

    DeleteDialog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        onDismissRequest = { isDeleteDialogOpen=false },
        onConfirmButtonClick = {isDeleteDialogOpen=false},
        bodyText = "Are you sure, you want to delete this session? "+
                "This action can not be undone."
    )
    Scaffold(topBar = {
        SessionScreenTopBar(onBackButtonClick = onBackButtonClick)
    }) {innerPadding->

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            item { 
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            item {
                RelatedSubjectSession(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = "Tamil",
                    selectSubjectButtonClick = { isBottomSheetOpen=true }
                )
            }

            item {
                ButtonSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {
                        ServiceHelper.triggerForegroundService(context=context,
                            action = ACTION_SERVICE_START) },

                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(context=context,
                        action = ACTION_SERVICE_CANCEL) },
                    finishButtonClick = {  }
                )
            }

            studySessionList(
                sectionTitle = "Study Session History",
                emptyListText = "You don't have any study tasks.\n "
                        + "Start a study session to begin recording your progress.",
                sessions = sessionList,
                onDeleteIconClick = {isDeleteDialogOpen=true}
            )
        }
    }
}


 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
private fun SessionScreenTopBar(onBackButtonClick:()-> Unit) {

     TopAppBar(
         navigationIcon = {
             IconButton(
                 onClick = onBackButtonClick
             ) {
                 Icon(imageVector = Icons.Default.ArrowBack,
                     contentDescription = "Navigate back to screen")

             }
         },
         title = { Text(text = "Study Session",
             style = MaterialTheme.typography.headlineSmall) }
     )


}

@Composable
private fun TimerSection(modifier: Modifier = Modifier) {


    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {  
        Box(modifier = Modifier
            .size(250.dp)
            .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape))
        
        Text(text = "00:50:32",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
        
    }
}

@Composable
private fun RelatedSubjectSession(modifier: Modifier ,
                                  relatedToSubject: String,
                                  selectSubjectButtonClick:()-> Unit) {
    Column(modifier = modifier) {
        Text(text = "Related To Subject",
            style = MaterialTheme.typography.bodySmall)

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Tamil",
                style = MaterialTheme.typography.bodyLarge)

            IconButton(onClick = selectSubjectButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
            }
        }
    }
}

@Composable
private fun ButtonSection(modifier: Modifier,
                          startButtonClick:()-> Unit,
                          cancelButtonClick:()-> Unit,
                          finishButtonClick:()-> Unit,) {
    Row(modifier=modifier,
        horizontalArrangement = Arrangement.SpaceBetween) {

        Button(onClick = cancelButtonClick) {
            Text(text = "Cancel",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }

        Button(onClick = startButtonClick) {
            Text(text = "Start",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }

        Button(onClick = finishButtonClick) {
            Text(text = "Finish",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        }
    }

}