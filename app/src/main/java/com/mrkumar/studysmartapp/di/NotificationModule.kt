package com.mrkumar.studysmartapp.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.mrkumar.studysmartapp.R
import com.mrkumar.studysmartapp.presentation.session.ServiceHelper
import com.mrkumar.studysmartapp.util.Constants.NOTIFICATION_CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.android.components.ServiceComponent


@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {


    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder{
        return NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Study Session")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(ServiceHelper.clickPendingIntent(context))
    }
    @ServiceScoped
    @Provides
    fun provideNotificationManger(@ApplicationContext context: Context): NotificationManager{
        return context.getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager
    }
}