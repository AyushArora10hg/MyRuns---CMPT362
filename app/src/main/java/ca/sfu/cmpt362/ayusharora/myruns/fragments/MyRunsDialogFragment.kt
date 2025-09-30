package ca.sfu.cmpt362.ayusharora.myruns.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ca.sfu.cmpt362.ayusharora.myruns.R

class MyRunsDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        const val DIALOG_TYPE_KEY = "dialogType"
        const val TITLE_KEY = "title"
        const val INPUT_TYPE_KEY = "inputType"
        const val OPTIONS = "options"
        const val TYPE_INPUT = 1
        const val TYPE_OPTION = 2
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        val type = arguments?.getInt(DIALOG_TYPE_KEY)
        val title = arguments?.getString(TITLE_KEY)
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
        } else if (type == TYPE_OPTION){
            val view = requireActivity().layoutInflater.inflate(
                R.layout.fragment_options_dialog,
                null)
            val options = arguments?.getStringArray(OPTIONS)!!
            val listView = view.findViewById<ListView>(R.id.od_listview)
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),android.R.layout.simple_list_item_1,options
            )
            listView.adapter = arrayAdapter
            listView.setOnItemClickListener { parent, view, position, id ->
                parentFragmentManager.setFragmentResult(
                    "selectedChoice",
                    Bundle().apply { putInt("choice", position) }
                )
                dismiss()
            }
            builder.setTitle(title)
            builder.setView(view)
        }
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface?, item: Int) {
        //TODO: Save to databse/cancel
    }
}