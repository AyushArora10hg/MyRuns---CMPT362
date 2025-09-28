package ca.sfu.cmpt362.ayusharora.myruns

import android.content.DialogInterface
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class MyRunsDialogFragment: DialogFragment(), DialogInterface.OnClickListener {



    override fun onClick(dialog: DialogInterface?, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            Toast.makeText(activity, "Saved!", Toast.LENGTH_LONG).show()
        } else if (item == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(activity, "Discarded!", Toast.LENGTH_LONG).show()
        }
    }
}