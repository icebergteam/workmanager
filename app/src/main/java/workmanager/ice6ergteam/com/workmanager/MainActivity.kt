package workmanager.ice6ergteam.com.workmanager

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.work.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val  mWorkManager: WorkManager by lazy {
        WorkManager.getInstance()
    }
    val PERIODIC_TASK_TAG :String = "PERIODIC_TASK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        }
        setContentView(R.layout.activity_main)
    }

    class LineNumberDebugTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            //remove the extra info and only get the class name.
            val className = super.createStackElementTag(element)?.split("$")?.get(0)
            return "($className.kt:${element.lineNumber})#${element.methodName}"
        }
    }

    override fun onStart() {
        super.onStart()
        mWorkManager.getStatusesByTag(PERIODIC_TASK_TAG).observe(this, Observer {
            Timber.e("Work status: %s", it)
        })
        findViewById<Button>(R.id.button).setOnClickListener {
            mWorkManager.cancelAllWorkByTag(PERIODIC_TASK_TAG)
            startWork()
        }
    }

    class PeriodicWork : Worker() {

        override fun doWork(): WorkerResult {
            Timber.e("Tick work")
            return WorkerResult.SUCCESS
        }
    }

    private fun startWork() {
        val build = PeriodicWorkRequest.Builder(PeriodicWork::class.java, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
               .addTag(PERIODIC_TASK_TAG)
               .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()
        mWorkManager.enqueue(build)
    }
}
