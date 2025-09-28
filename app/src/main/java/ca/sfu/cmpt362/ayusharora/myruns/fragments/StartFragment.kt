package ca.sfu.cmpt362.ayusharora.myruns.fragments

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
import ca.sfu.cmpt362.ayusharora.myruns.activities.ManualInputActivity
import ca.sfu.cmpt362.ayusharora.myruns.activities.MapDisplayActivity

class StartFragment : Fragment() {

    private lateinit var inputTypeSpinner: Spinner
    private lateinit var activityTypeSpinner: Spinner
    private lateinit var startButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ret =  inflater.inflate(R.layout.fragment_start, container, false)

        inputTypeSpinner = ret.findViewById(R.id.start_spinner_input)
        activityTypeSpinner = ret.findViewById(R.id.start_spinner_activity)
        startButton = ret.findViewById(R.id.start_button_start)

        val inputAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.input_type)
        )
        inputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputTypeSpinner.adapter = inputAdapter

        val activityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.activity_type)
        )
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityTypeSpinner.adapter = activityAdapter

        startButton.setOnClickListener {
            when(inputTypeSpinner.selectedItemPosition){
                0->{
                    val intent = Intent(requireContext(), ManualInputActivity::class.java)
                    startActivity(intent)
                    intent.putExtra("ACTIVITY_TYPE", activityTypeSpinner.selectedItem.toString())
                }
                1, 2->{
                    val intent = Intent(requireContext(), MapDisplayActivity::class.java)
                    startActivity(intent)
                    intent.putExtra("ACTIVITY_TYPE", activityTypeSpinner.selectedItem.toString())
                }
            }
        }

        return ret
    }
}