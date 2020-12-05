package com.example.jobpractic

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


private const val JOB_ID = 0

class MainActivity : AppCompatActivity() {
    private var mScheduler: JobScheduler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.b1).setOnClickListener {
            scheduleJob()
        }
        findViewById<Button>(R.id.b2).setOnClickListener {
            cancelJob()
        }
    }

    fun scheduleJob(){
        val networkOptions = findViewById<RadioGroup>(R.id.radiouGroupId)
        val selectedNetworkID = networkOptions.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
        when (selectedNetworkID) {
            R.id.noNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            R.id.anyNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
        }
        mScheduler =  getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        val serviceName = ComponentName(
            packageName,
            NotificationJobService::class.java.name
        )
        val builder = JobInfo.Builder(JOB_ID, serviceName).apply {
            setRequiredNetworkType(selectedNetworkOption)
        }

        val myJobInfo = builder.build()
        mScheduler?.schedule(myJobInfo)

        Toast.makeText(this, "Job Scheduled, job will run when " +
                "the constraints are met.", Toast.LENGTH_SHORT).show()

    }

    fun cancelJob(){
        if (mScheduler!=null){
            mScheduler?.cancelAll()
            mScheduler = null
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}