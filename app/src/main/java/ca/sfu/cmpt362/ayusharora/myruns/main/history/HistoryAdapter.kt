package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.R

class HistoryAdapter (private val context: Context, private var workoutList: List<ExerciseEntry>) : BaseAdapter(){

    private val inputTypeArray = context.resources.getStringArray(R.array.input_type)
    private val activityTypeArray = context.resources.getStringArray(R.array.activity_type)

    override fun getCount(): Int {
        return workoutList.size
    }

    override fun getItem(position: Int): Any? {
        return workoutList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
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

        firstRow.text = "$inputType: $activityType, $time $date"
        secondRow.text = "${currentEntry.duration}, ${currentEntry.distance}"

        return view
    }

    fun replace(newList: List<ExerciseEntry>){
        this.workoutList = newList
    }

}