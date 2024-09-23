package com.example.dailydo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydo.HomeActivity
import com.example.dailydo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class EditTask : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextTaskName: EditText
    private lateinit var updateButton: Button
    private lateinit var sharedPreferences: SharedPreferences


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_task)

        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextTaskName = findViewById(R.id.editTextTaskName)
        updateButton = findViewById(R.id.updateButton)

        val BackHome = findViewById<FloatingActionButton>(R.id.floatingActionButton2edit
        )
        BackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyTodoPrefs", MODE_PRIVATE)

        // Get task details passed from the previous activity
        val task = intent.getSerializableExtra("task_list") as? Task
       var taskPosition = intent.getIntExtra("task_position", -1)
       Log.d("taskPosition","taskPosition $taskPosition")
        // Populate fields with existing task data
        editTextTaskName.setText(task?.name)
        editTextDate.setText(task?.date)
        editTextTime.setText(task?.time)

        // Set click listeners for date and time fields
        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        editTextTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Set click listener for update button
        updateButton.setOnClickListener {
            updateTask(taskPosition)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            editTextDate.setText(date)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            editTextTime.setText(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun updateTask(taskPosition: Int) {
        val updatedTaskName = editTextTaskName.text.toString()
        val updatedTaskDate = editTextDate.text.toString()
        val updatedTaskTime = editTextTime.text.toString()

        if (updatedTaskName.isNotEmpty() && updatedTaskDate.isNotEmpty() && updatedTaskTime.isNotEmpty()) {
            val updatedTask = Task(updatedTaskName, updatedTaskDate, updatedTaskTime)
            Log.d("updatedTask","updatedTask:$updatedTask")
            // Retrieve the current list of tasks from SharedPreferences
            val taskList = getTaskList()
            Log.d("taskList","taskList:$taskList")
            taskList[taskPosition] = updatedTask
            Log.d("updatedTaskList","updatedTaskList:$taskList")

            // Save the updated task list back to SharedPreferences
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(taskList)
            editor.putString("task_list", json)
            editor.apply()

            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
            finish()  // Return to the previous activity
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTaskList(): MutableList<Task> {
        val gson = Gson()
        val json = sharedPreferences.getString("task_list", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        Log.d("TaskList", "Retrieved JSON: $json")
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
