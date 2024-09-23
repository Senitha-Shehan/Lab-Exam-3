package com.example.dailydo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Context
import android.content.Intent
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import android.util.Log
import android.widget.ImageView
import java.text.SimpleDateFormat
import androidx.cardview.widget.CardView
import com.example.dailydo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale


class CalendarActivity: AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tasksContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        calendarView = findViewById(R.id.calendarView)
        tasksContainer = findViewById(R.id.tasksContainer)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            displayTasksForDate(selectedDate)
        }

        val BackHome = findViewById<FloatingActionButton>(R.id.floatingActionButton3
        )
        BackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun displayTasksForDate(selectedDate: String) {
        // Clear previous task views
        Log.d("DisplayTasks", "Clearing previous views for date: $selectedDate")
        tasksContainer.removeAllViews()

        val tasksForDate = loadTasksForDate(selectedDate)
        Log.d("DisplayTasks", "Tasks loaded for date $selectedDate: ${tasksForDate.size} task(s) found.")

        if (tasksForDate.isEmpty()) {
            val noTasksTextView = TextView(this).apply {
                text = "No tasks for this date."
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            tasksContainer.addView(noTasksTextView)
        } else {
            for (task in tasksForDate) {
                // Create a CardView
                val cardView = CardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 16, 0, 16) // Set top and bottom margins (16 pixels)
                    }
                    radius = 12f
                    elevation = 8f
                    setCardBackgroundColor(getColor(R.color.cardBackground)) // Change to your desired color
                    setPadding(16, 16, 16, 16) // Padding inside the card
                }

                // Create a LinearLayout to hold the image and text
                val layout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 0, 0, 8) // Padding below the layout
                }

                // Create an ImageView for the vector image
//                val imageView = ImageView(this).apply {
////                    setImageResource(R.drawable.baseline_task_alt_24) // Replace with your vector drawable
//                    layoutParams = LinearLayout.LayoutParams(200, 200) // Size of the image
//                    setPadding(30, 20, 16, 0) // Padding to the right of the image
//                }

                // Create a LinearLayout to hold title and date
                val textContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                }

                // Create a TextView for task title
                val titleTextView = TextView(this).apply {
                    text = task.name
                    textSize = 18f
                    setTextColor(getColor(R.color.titleColor)) // Set a highlight color for the title
                    setPadding(30, 40, 20, 4) // Add some padding below the title
                }

                // Create a TextView for task date
                val dateTextView = TextView(this).apply {
                    text = task.time
                    textSize = 14f
                    setTextColor(getColor(R.color.dateColor)) // Set a different color for the date
                    setPadding(35, 30, 25, 8) // Add padding below the date
                }

                // Add title and date to textContainer
                textContainer.addView(titleTextView)
                textContainer.addView(dateTextView)

                // Add the ImageView and textContainer to the layout
//                layout.addView(imageView)
                layout.addView(textContainer)

                // Add the layout to the CardView
                cardView.addView(layout)

                // Add CardView to tasksContainer
                tasksContainer.addView(cardView)
            }
        }
    }




    private fun loadTasks(): List<Task> {
        val sharedPreferences = getSharedPreferences("MyTodoPrefs", Context.MODE_PRIVATE)
        // Retrieve the JSON string from SharedPreferences
        val taskJson = sharedPreferences.getString("task_list", "[]") ?: "[]"
        Log.d("taskJson1", "taskJson: $taskJson")

        // Use Gson to parse the JSON string into a List<Task>
        val gson = com.google.gson.Gson()
        val taskType = object : com.google.gson.reflect.TypeToken<List<Task>>() {}.type
        val taskList: List<Task> = gson.fromJson(taskJson, taskType)

        Log.d("taskList", "taskList: $taskList")
        return taskList
    }

    private fun loadTasksForDate(selectedDate: String): List<Task> {
        // Convert selectedDate from "yyyy-MM-dd" to "dd/M/yyyy"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())

        val date = inputFormat.parse(selectedDate)
        val formattedDate = if (date != null) outputFormat.format(date) else ""

        // Load all tasks
        val tasks = loadTasks()
        Log.d("tasks232323", "tasks: $tasks")

        // Filter tasks by the formatted date
        val filteredTasks = tasks.filter { it.date == formattedDate }
        Log.d("filteredTasks", "filteredTasks: $filteredTasks")

        return filteredTasks // Return filtered tasks
    }
}