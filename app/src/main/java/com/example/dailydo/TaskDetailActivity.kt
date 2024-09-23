package com.example.dailydo
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.TextView
import com.example.dailydo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var tvTaskName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStartTimer: Button
    private lateinit var etTimerInput: EditText // Add this line

    private var timer: CountDownTimer? = null
    private var timeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        tvTaskName = findViewById(R.id.tvTaskName)
        tvDate = findViewById(R.id.tvDate)
        tvTime = findViewById(R.id.tvTime)
        tvTimer = findViewById(R.id.tvTimer)
        btnStartTimer = findViewById(R.id.btnStartTimer)
        etTimerInput = findViewById(R.id.etTimerInput) // Initialize the EditText

        // Get the task details passed from the previous activity
        val task = intent.getSerializableExtra("task") as Task

        val BackHome = findViewById<FloatingActionButton>(R.id.floatingActionButton
        )
        BackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Display task details
        tvTaskName.text = task.name
        tvDate.text = task.date
        tvTime.text = task.time

        btnStartTimer.setOnClickListener {
            val minutes = etTimerInput.text.toString().toLongOrNull()
            if (minutes != null) {
                startTimer(minutes * 60 * 1000) // Convert minutes to milliseconds
            } else {
                tvTimer.text = "Please enter a valid time"
            }
        }
    }

    private fun startTimer(duration: Long) {
        timeInMillis = duration

        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
//                tvTimer.text = "Time's up!"
                playAlarm()
            }
        }.start()
    }

    private fun updateTimerText() {
        val seconds = (timeInMillis / 1000).toInt() % 60
        val minutes = (timeInMillis / 1000 / 60).toInt()
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun playAlarm() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.alarm) // Ensure you have the sound file in res/raw
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // Cancel the timer if the activity is destroyed
    }
}