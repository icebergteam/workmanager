package workmanager.ice6ergteam.com.workmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var  mWorkManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWorkManager = WorkManager.getInstance()
    }

    override fun onStart() {
        super.onStart()
        findViewById<Button>(R.id.button).setOnClickListener {
            startWork()
        }
    }

    class PeriodicWork : Worker() {

        override fun doWork(): WorkerResult {
            Log.d("work", "check")
            return WorkerResult.SUCCESS
        }
    }

    private fun startWork() {
        val build = PeriodicWorkRequest.Builder(PeriodicWork::class.java, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()
        mWorkManager.enqueue(build)
    }
}
