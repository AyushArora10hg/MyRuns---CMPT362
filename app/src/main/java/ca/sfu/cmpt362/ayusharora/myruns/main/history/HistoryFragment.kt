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
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import ca.sfu.cmpt362.ayusharora.myruns.displayentry.DisplayEntryActivity

//Code adapted from XD's demo code (static fragment)
class HistoryFragment : Fragment() {

    // adapter to load elements
    private lateinit var arrayAdapter: HistoryAdapter

    // preference for unit info
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_history, container, false)

        setupListView(view)
        observeDatabase()
        observeUnitPreference()

        return view
    }

    // This helper method setups a listView and adds a click listener to open new activity
    // It sends in the position of the clicked item to DisplayEntryActivity, which in turns accesses
    // that particular entry from database with the help of the position sent to it via intent
    private fun setupListView (view:View){

        val listView = view.findViewById<ListView>(R.id.history_listview)
        val arrayList = ArrayList<ExerciseEntry>()
        arrayAdapter = HistoryAdapter(requireActivity(), arrayList)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(requireContext(), DisplayEntryActivity::class.java)
            intent.putExtra("position", position)
            startActivity(intent)
            true
        }
    }

    // This helper method loads the database and retrieves all entries in it
    // It also observes the database through a view model. Whenever a new activity is added to the
    // database, the listView updates in real time
    private fun observeDatabase(){
        val db = WorkoutDatabase.getInstance(requireActivity())
        val dao = db.workoutDatabaseDao
        val repository = WorkoutRepository(dao)
        val viewModelFactory = ViewModelFactory(repository)
        val workoutViewModel = ViewModelProvider (requireActivity(), viewModelFactory)[WorkoutViewModel::class.java]

        workoutViewModel.allWorkouts.observe(requireActivity(), Observer { it ->
            arrayAdapter.replace(it)
        })
    }

    // This helper method observes what unit system user has selected in the settings fragment
    // It updates the listView in real time too (triggers notifyDataSetChanged, which in turn
    // converts all values and units according to the selected system)
    private fun observeUnitPreference(){

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "unit_preference") {
                arrayAdapter.notifyDataSetChanged()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener (preferenceChangeListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}