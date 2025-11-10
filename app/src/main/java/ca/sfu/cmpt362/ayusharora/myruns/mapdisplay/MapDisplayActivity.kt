package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.WorkoutFormatter
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val TAG = "MapDisplayActivity"
        const val MODE = "mode"
        const val ENTRY_POSITION = "entry_position"
        const val INPUT_TYPE = "input_type"
        const val ACTIVITY_TYPE = "activity_type"
        const val MODE_TRACKING = 0
        const val MODE_HISTORY = 1
    }

    private var mode = MODE_TRACKING
    private var startTimeMillis = 0L

    private lateinit var typeTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var curSpeedTextView: TextView
    private lateinit var avgSpeedTextView: TextView
    private lateinit var calorieTextView: TextView
    private lateinit var climbTextView: TextView

    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var mMap: GoogleMap
    private var startMarker: Marker? = null
    private var endMarker : Marker? = null
    private var polyline: Polyline? = null

    // Tracking mode only variables
    private var mapDisplayViewModel: MapDisplayViewModel? = null
    private var isBound = false
    private var shouldShowToast = false
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var startPlaced = false

    // History mode only variables
    private lateinit var historyEntry: ExerciseEntry
    private lateinit var deleteButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        mode = intent.getIntExtra(MODE, MODE_TRACKING)
        Log.d(TAG, "onCreate: mode = $mode")

        initializeViews()
        loadDatabase()
        showGoogleMap()

        when (mode) {
            MODE_TRACKING -> launchTrackingMode()
            MODE_HISTORY -> launchHistoryMode()
        }
    }

    private fun initializeViews() {
        typeTextView = findViewById(R.id.md_stats_type)
        distanceTextView = findViewById(R.id.md_stats_distance)
        curSpeedTextView = findViewById(R.id.md_stats_current_speed)
        avgSpeedTextView = findViewById(R.id.md_stats_average_speed)
        calorieTextView = findViewById(R.id.md_stats_calories)
        climbTextView = findViewById(R.id.md_stats_climb)

        saveButton = findViewById(R.id.md_button_save)
        cancelButton = findViewById(R.id.md_button_cancel)
        deleteButton = findViewById(R.id.md_button_delete)
    }

    private fun launchTrackingMode() {

        handleSaveAndCancelButtons()
        deleteButton.visibility = View.GONE

        if (startTimeMillis == 0L) {
            startTimeMillis = System.currentTimeMillis()
        }

        // Initialize entry with input parameters
        workoutViewModel.entry.inputType = intent.getIntExtra(INPUT_TYPE, -1)
        workoutViewModel.entry.activityType = intent.getIntExtra(ACTIVITY_TYPE, -1)

        Log.d(TAG, "Input type: ${workoutViewModel.entry.inputType}, Activity type: ${workoutViewModel.entry.activityType}")

        // Observe database for toast notification
        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (shouldShowToast && workouts.isNotEmpty()) {
                Toast.makeText(this, "Entry #${workouts.last().id} saved!", Toast.LENGTH_SHORT).show()
                shouldShowToast = false
            }
        }


        initializeMapDisplayViewModel()
        observeLocationChanges()
        startTrackingService()
    }

    private fun launchHistoryMode() {
        saveButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
        handleDeleteButton()

        val pos = intent.getIntExtra(ENTRY_POSITION, -1)
        Log.d(TAG, "Entry position: $pos")

        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (pos != -1 && pos < workouts.size) {
                historyEntry = workouts[pos]
                displayHistoryEntry()
            } else {
                Log.e(TAG, "Invalid entry position: $pos")
            }
        }
    }

    private fun displayHistoryEntry() {
        WorkoutFormatter.initialize(this, historyEntry)

        typeTextView.text = "Type: ${WorkoutFormatter.activityType}"
        distanceTextView.text = "Distance: ${WorkoutFormatter.distance}"
        curSpeedTextView.text = "Current Speed: N/A"
        avgSpeedTextView.text = "Average Speed: ${WorkoutFormatter.avgSpeed}"
        calorieTextView.text = "Calories: ${WorkoutFormatter.calories}"
        climbTextView.text = "Climb: ${WorkoutFormatter.climb}"

        if (::mMap.isInitialized) {
            displayActivityTrace()
        }
    }

    private fun displayActivityTrace() {
        if (historyEntry.locationList.isEmpty()) return

        Log.d(TAG, "Displaying trace with ${historyEntry.locationList.size} points")

        startMarker = mMap.addMarker(
            MarkerOptions()
                .position(historyEntry.locationList.first())
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        if (historyEntry.locationList.size > 1) {
            endMarker = mMap.addMarker(
                MarkerOptions()
                    .position(historyEntry.locationList.last())
                    .title("End")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            polyline = mMap.addPolyline(
                PolylineOptions()
                    .addAll(historyEntry.locationList)
                    .color(android.graphics.Color.BLACK)
                    .width(10f)
            )
        }

        centerMapOnRoute(historyEntry.locationList)
    }

    private fun centerMapOnRoute(locations: ArrayList<LatLng>) {
        if (locations.isEmpty()) return

        if (locations.size == 1) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.first(), 17f))
        } else {
            val builder = LatLngBounds.Builder()
            locations.forEach { builder.include(it) }
            val bounds = builder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun initializeMapDisplayViewModel() {
        Log.d(TAG, "Initializing MapDisplayViewModel")
        mapDisplayViewModel = ViewModelProvider(this)[MapDisplayViewModel::class.java]
    }

    private fun observeLocationChanges() {
        mapDisplayViewModel?.let { viewModel ->
            WorkoutFormatter.initialize(this, workoutViewModel.entry)
            typeTextView.text = "Type: ${WorkoutFormatter.activityType}"
            climbTextView.text = "Climb: ${WorkoutFormatter.climb}"

            viewModel.currentLocation.observe(this) { location ->
                workoutViewModel.entry.locationList.add(location)
                updateMapWithCurrentLocation(location)
            }

            viewModel.distance.observe(this) { d ->
                workoutViewModel.entry.distance = d
                WorkoutFormatter.initialize(this, workoutViewModel.entry)
                distanceTextView.text = "Distance: ${WorkoutFormatter.distance}"
            }

            viewModel.curSpeed.observe(this) { speed ->
                curSpeedTextView.text = "Current Speed: ${WorkoutFormatter.convertSpeedForDisplay(speed)}"
            }

            viewModel.avgSpeed.observe(this) { s ->
                workoutViewModel.entry.avgSpeed = s
                WorkoutFormatter.initialize(this, workoutViewModel.entry)
                avgSpeedTextView.text = "Average Speed: ${WorkoutFormatter.avgSpeed}"
            }

            viewModel.calories.observe(this) { cal ->
                workoutViewModel.entry.calorie = cal
                WorkoutFormatter.initialize(this, workoutViewModel.entry)
                calorieTextView.text = "Calories: ${WorkoutFormatter.calories}"
            }
        } ?: run {
            Log.e(TAG, "MapDisplayViewModel is null!")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d(TAG, "Map is ready")

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    private fun showGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.md_fragment_container)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun startTrackingService() {
        Log.d(TAG, "Starting tracking service")

        mapDisplayViewModel?.let { viewModel ->
            val serviceIntent = Intent(this, TrackingService::class.java)
            startService(serviceIntent)
            bindService(serviceIntent, viewModel, BIND_AUTO_CREATE)
            isBound = true
            Log.d(TAG, "Service started and bound")
        } ?: run {
            Log.e(TAG, "Cannot start service - MapDisplayViewModel is null")
        }
    }

    private fun updateMapWithCurrentLocation(location: LatLng) {

        if (!::mMap.isInitialized) {
            return
        }

        if (!startPlaced) {
            startMarker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
            startPlaced = true
        } else {
            endMarker?.remove()
            endMarker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Current")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
            updatePolyline()
        }

    }

    private fun updatePolyline() {
        polyline?.remove()

        polyline = mMap.addPolyline(
            PolylineOptions()
                .addAll(workoutViewModel.entry.locationList)
                .color(android.graphics.Color.BLACK)
                .width(10f)
        )
    }

    private fun stopTrackingAndFinish() {
        Log.d(TAG, "Stopping tracking")

        if (isBound) {
            mapDisplayViewModel?.let { unbindService(it) }
            isBound = false
            Log.d(TAG, "Service unbound")
        }

        val serviceIntent = Intent(this, TrackingService::class.java)
        stopService(serviceIntent)

        finish()
    }

    private fun handleSaveAndCancelButtons() {
        saveButton.setOnClickListener {
            Log.d(TAG, "Save button clicked")
            val duration = (System.currentTimeMillis() - startTimeMillis) / 60000.0
            workoutViewModel.entry.duration = duration
            workoutViewModel.insert()
            shouldShowToast = true
            stopTrackingAndFinish()
        }

        cancelButton.setOnClickListener {
            Log.d(TAG, "Cancel button clicked")
            stopTrackingAndFinish()
        }
    }

    private fun handleDeleteButton(){

        deleteButton.setOnClickListener {
            workoutViewModel.deleteEntry(historyEntry.id)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        if (isBound && mode == MODE_TRACKING) {
            mapDisplayViewModel?.let { unbindService(it) }
            isBound = false
            Log.d(TAG, "Service unbound in onDestroy")
        }
    }

    private fun loadDatabase() {
        val db = WorkoutDatabase.getInstance(this)
        val dao = db.workoutDatabaseDao
        val repository = WorkoutRepository(dao)
        val factory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]
        Log.d(TAG, "Database loaded")
    }
}