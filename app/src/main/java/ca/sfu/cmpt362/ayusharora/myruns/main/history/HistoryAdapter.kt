package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.R

class HistoryAdapter (private val context: Context, private var workoutList: List<ExerciseEntry>) : BaseAdapter(){

    private val inputTypeArray = context.resources.getStringArray(R.array.input_type)
    private val activityTypeArray = context.resources.getStringArray(R.array.activity_type)
    private val unitArray = context.resources.getStringArray(R.array.unit_values)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getCount(): Int {
        return workoutList.size
    }

    override fun getItem(position: Int): Any? {
        return workoutList[position]
    }

    override fun getItemId(position: Int): Long {
        return workoutList[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = View.inflate(context, R.layout.adapter_fragment_history, null)
        val firstRow = view.findViewById<TextView>(R.id.adapter_first_row)
        val secondRow = view.findViewById<TextView>(R.id.adapter_second_row)

        val currentEntry = workoutList[position]

        val inputType = inputTypeArray[currentEntry.inputType]
        val activityType = activityTypeArray[currentEntry.activityType]
        val time = "9:01:45"
        val date = "Oct 23 2025"
        val duration = currentEntry.duration
        var distance = currentEntry.distance

        var unit = sharedPreferences.getString("unit_preference", unitArray[0])
        if (unit == unitArray[0]){
            unit = "km"
        } else if (unit == unitArray[1]){
            unit = "mi"
            distance = "%.3f".format(distance / 1.6094).toDouble()
        }

        firstRow.text = "$inputType: $activityType, $time $date"
        secondRow.text = "$duration, $distance $unit"

        return view
    }

    fun replace(newList: List<ExerciseEntry>){
        this.workoutList = newList
        notifyDataSetChanged()
    }

}