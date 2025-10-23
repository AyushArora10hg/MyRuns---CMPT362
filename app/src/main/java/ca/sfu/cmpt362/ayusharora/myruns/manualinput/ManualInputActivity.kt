package ca.sfu.cmpt362.ayusharora.myruns.manualinput

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManualInputActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var manualInputViewModel: ManualInputViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        setup()
        handleListItems()
        handleDialogInputs()
        handleButtonClicks()
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
        manualInputViewModel = ViewModelProvider(this, factory)[ManualInputViewModel::class.java]
    }
    // Code for date and time dialogs copied from XD's class demos/lectures.
    private fun handleListItems(){
        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0->{ showDateDialog() }

                1->{ showTimeDialog() }

                2->{ showInputDialog("durationDialog",
                    "Duration",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter workout duration") }

                3->{ showInputDialog("distanceDialog",
                    "Distance",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter distance covered") }

                4->{ showInputDialog("calorieDialog",
                    "Calories",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter calories burnt") }

                5->{ showInputDialog("heartRateDialog",
                    "Heart Rate",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter average bpm") }

                6->{ showInputDialog("commentsDialog",
                    "Comments",
                    InputType.TYPE_CLASS_TEXT,
                    "How did your workout go?") }
            }
        }
    }

    private fun handleDialogInputs(){

        //Date
        //Time
        //Duration
        supportFragmentManager.setFragmentResultListener("input_duration", this){_, bundle->
            val value = bundle.getString("user_input")
            manualInputViewModel.entry.duration = value?.toDouble()?:0.0
        }
        //Distance
        supportFragmentManager.setFragmentResultListener("input_distance", this){_, bundle->
            val value = bundle.getString("user_input")
            manualInputViewModel.entry.distance = value?.toDouble()?:0.0
        }
        //Calories
        supportFragmentManager.setFragmentResultListener("input_calories", this){_, bundle->
            val value = bundle.getString("user_input")
            manualInputViewModel.entry.calorie = value?.toDouble()?:0.0
        }
        //Heart Rate
        supportFragmentManager.setFragmentResultListener("input_heart rate", this){_, bundle->
            val value = bundle.getString("user_input")
            manualInputViewModel.entry.heartRate = value?.toDouble()?:0.0
        }
        //Comments
        supportFragmentManager.setFragmentResultListener("input_comments", this){_, bundle->
            val value = bundle.getString("user_input")
            manualInputViewModel.entry.comment = value?: ""
        }
    }

    private fun handleButtonClicks(){

        val saveButton = findViewById<Button>(R.id.mi_button_save)
        saveButton.setOnClickListener {
            manualInputViewModel.insert()
            finish()
        }

        val cancelButton = findViewById<Button>(R.id.mi_button_cancel)
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
            manualInputViewModel.entry.apply {
                id = 0
                inputType = 0
                activityType = 0
                duration = 0.0
                distance = 0.0
                avgPace = 0.0
                avgSpeed = 0.0
                calorie = 0.0
                climb = 0.0
                heartRate = 0.0
                comment = ""
            }
            finish()
        }
    }

    private fun showDateDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.Companion.DIALOG_TYPE_KEY, InputDialogFragment.Companion.TYPE_DATE)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "datePickerDialog")
    }
    private fun showTimeDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.Companion.DIALOG_TYPE_KEY, InputDialogFragment.Companion.TYPE_TIME)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "timePickerDialog")
    }
    private fun showInputDialog(tag: String, title: String, inputType: Int, hint: String){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.Companion.DIALOG_TYPE_KEY, InputDialogFragment.Companion.TYPE_INPUT)
        args.putString(InputDialogFragment.Companion.TITLE_KEY, title)
        args.putInt(InputDialogFragment.Companion.INPUT_TYPE_KEY, inputType)
        args.putString(InputDialogFragment.Companion.HINT_KEY,hint)
        dialog.arguments = args
        dialog.show(supportFragmentManager, tag)
    }

}