package ca.sfu.cmpt362.ayusharora.myruns.main.history

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabaseDao
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import ca.sfu.cmpt362.ayusharora.myruns.displayentry.DisplayEntryActivity

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

    private lateinit var sharedPreferences: SharedPreferences
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == "unit_preference") {
            arrayAdapter.notifyDataSetChanged()
        }
    }

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

        workoutViewModel.allWorkouts.observe(requireActivity(), Observer { it ->
            arrayAdapter.replace(it)
        })

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireContext(), DisplayEntryActivity::class.java)
            intent.putExtra("position", position)
            startActivity(intent)
            true
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        sharedPreferences.registerOnSharedPreferenceChangeListener (preferenceChangeListener)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}