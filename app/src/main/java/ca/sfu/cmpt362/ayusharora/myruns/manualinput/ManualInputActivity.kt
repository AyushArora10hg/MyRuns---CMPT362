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
import ca.sfu.cmpt362.ayusharora.myruns.database.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel

class ManualInputActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var unitSharedPreference: SharedPreferences
    private lateinit var unitArray: Array <String>
    private lateinit var dialogSharedPreferences: SharedPreferences
    private var shouldShowToast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        setup()
        handleListItems()
        handleDialogInputs()
        handleButtonClicks()

        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (shouldShowToast && workouts.isNotEmpty()) {
                val lastEntry = workouts.last()
                Toast.makeText(this, "Entry #${lastEntry.id} saved!", Toast.LENGTH_SHORT).show()
                shouldShowToast = false
            }
        }
    }
    private fun setup(){
        listView = findViewById(R.id.mi_listview)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.manual_entry_fields)
        )
        listView.adapter = arrayAdapter

        val db = WorkoutDatabase.getInstance(this)
        val dao = db.workoutDatabaseDao
        val repository = WorkoutRepository(dao)
        val factory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]
        unitSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        dialogSharedPreferences = getSharedPreferences("dialogData", MODE_PRIVATE)
        unitArray= resources.getStringArray(R.array.unit_values)
    }
    // Code for date and time dialogs copied from XD's class demos/lectures.
    private fun handleListItems(){
        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0->{ showDateDialog() }

                1->{ showTimeDialog() }

                2->{ showInputDialog("durationDialog",
                    "Duration",
                    "(in min)",
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                    "Enter workout duration",
                    dialogSharedPreferences.getString("duration", ""))
                }

                3->{
                    var unit = unitSharedPreference.getString("unit_preference",unitArray[0])
                    if (unit == unitArray[0]){
                        unit = "kilometers"
                    } else if (unit == unitArray[1]){
                        unit = "miles"
                    }
                    showInputDialog("distanceDialog",
                    "Distance", "(in $unit)",
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                    "Enter distance covered",
                        dialogSharedPreferences.getString("distance", "")) }

                4->{ showInputDialog("calorieDialog",
                    "Calories",
                    "(in kcal)",
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                    "Enter calories burnt",
                    dialogSharedPreferences.getString("calories", "")) }

                5->{ showInputDialog("heartRateDialog",
                    "Heart Rate",
                    "(in bpm)",
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
                    "Enter average bpm",
                    dialogSharedPreferences.getString("heartRate", "")) }

                6->{ showInputDialog("commentsDialog",
                    "Comments",
                    "",
                    InputType.TYPE_CLASS_TEXT,
                    "How did your workout go?",
                    dialogSharedPreferences.getString("comments", "")) }
            }
        }
    }

    private fun handleDialogInputs(){

        //Date
        //Time
        //Duration
        supportFragmentManager.setFragmentResultListener("input_duration", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.duration = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("duration", value)
            }
        }
        //Distance
        supportFragmentManager.setFragmentResultListener("input_distance", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.distance = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("distance", value)
            }
        }
        //Calories
        supportFragmentManager.setFragmentResultListener("input_calories", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.calorie = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("calories", value)
            }
        }
        //Heart Rate
        supportFragmentManager.setFragmentResultListener("input_heart rate", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.heartRate = value?.toDoubleOrNull()?:0.0
            dialogSharedPreferences.edit{
                putString("heartRate", value)
            }
        }
        //Comments
        supportFragmentManager.setFragmentResultListener("input_comments", this){_, bundle->
            val value = bundle.getString("user_input")
            workoutViewModel.entry.comment = value?: ""
            dialogSharedPreferences.edit{
                putString("comments", value)
            }
        }
    }

    private fun handleButtonClicks(){

        val saveButton = findViewById<Button>(R.id.mi_button_save)
        saveButton.setOnClickListener {
            workoutViewModel.entry.activityType = intent.getIntExtra("ACTIVITY_TYPE", -1)
            val unit = unitSharedPreference.getString("unit_preference", unitArray[0])
            if (unit == unitArray[1]){
                workoutViewModel.entry.distance = "%.2f".format(workoutViewModel.entry.distance * 1.6094).toDouble()
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

    private fun showDateDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.DIALOG_TYPE_KEY, InputDialogFragment.TYPE_DATE)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "datePickerDialog")
    }

    private fun showTimeDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.DIALOG_TYPE_KEY, InputDialogFragment.TYPE_TIME)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "timePickerDialog")
    }

    private fun showInputDialog(tag: String, title: String, unit: String?, inputType: Int, hint: String, text: String?){
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