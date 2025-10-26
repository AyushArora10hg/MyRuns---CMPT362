package ca.sfu.cmpt362.ayusharora.myruns.displayentry

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.Util
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabaseDao
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import kotlin.math.floor

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

        val inputTypeArray = resources.getStringArray(R.array.input_type)
        val activityTypeArray = resources.getStringArray(R.array.activity_type)
        val unitArray = resources.getStringArray(R.array.unit_values)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val pos = intent.getIntExtra("position", -1)

        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (pos == -1 || pos < workouts.size) {
                entry = workouts[pos]

                // Units for distance
                var unit = sharedPreferences.getString("unit_preference", unitArray[0])
                var displayDistance = 0.0
                if (unit == unitArray[0]) {
                    unit = "km"
                    displayDistance = entry.distance
                }
                else if (unit == unitArray[1]) {
                    unit = "mi"
                    displayDistance = Util.convertKilometersToMiles(displayDistance)
                }

                // Add Data to all edit texts
                val inputTypeEditText = findViewById<EditText>(R.id.de_edittext_input)
                inputTypeEditText.setText(inputTypeArray[entry.inputType])
                val activityTypeEditText = findViewById<EditText>(R.id.de_edittext_activity)
                activityTypeEditText.setText(activityTypeArray[entry.activityType])
                val dateAndTimeEditText = findViewById<EditText>(R.id.de_edittext_date_time)
                dateAndTimeEditText.setText(Util.formatDateTime(entry.dateTime))
                val durationEditText = findViewById<EditText>(R.id.de_edittext_duration)
                durationEditText.setText(Util.formatDuration(entry.duration))
                val distanceEditText = findViewById<EditText>(R.id.de_edittext_distance)
                distanceEditText.setText("$displayDistance $unit")
                val caloriesEditText = findViewById<EditText>(R.id.de_edittext_calories)
                caloriesEditText.setText("${Util.roundToTwoPlaces(entry.calorie)} kcal")
                val heartRateEditText = findViewById<EditText>(R.id.de_edittext_heart_rate)
                heartRateEditText.setText("${Util.roundToTwoPlaces(entry.heartRate)} bpm")
                val commentsEditText = findViewById<EditText>(R.id.de_edittext_comments)
                commentsEditText.setText(entry.comment)
            }
        }
    }

    private fun handleButtonClick() {

        val deleteButton : Button = findViewById(R.id.de_button_delete)
        deleteButton.setOnClickListener {
            workoutViewModel.deleteEntry(entry.id)
            finish()
        }
    }
}