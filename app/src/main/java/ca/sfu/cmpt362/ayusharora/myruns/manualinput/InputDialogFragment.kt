package ca.sfu.cmpt362.ayusharora.myruns.manualinput

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import ca.sfu.cmpt362.ayusharora.myruns.R
import java.util.Calendar

// Code adapted from XD's lecture/demo on dialogs.
class InputDialogFragment : DialogFragment(), DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    companion object {
        const val DIALOG_TYPE_KEY = "dialogType"
        const val TITLE_KEY = "title"
        const val INPUT_TYPE_KEY = "inputType"
        const val HINT_KEY = "editTextHint"
        const val TYPE_DATE = 0
        const val TYPE_TIME = 1
        const val TYPE_INPUT = 2
    }

    private lateinit var editText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        val type = arguments?.getInt(DIALOG_TYPE_KEY)
        var dialog : Dialog? = null

        when(type){

            TYPE_DATE->{
                val calendar = Calendar.getInstance()
                dialog = DatePickerDialog(
                    requireContext(),
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
            TYPE_TIME->{
                val calendar = Calendar.getInstance()
                dialog = TimePickerDialog(
                    requireContext(),
                    this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
            }
            TYPE_INPUT->{
                val view = requireActivity().layoutInflater.inflate(
                    R.layout.fragment_input_dialog,
                    null)
                val title = arguments?.getString(TITLE_KEY)
                editText = view.findViewById<EditText>(R.id.id_edittext)
                val inputType = arguments?.getInt(INPUT_TYPE_KEY)
                if (inputType != null) {
                    editText.inputType = inputType
                }
                val hint = arguments?.getString(HINT_KEY)
                if (hint != null) {
                    editText.setHint(hint)
                }
                builder.setView(view)
                builder.setTitle(title)
                builder.setPositiveButton("Save", this)
                builder.setNegativeButton("Cancel", this)
                dialog = builder.create()
            }
        }
        return dialog!!
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val bundle = Bundle().apply {
            putInt("selected_year", year)
            putInt("selected_month", month)
            putInt("selected_day", dayOfMonth)
        }
        parentFragmentManager.setFragmentResult("selected_date", bundle)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val bundle = Bundle().apply {
            putInt("selected_hour", hourOfDay)
            putInt("selected_minute", minute)
        }
        parentFragmentManager.setFragmentResult("selected_time", bundle)
    }

    override fun onClick(dialog: DialogInterface?, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            val title = arguments?.getString(TITLE_KEY).toString().lowercase().trim()
            val bundle = Bundle().apply {
                putString("user_input", editText.text.toString())
            }
            parentFragmentManager.setFragmentResult("input_$title", bundle)
        }
    }
}