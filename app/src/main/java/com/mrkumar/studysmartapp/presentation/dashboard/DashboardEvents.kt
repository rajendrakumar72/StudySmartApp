package com.mrkumar.studysmartapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.mrkumar.studysmartapp.domain.model.Session
import com.mrkumar.studysmartapp.domain.model.Task

sealed class DashboardEvents {

    data object SaveSubject: DashboardEvents()

    data object DeleteSession: DashboardEvents()

    data class OnDeleteSessionButtonClick(val session: Session): DashboardEvents()


    data class OnTaskIsCompleteChange(val task: Task): DashboardEvents()

    data class OnSubjectCardColorChange(val colors: List<Color>): DashboardEvents()

    data class OnSubjectNameChange(val name: String): DashboardEvents()

    data class OnGoalStudyHoursChange(val hours: String): DashboardEvents()



}