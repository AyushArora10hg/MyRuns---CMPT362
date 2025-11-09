package ca.sfu.cmpt362.ayusharora.myruns.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.displayentry.DisplayEntryActivity
import ca.sfu.cmpt362.ayusharora.myruns.manualinput.ManualInputActivity
import ca.sfu.cmpt362.ayusharora.myruns.mapdisplay.MapDisplayActivity

class StartFragment : Fragment() {

    private lateinit var inputTypeSpinner: Spinner
    private lateinit var activityTypeSpinner: Spinner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_start, container, false)

        inputTypeSpinner = view.findViewById(R.id.start_spinner_input)
        setupInputSpinner()

        activityTypeSpinner = view.findViewById(R.id.start_spinner_activity)
        setupActivitySpinner()

        handleStartButtonClick(view)

        return view
    }

    // This helper method loads values of input types listed in arrays.xml to a spinner using adapter
    private fun setupInputSpinner(){
        val inputAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.input_type)
        )
        inputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputTypeSpinner.adapter = inputAdapter
    }

    // This helper method loads values of activity types listed in arrays.xml to a spinner using adapter
    private fun setupActivitySpinner(){
        val activityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.activity_type)
        )
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityTypeSpinner.adapter = activityAdapter
    }

    // Start button handler to launch new activities based on values of spinners
    private fun handleStartButtonClick(view:View){

        val startButton = view.findViewById<Button>(R.id.start_button_start)
        startButton.setOnClickListener {
            val pos = inputTypeSpinner.selectedItemPosition
            when(pos){
                0->{
                    val intent = Intent(requireContext(), ManualInputActivity::class.java)
                    intent.putExtra(ManualInputActivity.INPUT_TYPE, pos)
                    intent.putExtra(ManualInputActivity.ACTIVITY_TYPE, activityTypeSpinner.selectedItemPosition)
                    startActivity(intent)

                }
                1, 2->{
                    val intent = Intent(requireContext(), MapDisplayActivity::class.java)
                    intent.putExtra(MapDisplayActivity.MODE, MapDisplayActivity.MODE_TRACKING)
                    intent.putExtra(MapDisplayActivity.INPUT_TYPE, pos)
                    intent.putExtra(MapDisplayActivity.ACTIVITY_TYPE, activityTypeSpinner.selectedItemPosition)
                    startActivity(intent)

                }
            }
        }
    }
}