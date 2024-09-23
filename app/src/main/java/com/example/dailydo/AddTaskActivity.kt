package com.example.dailydo
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.io.Serializable

class AddTaskActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextTaskName: EditText
    private lateinit var submitButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task)

        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextTaskName = findViewById(R.id.editTextTaskName)
        submitButton = findViewById(R.id.submitButton)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyTodoPrefs", MODE_PRIVATE)

        // Set click listeners for date and time fields
        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }

        editTextTime.setOnClickListener {
            showTimePickerDialog()
        }

        val BackHome = findViewById<FloatingActionButton>(R.id.floatingActionButton2
        )
        BackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for submit button
        submitButton.setOnClickListener {
            saveTask()
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

    private fun saveTask() {
        val taskName = editTextTaskName.text.toString()
        val taskDate = editTextDate.text.toString()
        val taskTime = editTextTime.text.toString()

        if (taskName.isNotEmpty() && taskDate.isNotEmpty() && taskTime.isNotEmpty()) {
            val task = Task(taskName, taskDate, taskTime)

            // Retrieve the current list of tasks from SharedPreferences
            val taskList = getTaskList()
            taskList.add(task)

            // Save the updated task list back to SharedPreferences
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(taskList)
            editor.putString("task_list", json)
            editor.apply()

            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
            finish()
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

data class Task(val name: String, val date: String, val time: String)  : Serializable
