package com.mrkumar.studysmartapp.presentation.subject

import android.content.Context
import android.content.Intent
import com.mrkumar.studysmartapp.presentation.session.StudySessionTimerService

object ServiceHelper{

    fun triggerForegroundService(context: Context,action: String){
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action=action
            context.startService(this)
        }
    }
}