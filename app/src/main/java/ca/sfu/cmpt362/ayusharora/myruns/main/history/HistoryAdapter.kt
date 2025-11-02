package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.WorkoutFormatter

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

        WorkoutFormatter.initialize(context, currentEntry)

        // TextViews
        val firstRow = view.findViewById<TextView>(R.id.adapter_first_row)
        firstRow.text = "${WorkoutFormatter.inputType}: ${WorkoutFormatter.activityType}, ${WorkoutFormatter.dateTime}"
        val secondRow = view.findViewById<TextView>(R.id.adapter_second_row)
        secondRow.text = "${WorkoutFormatter.duration}, ${WorkoutFormatter.distance}"

        return view
    }

    // Update the local copy of database entry array with a new one and update the display
    fun replace(newList: List<ExerciseEntry>){
        this.workoutList = newList
        notifyDataSetChanged()
    }

}