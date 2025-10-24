package ca.sfu.cmpt362.ayusharora.myruns.displayentry

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabaseDao
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel

class DisplayEntryActivity () : AppCompatActivity() {


    private lateinit var inputTypeEditText: EditText
    private lateinit var activityTypeEditText: EditText
    private lateinit var dateAndTimeEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var distanceEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var heartRateEditText: EditText
    private lateinit var deleteButton : Button

    private lateinit var db : WorkoutDatabase
    private lateinit var dao : WorkoutDatabaseDao
    private lateinit var repository: WorkoutRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var workoutViewModel: WorkoutViewModel

    private lateinit var entry: ExerciseEntry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_display_entry)

        val inputTypeArray = resources.getStringArray(R.array.input_type)
        val activityTypeArray = resources.getStringArray(R.array.activity_type)

        db = WorkoutDatabase.getInstance(this)
        dao = db.workoutDatabaseDao
        repository = WorkoutRepository(dao)
        viewModelFactory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider (this, viewModelFactory)[WorkoutViewModel::class.java]


        inputTypeEditText = findViewById(R.id.de_edittext_input)
        activityTypeEditText = findViewById(R.id.de_edittext_activity)
        dateAndTimeEditText = findViewById(R.id.de_edittext_date_time)
        durationEditText = findViewById(R.id.de_edittext_duration)
        distanceEditText = findViewById(R.id.de_edittext_distance)
        caloriesEditText = findViewById(R.id.de_edittext_calories)
        heartRateEditText = findViewById(R.id.de_edittext_heart_rate)

        val pos = intent.getIntExtra("position", -1)
        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (pos < workouts.size) {
                entry = workouts[pos]

                inputTypeEditText.setText(inputTypeArray[entry.inputType])
                activityTypeEditText.setText(activityTypeArray[entry.activityType])
                dateAndTimeEditText.setText("9:12:32 Oct 23 2025")
                durationEditText.setText(entry.duration.toString())
                distanceEditText.setText(entry.distance.toString())
                caloriesEditText.setText(entry.calorie.toString())
                heartRateEditText.setText(entry.heartRate.toString())
            }
        }

        deleteButton = findViewById(R.id.de_button_delete)
        deleteButton.setOnClickListener {
            workoutViewModel.deleteEntry(entry.id)
            finish()
        }


    }
}