package ca.sfu.cmpt362.ayusharora.myruns.manualinput

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.Util
import ca.sfu.cmpt362.ayusharora.myruns.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import java.util.Calendar

class ManualInputActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var unitSharedPreference: SharedPreferences
    private lateinit var dialogSharedPreferences: SharedPreferences
    private var shouldShowToast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        setupListView()
        loadAndObserveDatabase()
        handleDialogInputs()
        handleButtonClicks()
    }

    // This helper method sets up the activity's list view and adds a array adapter to it
    // It also adds a listener to the list view
    // Based on the position of item clicked by the user, it opens the associated dialog
    private fun setupListView(){

        listView = findViewById(R.id.mi_listview)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, resources.getStringArray(R.array.manual_entry_fields))
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            when(position) {
                // Date
                0 -> {
                    createDateDialog()
                }
                // Time
                1 -> {
                    createTimeDialog()
                }
                // Duration
                2 -> {
                    createInputDialog(
                        "durationDialog",
                        "Duration",
                        "(in min)",
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                        "Enter workout duration",
                        dialogSharedPreferences.getString("duration", "")
                    )
                }
                // Distance
                3 -> {
                    val unitArray = resources.getStringArray(R.array.unit_values)
                    var unit = unitSharedPreference.getString("unit_preference", unitArray[0])
                    if (unit == unitArray[0]) {
                        unit = "kilometers"
                    } else if (unit == unitArray[1]) {
                        unit = "miles"
                    }
                    createInputDialog(
                        "distanceDialog",
                        "Distance",
                        "(in $unit)",
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                        "Enter distance covered",
                        dialogSharedPreferences.getString("distance", "")
                    )
                }
                // Calories
                4 -> {
                    createInputDialog(
                        "calorieDialog",
                        "Calories", "(in kcal)",
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                        "Enter calories burnt",
                        dialogSharedPreferences.getString("calories", "")
                    )
                }
                // Heart Rate
                5 -> {
                    createInputDialog(
                        "heartRateDialog",
                        "Heart Rate",
                        "(in bpm)",
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                        "Enter average bpm",
                        dialogSharedPreferences.getString("heartRate", "")
                    )
                }
                // Comments
                6 -> {
                    createInputDialog(
                        "commentsDialog",
                        "Comments",
                        "",
                        InputType.TYPE_CLASS_TEXT,
                        "How did your workout go?",
                        dialogSharedPreferences.getString("comments", "")
                    )
                }
            }
        }
    }

    // This helper method provides database access to the class
    // This class requires database to insert a new entry and to show the id of the newly inserted
    // entry through a toast
    private fun loadAndObserveDatabase(){

        unitSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        dialogSharedPreferences = getSharedPreferences("dialogData", MODE_PRIVATE)

        val db = WorkoutDatabase.getInstance(this)
        val dao = db.workoutDatabaseDao
        val repository = WorkoutRepository(dao)
        val factory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]
        workoutViewModel.allWorkouts.observe(this){ workouts->
            if (shouldShowToast && workouts.isNotEmpty()){
                Toast.makeText(this, "Entry #${workouts.last().id} saved!", Toast.LENGTH_SHORT).show()
                shouldShowToast = false
            }
        }
    }

    // This method retrieves the input provided by the user in dialogs
    // The InputDialogFragment class returns the user input through bundles and this class
    // accesses them and stores them in the database and a shared preference
    // The shared preference holds the value till the time user hasn't provided input to all
    // the dialogs and hit the Save button
    private fun handleDialogInputs(){

        // Date
        supportFragmentManager.setFragmentResultListener("selected_date", this){_, bundle ->
            val year = bundle.getInt("selected_year")
            val month = bundle.getInt("selected_month")
            val day = bundle.getInt("selected_day")
            workoutViewModel.entry.dateTime.set(year, month, day)
        }
        // Time
        supportFragmentManager.setFragmentResultListener("selected_time", this){_, bundle ->
            val hour = bundle.getInt("selected_hour")
            val min = bundle.getInt("selected_minute")
            workoutViewModel.entry.dateTime.set(Calendar.HOUR_OF_DAY, hour)
            workoutViewModel.entry.dateTime.set(Calendar.MINUTE, min)
            workoutViewModel.entry.dateTime.set(Calendar.SECOND, 0)
        }
        // Duration
        supportFragmentManager.setFragmentResultListener("input_duration", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.duration = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("duration", value)
            }
        }
        // Distance
        supportFragmentManager.setFragmentResultListener("input_distance", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.distance = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("distance", value)
            }
        }
        // Calories
        supportFragmentManager.setFragmentResultListener("input_calories", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.calorie = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("calories", value)
            }
        }
        // Heart Rate
        supportFragmentManager.setFragmentResultListener("input_heart rate", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.heartRate = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("heartRate", value)
            }
        }
        // Comments
        supportFragmentManager.setFragmentResultListener("input_comments", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.comment = value?: ""
            dialogSharedPreferences.edit{
                putString("comments", value)
            }
        }
    }

    // This helper method overrides click listeners of the two buttons
    // Save button: Save the entry to the database, show a toast, kill the activity
    // Cancel button: finish the activity
    private fun handleButtonClicks(){

        val saveButton = findViewById<Button>(R.id.mi_button_save)
        saveButton.setOnClickListener {
            workoutViewModel.entry.activityType = intent.getIntExtra("ACTIVITY_TYPE", -1)
            val unitArray = resources.getStringArray(R.array.unit_values)
            val unit = unitSharedPreference.getString("unit_preference", unitArray[0])
            if (unit == unitArray[1]){
                // User has selected imperial system, so convert miles to kms
                workoutViewModel.entry.distance = Util.convertMilesToKilometers(workoutViewModel.entry.distance)
            }
            workoutViewModel.insert()
            dialogSharedPreferences.edit {
                clear()
                apply()
            }
            shouldShowToast = true
            finish()
        }

        val cancelButton = findViewById<Button>(R.id.mi_button_cancel)
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
            dialogSharedPreferences.edit {
                clear()
                apply()
            }
            finish()
        }
    }

    // Helper method to call a datePickerDialog
    private fun createDateDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.DIALOG_TYPE_KEY, InputDialogFragment.TYPE_DATE)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "datePickerDialog")
    }

    // Helper method to call oa timePicketDialog
    private fun createTimeDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.DIALOG_TYPE_KEY, InputDialogFragment.TYPE_TIME)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "timePickerDialog")
    }

    // Helper method to call a inputTypeDialog
    private fun createInputDialog(tag: String, title: String, unit: String?, inputType: Int, hint: String, text: String?){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.DIALOG_TYPE_KEY, InputDialogFragment.TYPE_INPUT)
        args.putString(InputDialogFragment.TITLE_KEY, title)
        args.putString(InputDialogFragment.UNIT_KEY, unit)
        args.putInt(InputDialogFragment.INPUT_TYPE_KEY, inputType)
        args.putString(InputDialogFragment.HINT_KEY,hint)
        args.putString(InputDialogFragment.DEFAULT_TEXT_KEY, text)
        dialog.arguments = args
        dialog.show(supportFragmentManager, tag)
    }

}