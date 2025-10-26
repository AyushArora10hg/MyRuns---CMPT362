package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.Util

//Custom adapter inflating two text views to each entry of a ListView
// Code adapted from XD's lecture demos
class HistoryAdapter (private val context: Context, private var workoutList: List<ExerciseEntry>) : BaseAdapter(){

    override fun getCount(): Int {
        return workoutList.size
    }

    override fun getItem(position: Int): Any? {
        return workoutList[position]
    }

    override fun getItemId(position: Int): Long {
        return workoutList[position].id
    }

    // This method grabs all activity details and retrieves there attributes to display
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = View.inflate(context, R.layout.adapter_fragment_history, null)
        val currentEntry = workoutList[position]

        // inputType
        val inputTypeArray = context.resources.getStringArray(R.array.input_type)
        val inputType = inputTypeArray[currentEntry.inputType]

        // activityType
        val activityTypeArray = context.resources.getStringArray(R.array.activity_type)
        val activityType = activityTypeArray[currentEntry.activityType]

        //dateTime
        val dateTime = currentEntry.dateTime

        //duration
        val duration = currentEntry.duration

        //distance
        var distance = currentEntry.distance
        val unitArray = context.resources.getStringArray(R.array.unit_values)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var unit = sharedPreferences.getString("unit_preference", unitArray[0])
        if (unit == unitArray[0]){
            unit = "km"
        } else if (unit == unitArray[1]){
            unit = "mi"
            distance = Util.convertKilometersToMiles(distance)
        }

        // TextViews
        val firstRow = view.findViewById<TextView>(R.id.adapter_first_row)
        firstRow.text = "$inputType: $activityType, ${Util.formatDateTime(dateTime)}"
        val secondRow = view.findViewById<TextView>(R.id.adapter_second_row)
        secondRow.text = "${Util.formatDuration(duration)}, $distance $unit"

        return view
    }

    // Update the local copy of database entry array with a new one and update the display
    fun replace(newList: List<ExerciseEntry>){
        this.workoutList = newList
        notifyDataSetChanged()
    }

}