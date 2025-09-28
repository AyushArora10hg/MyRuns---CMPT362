package ca.sfu.cmpt362.ayusharora.myruns.fragments

import android.content.DialogInterface
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class DialogFragment : DialogFragment(), DialogInterface.OnClickListener {


    override fun onClick(dialog: DialogInterface?, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "ok clicked", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}