package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabaseDao
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel

//Code adapted from XD's demo code (static fragment)
class HistoryFragment : Fragment() {

    private lateinit var arrayList : ArrayList<ExerciseEntry>
    private lateinit var listView: ListView
    private lateinit var arrayAdapter: HistoryAdapter

    private lateinit var db : WorkoutDatabase
    private lateinit var dao : WorkoutDatabaseDao
    private lateinit var repository: WorkoutRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var workoutViewModel: WorkoutViewModel

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_history, container, false)

        listView = view.findViewById<ListView>(R.id.history_listview)
        arrayList = ArrayList()
        arrayAdapter = HistoryAdapter(requireActivity(), arrayList)
        listView.adapter = arrayAdapter

        db = WorkoutDatabase.getInstance(requireActivity())
        dao = db.workoutDatabaseDao
        repository = WorkoutRepository(dao)
        viewModelFactory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider (requireActivity(), viewModelFactory)[WorkoutViewModel::class.java]

        workoutViewModel.allWorkouts.observe(viewLifecycleOwner) { workouts ->
            arrayAdapter.replace(workouts)
            arrayAdapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Refresh when tab becomes visible
        workoutViewModel.allWorkouts.value?.let { workouts ->
            arrayAdapter.replace(workouts)
            arrayAdapter.notifyDataSetChanged()
        }
    }
}