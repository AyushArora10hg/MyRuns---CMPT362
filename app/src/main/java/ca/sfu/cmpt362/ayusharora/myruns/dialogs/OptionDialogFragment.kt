package ca.sfu.cmpt362.ayusharora.myruns.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import ca.sfu.cmpt362.ayusharora.myruns.R

// Code adapted from XD's lecture/demo on dialogs.
class OptionDialogFragment : DialogFragment() {

    // Holding constants for other classes to send in arguments like what dialog to create and with what attributes.
    companion object {
        const val TITLE_KEY = "title"
        const val OPTIONS = "options"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(
            R.layout.fragment_options_dialog,
            null)
        val title = arguments?.getString(TITLE_KEY)
        val options = arguments?.getStringArray(OPTIONS)!!
        val listView = view.findViewById<ListView>(R.id.od_listview)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_list_item_1, options
        )
        listView.adapter = arrayAdapter
        // Took help from ChatGpt on how to return something back to caller class.
        listView.setOnItemClickListener { parent, view, position, id ->
            parentFragmentManager.setFragmentResult(
                "selectedChoice",
                Bundle().apply { putInt("choice", position) }
            )
            dismiss()
        }
        builder.setTitle(title)
        builder.setView(view)
        return builder.create()
    }
}