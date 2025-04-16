package com.mrkumar.studysmartapp.domain.model

import androidx.compose.ui.graphics.Color
import com.mrkumar.studysmartapp.presentation.theme.gradient1
import com.mrkumar.studysmartapp.presentation.theme.gradient2
import com.mrkumar.studysmartapp.presentation.theme.gradient3
import com.mrkumar.studysmartapp.presentation.theme.gradient4
import com.mrkumar.studysmartapp.presentation.theme.gradient5

data class Subject(val name: String,
    val goalHours: Float,
    val colors: List<Color>,
    val subjectId: Int){

    companion object{
        val subjectCardColors=listOf(gradient1, gradient2,gradient3, gradient4, gradient5)
    }
}