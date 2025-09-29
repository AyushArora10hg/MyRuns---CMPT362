package ca.sfu.cmpt362.ayusharora.myruns.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ca.sfu.cmpt362.ayusharora.myruns.R

class DialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        const val DIALOG_TYPE_KEY = "dialogType"
        const val TITLE_KEY = "title"
        const val INPUT_TYPE_KEY = "inputType"

        const val TYPE_INPUT = 1
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val type = arguments?.getInt(DIALOG_TYPE_KEY)
        val title = arguments?.getString(TITLE_KEY)

        val builder = AlertDialog.Builder(requireActivity())
        if (type == TYPE_INPUT){
            val view = requireActivity().layoutInflater.inflate(
                R.layout.fragment_input_dialog,
                null)
            val editText = view.findViewById<EditText>(R.id.id_edittext)
            val inputType = arguments?.getInt(INPUT_TYPE_KEY)
            if (inputType != null) {
                editText.inputType = inputType
            }
            builder.setView(view)
            builder.setTitle(title)
            builder.setPositiveButton("Save", this)
            builder.setNegativeButton("Cancel", this)
        }
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface?, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "Saved!", Toast.LENGTH_SHORT).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "Entry Discarded", Toast.LENGTH_SHORT).show()
        }
    }
}