package ca.sfu.cmpt362.ayusharora.myruns.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.fragments.MyRunsDialogFragment
import java.util.Calendar

class ManualInputActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var listView: ListView
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        setup()
        handleListItems()
        handleButtonClicks()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    }
    private fun setup(){
        listView = findViewById(R.id.mi_listview)
        saveButton = findViewById(R.id.mi_button_save)
        cancelButton = findViewById(R.id.mi_button_cancel)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.manual_entry_fields)
        )
        listView.adapter = arrayAdapter
    }
    private fun handleListItems(){
        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0->{
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        this, this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
                1->{
                    val calendar = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(
                        this, this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true
                    )
                    timePickerDialog.show()
                }
                2->{
                    val dialog = MyRunsDialogFragment()
                    val args = Bundle()
                    args.putInt(MyRunsDialogFragment.DIALOG_TYPE_KEY, MyRunsDialogFragment.TYPE_INPUT)
                    args.putString(MyRunsDialogFragment.TITLE_KEY, "Duration")
                    args.putInt(MyRunsDialogFragment.INPUT_TYPE_KEY, android.text.InputType.TYPE_CLASS_NUMBER)
                    dialog.arguments = args
                    dialog.show(supportFragmentManager, "durationDialog")
                }
                3->{
                    val dialog = MyRunsDialogFragment()
                    val args = Bundle()
                    args.putInt(MyRunsDialogFragment.DIALOG_TYPE_KEY, MyRunsDialogFragment.TYPE_INPUT)
                    args.putString(MyRunsDialogFragment.TITLE_KEY, "Distance")
                    args.putInt(MyRunsDialogFragment.INPUT_TYPE_KEY, android.text.InputType.TYPE_CLASS_NUMBER)
                    dialog.arguments = args
                    dialog.show(supportFragmentManager, "distanceDialog")
                }
                4->{
                    val dialog = MyRunsDialogFragment()
                    val args = Bundle()
                    args.putInt(MyRunsDialogFragment.DIALOG_TYPE_KEY, MyRunsDialogFragment.TYPE_INPUT)
                    args.putString(MyRunsDialogFragment.TITLE_KEY, "Calories")
                    args.putInt(MyRunsDialogFragment.INPUT_TYPE_KEY, android.text.InputType.TYPE_CLASS_NUMBER)
                    dialog.arguments = args
                    dialog.show(supportFragmentManager, "CaloriesDialog")
                }
                5->{
                    val dialog = MyRunsDialogFragment()
                    val args = Bundle()
                    args.putInt(MyRunsDialogFragment.DIALOG_TYPE_KEY, MyRunsDialogFragment.TYPE_INPUT)
                    args.putString(MyRunsDialogFragment.TITLE_KEY, "Heart Rate")
                    args.putInt(MyRunsDialogFragment.INPUT_TYPE_KEY, android.text.InputType.TYPE_CLASS_NUMBER)
                    dialog.arguments = args
                    dialog.show(supportFragmentManager, "heartRateDialog")
                }
                6->{
                    val dialog = MyRunsDialogFragment()
                    val args = Bundle()
                    args.putInt(MyRunsDialogFragment.DIALOG_TYPE_KEY, MyRunsDialogFragment.TYPE_INPUT)
                    args.putString(MyRunsDialogFragment.TITLE_KEY, "Comments")
                    args.putInt(MyRunsDialogFragment.INPUT_TYPE_KEY, android.text.InputType.TYPE_CLASS_TEXT)
                    dialog.arguments = args
                    dialog.show(supportFragmentManager, "commentDialog")
                }

            }
        }
    }
    private fun handleButtonClicks(){
        saveButton.setOnClickListener {
            //TODO: Save to database
            finish()
        }

        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}