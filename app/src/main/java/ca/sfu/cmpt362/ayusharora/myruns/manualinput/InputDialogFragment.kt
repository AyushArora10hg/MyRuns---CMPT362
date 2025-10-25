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
    // Holding constants for other classes to send in arguments like what dialog to create and with what attributes.
    companion object {
        const val DIALOG_TYPE_KEY = "dialogType"
        const val TITLE_KEY = "title"
        const val UNIT_KEY = "unit"
        const val INPUT_TYPE_KEY = "inputType"
        const val HINT_KEY = "editTextHint"
        const val DEFAULT_TEXT_KEY = "defaultText"
        const val TYPE_DATE = 0
        const val TYPE_TIME = 1
        const val TYPE_INPUT = 2
    }

    private lateinit var editText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val type = arguments?.getInt(DIALOG_TYPE_KEY)
        var dialog: Dialog
        when(type){
            TYPE_DATE->{ dialog = createDatePickerDialog() }
            TYPE_TIME->{ dialog = createTimePickerDialog() }
            TYPE_INPUT->{ dialog = createUserInputDialog() }
            else -> { dialog = AlertDialog.Builder(requireActivity()).create() }
        }
        return dialog
    }

    // The method returns the selected date in the date picker dialog as a bundle
    // The activity/fragment creating this dialog can extract the result for use
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val bundle = Bundle().apply {
            putInt("selected_year", year)
            putInt("selected_month", month)
            putInt("selected_day", dayOfMonth)
        }
        parentFragmentManager.setFragmentResult("selected_date", bundle)
    }

    // The method returns the selected time in the time picker dialog as a bundle
    // The activity/fragment creating this dialog can extract the result for use
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val bundle = Bundle().apply {
            putInt("selected_hour", hourOfDay)
            putInt("selected_minute", minute)
        }
        parentFragmentManager.setFragmentResult("selected_time", bundle)
    }

    // The method returns the user input value in the input type dialog as a bundle
    // The activity/fragment creating this dialog can extract the result for use
    override fun onClick(dialog: DialogInterface?, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            val title = arguments?.getString(TITLE_KEY).toString().lowercase().trim()
            val bundle = Bundle().apply {
                putString("user_input", editText.text.toString())
            }
            parentFragmentManager.setFragmentResult("input_$title", bundle)
        }
    }

    // returns a datePicker Dialog
    private fun createDatePickerDialog() : Dialog{
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        return dialog
    }

    // returns a timePickerDialog
    private fun createTimePickerDialog() : Dialog{
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        return dialog
    }

    // returns a dialog taking user input
    // handles the arguments sent to this fragment and create a customized dialog based on them
    private fun createUserInputDialog() : Dialog{
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_input_dialog, null)
        editText = view.findViewById(R.id.id_edittext)

        //extract arguments given by the caller
        val title = arguments?.getString(TITLE_KEY)
        val unit = arguments?.getString(UNIT_KEY)
        val inputType = arguments?.getInt(INPUT_TYPE_KEY)
        val hint = arguments?.getString(HINT_KEY)
        val text = arguments?.getString(DEFAULT_TEXT_KEY)

        // set the arguments and create the dialog
        if (inputType != null) {
            editText.inputType = inputType
        }
        if (hint != null) {
            editText.setHint(hint)
        }
        if (text!= null) {
            editText.setText(text)
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        builder.setTitle("$title $unit")
        builder.setPositiveButton("Save", this)
        builder.setNegativeButton("Cancel", this)

        return builder.create()
    }
}