package ca.sfu.cmpt362.ayusharora.myruns.displayentry

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.WorkoutFormatter
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabaseDao
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel

class DisplayEntryActivity () : AppCompatActivity() {

    // The workout to be displayed by this activity
    private lateinit var entry: ExerciseEntry

    // Database objects
    private lateinit var db : WorkoutDatabase
    private lateinit var dao : WorkoutDatabaseDao
    private lateinit var repository: WorkoutRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var workoutViewModel: WorkoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_display_entry)

        loadDataBase()
        setupActivityDisplay()
        handleButtonClick()
    }

    // This helper method loads the database following proper MVVM structure
    // Code adapted from XD's lecture demos
    private fun loadDataBase(){
        db = WorkoutDatabase.getInstance(this)
        dao = db.workoutDatabaseDao
        repository = WorkoutRepository(dao)
        viewModelFactory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider (this, viewModelFactory)[WorkoutViewModel::class.java]
    }

    // This helper method gets the activity tapped by user in history fragment via intent
    // It grabs all the attributes of that activity from database and displays them
    private fun setupActivityDisplay(){

        val pos = intent.getIntExtra("position", -1)

        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (pos == -1 || pos < workouts.size) {
                entry = workouts[pos]

                WorkoutFormatter.initialize(this, entry)

                // Add Data to all edit texts
                val inputTypeEditText = findViewById<EditText>(R.id.de_edittext_input)
                inputTypeEditText.setText(WorkoutFormatter.inputType)
                val activityTypeEditText = findViewById<EditText>(R.id.de_edittext_activity)
                activityTypeEditText.setText(WorkoutFormatter.activityType)
                val dateAndTimeEditText = findViewById<EditText>(R.id.de_edittext_date_time)
                dateAndTimeEditText.setText(WorkoutFormatter.dateTime)
                val durationEditText = findViewById<EditText>(R.id.de_edittext_duration)
                durationEditText.setText(WorkoutFormatter.duration)
                val distanceEditText = findViewById<EditText>(R.id.de_edittext_distance)
                distanceEditText.setText(WorkoutFormatter.distance)
                val caloriesEditText = findViewById<EditText>(R.id.de_edittext_calories)
                caloriesEditText.setText(WorkoutFormatter.calories)
                val heartRateEditText = findViewById<EditText>(R.id.de_edittext_heart_rate)
                heartRateEditText.setText(WorkoutFormatter.heartRate)
                val commentsEditText = findViewById<EditText>(R.id.de_edittext_comments)
                commentsEditText.setText(WorkoutFormatter.comment)
            }
        }
    }

    private fun handleButtonClick() {

        val deleteButton : Button = findViewById(R.id.de_button_delete)
        deleteButton.setOnClickListener {
            workoutViewModel.deleteEntry(entry.id)
            workoutViewModel.allWorkouts.removeObservers(this)
            finish()
        }
    }
}