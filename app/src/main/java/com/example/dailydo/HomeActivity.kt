package com.example.dailydo

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var deleteIcon: Drawable
    private val swipeBackground = ColorDrawable(Color.RED)



    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_list) // Ensure this is the correct layout file



        sharedPreferences = getSharedPreferences("MyTodoPrefs", MODE_PRIVATE)

        // Initialize the delete icon drawable
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.baseline_delete_sweep_24)!!

        // Load and display tasks using RecyclerView
        loadTasks()

        // Set up BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }

                else -> false
            }
        }

        // Floating Action Button to Add Task
        val fabAddTask = findViewById<ImageView>(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }



        // Implement swipe-to-delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Delete the swiped item from the list and SharedPreferences
                val position = viewHolder.adapterPosition
                deleteTask(position)
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dx: Float,
                dy: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                // Draw background (red)
                swipeBackground.setBounds(
                    itemView.right + dx.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                swipeBackground.draw(canvas)

                // Draw delete icon
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight
                val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteIcon.draw(canvas)

                super.onChildDraw(canvas, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive)
            }
        }

        // Attach the ItemTouchHelper to the RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    private fun loadTasks() {
        val taskList = getTaskList()
        Log.d("taskList", "taskList: $taskList")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(taskList) { task ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task", task)
            startActivity(intent)
        }
        recyclerView.adapter = taskAdapter

        // Add spacing (e.g., 16dp)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing) // Define in dimens.xml
        recyclerView.addItemDecoration(SpaceItemDecoration(spacingInPixels))
    }


    private fun getTaskList(): MutableList<Task> {
        val gson = Gson()
        val json = sharedPreferences.getString("task_list", null)
        Log.d("json", "json:$json")
        val type = object : TypeToken<MutableList<Task>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    private fun deleteTask(position: Int) {
        // Fetch the current task list from SharedPreferences
        val taskList = getTaskList().toMutableList()

        // Remove the task at the specified position
        if (position in taskList.indices) {
            taskList.removeAt(position)

            // Save the updated task list to SharedPreferences
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(taskList)
            editor.putString("task_list", json)
            editor.apply()

            // Update the RecyclerView with the new task list
            taskAdapter.updateTasks(taskList)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Portrait mode", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Landscape mode", Toast.LENGTH_SHORT).show()
        }
    }

        override fun onResume() {
        super.onResume()
        // Reload the task list to ensure the updated list is shown
        loadTasks()
    }
}
