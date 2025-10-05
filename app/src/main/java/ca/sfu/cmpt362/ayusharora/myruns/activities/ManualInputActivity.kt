package ca.sfu.cmpt362.ayusharora.myruns.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.fragments.MyRunsDialogFragment
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
                    android.text.InputType.TYPE_CLASS_NUMBER,
                    "Enter workout duration") }

                3->{ showInputDialog("distanceDialog",
                    "Distance (mi or km)",
                    android.text.InputType.TYPE_CLASS_NUMBER,
                    "Enter distance covered") }

                4->{ showInputDialog("calorieDialog",
                    "Calories (in kcal)",
                    android.text.InputType.TYPE_CLASS_NUMBER,
                    "Enter calories burnt") }

                5->{ showInputDialog("heartRateDialog",
                    "Heart Rate (average bpm)",
                    android.text.InputType.TYPE_CLASS_NUMBER,
                    "Enter average bpm") }

                6->{ showInputDialog("commentsDialog",
                    "Comments",
                    android.text.InputType.TYPE_CLASS_TEXT,
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
        val dialog = MyRunsDialogFragment()
        val args = Bundle()
        args.putInt(MyRunsDialogFragment.TITLE_KEY, MyRunsDialogFragment.TYPE_DATE)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "datePickerDialog")
    }
    private fun showTimeDialog(){
        val dialog = MyRunsDialogFragment()
        val args = Bundle()
        args.putInt(MyRunsDialogFragment.TITLE_KEY, MyRunsDialogFragment.TYPE_TIME)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "timePickerDialog")
    }
    private fun showInputDialog(tag: String, title: String, inputType: Int, hint: String){
        val dialog = MyRunsDialogFragment()
        val args = Bundle()
        args.putInt(MyRunsDialogFragment.DIALOG_TYPE_KEY, MyRunsDialogFragment.TYPE_INPUT)
        args.putString(MyRunsDialogFragment.TITLE_KEY, title)
        args.putInt(MyRunsDialogFragment.INPUT_TYPE_KEY, inputType)
        args.putString(MyRunsDialogFragment.HINT_KEY,hint)
        dialog.arguments = args
        dialog.show(supportFragmentManager, tag)
    }

}