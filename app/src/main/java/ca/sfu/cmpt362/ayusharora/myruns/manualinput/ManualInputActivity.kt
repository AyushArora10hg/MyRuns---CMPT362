package ca.sfu.cmpt362.ayusharora.myruns.manualinput

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.ayusharora.myruns.R

class ManualInputActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        setup()
        handleListItems()
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
    }
    // Code for date and time dialogs copied from XD's class demos/lectures.
    private fun handleListItems(){
        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0->{ showDateDialog() }

                1->{ showTimeDialog() }

                2->{ showInputDialog("durationDialog",
                    "Duration (in minutes)",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter workout duration") }

                3->{ showInputDialog("distanceDialog",
                    "Distance (mi or km)",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter distance covered") }

                4->{ showInputDialog("calorieDialog",
                    "Calories (in kcal)",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter calories burnt") }

                5->{ showInputDialog("heartRateDialog",
                    "Heart Rate (average bpm)",
                    InputType.TYPE_CLASS_NUMBER,
                    "Enter average bpm") }

                6->{ showInputDialog("commentsDialog",
                    "Comments",
                    InputType.TYPE_CLASS_TEXT,
                    "How did your workout go?") }
            }
        }
    }
    private fun handleButtonClicks(){

        val saveButton = findViewById<Button>(R.id.mi_button_save)
        saveButton.setOnClickListener {
            //TODO: Save to database
            finish()
        }

        val cancelButton = findViewById<Button>(R.id.mi_button_cancel)
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDateDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.Companion.TITLE_KEY, InputDialogFragment.Companion.TYPE_DATE)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "datePickerDialog")
    }
    private fun showTimeDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.Companion.TITLE_KEY, InputDialogFragment.Companion.TYPE_TIME)
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