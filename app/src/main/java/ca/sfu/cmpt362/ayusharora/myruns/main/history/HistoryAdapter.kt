package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.R

class HistoryAdapter (private val context: Context, private var workoutList: List<ExerciseEntry>) : BaseAdapter(){
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
        val viewHolder: ViewHolder
        val rowView: View

        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_history, parent, false)
            viewHolder = ViewHolder(
                rowView.findViewById(R.id.adapter_first_row),
                rowView.findViewById(R.id.adapter_second_row)
            )
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }

        val currentEntry = workoutList[position]
        val inputType = context.resources.getStringArray(R.array.input_type)[currentEntry.inputType]
        val activityType = context.resources.getStringArray(R.array.activity_type)[currentEntry.activityType]

        viewHolder.firstRow.text = "$inputType: $activityType"
        viewHolder.secondRow.text = "${currentEntry.distance}, ${currentEntry.duration}"

        return rowView
    }

    data class ViewHolder(
        val firstRow: TextView,
        val secondRow: TextView
    )

    fun replace(newList: List<ExerciseEntry>){
        this.workoutList = newList
    }

}