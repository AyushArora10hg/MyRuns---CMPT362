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
import java.util.Calendar

class ManualInputActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var listView: ListView
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        listView = findViewById(R.id.mi_listview)
        saveButton = findViewById(R.id.mi_button_save)
        cancelButton = findViewById(R.id.mi_button_cancel)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.manual_entry_fields)
        )
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0->{
                    val datePickerDialog = DatePickerDialog(
                        this, this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
                1->{
                    val timePickerDialog = TimePickerDialog(
                        this, this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true
                    )
                    timePickerDialog.show()
                }
                2->{}
                3->{}
                4->{}
                5->{}
                6->{}

            }
        }

        saveButton.setOnClickListener {
            //TODO: Save to database
            finish()
        }

        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry Discarded", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    }
}