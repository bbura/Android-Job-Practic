package com.example.jobpractic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_NONE
import android.app.job.JobInfo.NETWORK_TYPE_UNMETERED
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat


lateinit var mNotifyManager: NotificationManager


private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"


class NotificationJobService : JobService() {

    private fun createNotificationChannel() {

        // Define notification manager object.
        mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            // Create the NotificationChannel with all the parameters.
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Job Service notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications from Job Service"
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }


    override fun onStartJob(jobParameters: JobParameters?): Boolean {

        //Create the notification channel
        createNotificationChannel()

        //Set up the notification content intent to launch the app when clicked
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                MainActivity::class.java
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("Job Service")
            .setContentText("Your Job ran to completion!")
            .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.ic_job_running)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
        mNotifyManager.notify(0, builder.build())
        (getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
        scheduleJob()
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        scheduleJob()
        (getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
       return true
    }

    fun scheduleJob(){

        val serviceName = ComponentName(
            packageName,
            NotificationJobService::class.java.name
        )
        val builder = JobInfo.Builder(0, serviceName).apply {
            setRequiredNetworkType(NETWORK_TYPE_NONE)
        }

        val myJobInfo = builder.build()
        (getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(myJobInfo)

        Toast.makeText(this, "Job Scheduled, job will run when " +
                "the constraints are met.", Toast.LENGTH_SHORT).show()

    }
}