package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.WorkoutFormatter
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import ca.sfu.cmpt362.ayusharora.myruns.dialogs.InputDialogFragment
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
        const val MODE = "mode"
        const val ENTRY_POSITION = "entry_position"
        const val INPUT_TYPE = "input_type"
        const val ACTIVITY_TYPE = "activity_type"
        const val MODE_TRACKING = 0
        const val MODE_HISTORY = 1
    }

    // Mode Tracking
    private var mode = MODE_TRACKING

    // Display TextViews : Common to both modes
    private lateinit var typeTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var curSpeedTextView: TextView
    private lateinit var avgSpeedTextView: TextView
    private lateinit var calorieTextView: TextView
    private lateinit var climbTextView: TextView
    private lateinit var commentsTextView: TextView

    // To save to and load from database
    private lateinit var workoutViewModel: WorkoutViewModel

    // Google Map
    private lateinit var mMap: GoogleMap
    private var startMarker: Marker? = null
    private var endMarker : Marker? = null
    private var polyline: Polyline? = null

    // Tracking mode only variables
    private lateinit var mapDisplayViewModel: MapDisplayViewModel
    private var isBound = false
    private var shouldShowToast = false
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var startPlaced = false
    private var dialogShown = false

    // History mode only variables
    private lateinit var historyEntry: ExerciseEntry
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        mode = intent.getIntExtra(MODE, MODE_TRACKING)
        initializeViews()
        loadDatabase()
        showGoogleMap()
        when (mode) {
            MODE_TRACKING -> launchTrackingMode()
            MODE_HISTORY -> launchHistoryMode()
        }

        onBackPressedDispatcher.addCallback(this) {
            if (mode == MODE_TRACKING) {
                stopTracking()
            }
            finish()
        }
    }

    // Initializes all UI views used by the activity
    private fun initializeViews() {
        typeTextView = findViewById(R.id.md_stats_type)
        distanceTextView = findViewById(R.id.md_stats_distance)
        curSpeedTextView = findViewById(R.id.md_stats_current_speed)
        avgSpeedTextView = findViewById(R.id.md_stats_average_speed)
        calorieTextView = findViewById(R.id.md_stats_calories)
        climbTextView = findViewById(R.id.md_stats_climb)
        commentsTextView = findViewById(R.id.md_stats_comments)

        saveButton = findViewById(R.id.md_button_save)
        cancelButton = findViewById(R.id.md_button_cancel)
        deleteButton = findViewById(R.id.md_button_delete)
    }

    // Provides database access to this activity
    // MODE_TRACKING -> needs database access to save the workout
    // MODE_HISTORY -> needs database access to view a saved workout
    private fun loadDatabase() {
        val db = WorkoutDatabase.getInstance(this)
        val dao = db.workoutDatabaseDao
        val repository = WorkoutRepository(dao)
        val factory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]
    }

    // Helper method to load a Google Map
    // Code adapted from lecture demos (I_am_here_map_Kotlin)
    private fun showGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.md_fragment_container)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Callback invoked when the Google Map is ready to be used.
    // This method is called after getMapAsync() completes successfully.
    //
    // Configures map UI settings and restores/displays the appropriate map state:
    // - TRACKING MODE: If location data exists , restores markers and polyline to
    //   show the current workout progress (eg. after rotation).
    // - HISTORY MODE: If history entry is loaded, displays the complete workout trace.
    //
    // Note: This callback may be invoked before historyEntry is initialized in history mode,
    // so displayActivityTrace() is called again in displayHistoryEntry() if needed
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        when (mode) {
            MODE_TRACKING -> {
                if (workoutViewModel.entry.locationList.isNotEmpty()) {
                    restoreMapStateFromEntry()
                }
            }
            MODE_HISTORY -> {
                if (::historyEntry.isInitialized) {
                    displayActivityTrace()
                }
            }
        }
    }

    // Restores the map state (start marker, end marker, polyline) from
    // the workout entry in the ViewModel. Used when the activity is
    // recreated during tracking (e.g., after screen rotation)
    private fun restoreMapStateFromEntry() {
        if (workoutViewModel.entry.locationList.isEmpty()) return

        startMarker?.remove()
        endMarker?.remove()
        polyline?.remove()

        startMarker = mMap.addMarker(
            MarkerOptions()
                .position(workoutViewModel.entry.locationList.first())
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        startPlaced = true

        if (workoutViewModel.entry.locationList.size > 1) {
            endMarker = mMap.addMarker(
                MarkerOptions()
                    .position(workoutViewModel.entry.locationList.last())
                    .title("Current")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            polyline = mMap.addPolyline(
                PolylineOptions()
                    .addAll(workoutViewModel.entry.locationList)
                    .color(android.graphics.Color.BLACK)
                    .width(10f)
            )
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(workoutViewModel.entry.locationList.last(), 17f))
    }

    // Unbinds the tracking service if still bound when the activity is destroyed
    // to prevent memory leaks. The activity can rebind to the same service again as service
    // is not killed during changes like screen rotation or app going to background
    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
    }



    // ******************************** TRACKING MODE ****************************************** //

    // Puts the activity into tracking mode
    // It initializes a MapDisplayViewModel instance for LiveData tracking from service
    // It overrides listeners of Save and Cancel buttons
    // It hides Comments Text View and Delete button as they are not required in this view
    // Uses WorkoutViewModel to have database access
    private fun launchTrackingMode() {

        mapDisplayViewModel = ViewModelProvider(this)[MapDisplayViewModel::class.java]
        mapDisplayViewModel.initializeStartTime()

        deleteButton.visibility = View.GONE
        commentsTextView.visibility = View.GONE
        handleSaveAndCancelButtons()

        workoutViewModel.entry.inputType = intent.getIntExtra(INPUT_TYPE, -1)
        workoutViewModel.entry.activityType = intent.getIntExtra(ACTIVITY_TYPE, -1)
        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (shouldShowToast && workouts.isNotEmpty()) {
                Toast.makeText(this, "Entry #${workouts.last().id} saved!", Toast.LENGTH_SHORT).show()
                shouldShowToast = false
            }
        }

        updateViews()
        startTrackingService()
    }

    // Starts the background TrackingService to record GPS locations, and
    // binds this activity to receive updates via the MapDisplayViewModel
    private fun startTrackingService() {

        mapDisplayViewModel.let { viewModel ->
            val serviceIntent = Intent(this, TrackingService::class.java)
            startService(serviceIntent)
            bindService(serviceIntent, viewModel, BIND_AUTO_CREATE)
            isBound = true
        }
    }

    // Observes LiveData in the MapDisplayViewModel and updates its UI
    // fields and map elements (distance, speed, calories, markers, polylines)
    // accordingly in real time
    private fun updateViews() {

        WorkoutFormatter.initialize(this, workoutViewModel.entry)

        mapDisplayViewModel.let { viewModel ->
            typeTextView.text = buildString {
                append("Type: ")
                append(WorkoutFormatter.activityType)
            }
            climbTextView.text = buildString {
                append("Climb: ")
                append(WorkoutFormatter.climb)
            }

            viewModel.currentLocation.observe(this) { location ->
                workoutViewModel.entry.locationList.add(location)
                updateMapWithCurrentLocation(location)
            }

            viewModel.distance.observe(this) { d ->
                workoutViewModel.entry.distance = d
                distanceTextView.text = buildString {
                    append("Distance: ")
                    append(WorkoutFormatter.distance)
                }
            }

            viewModel.curSpeed.observe(this) { speed ->
                curSpeedTextView.text = buildString {
                    append("Current Speed: ")
                    append(WorkoutFormatter.convertSpeedForDisplay(speed))
                }
            }

            viewModel.avgSpeed.observe(this) { s ->
                workoutViewModel.entry.avgSpeed = s
                avgSpeedTextView.text = buildString {
                    append("Average Speed: ")
                    append(WorkoutFormatter.avgSpeed)
                }
            }

            viewModel.calories.observe(this) { cal ->
                workoutViewModel.entry.calorie = cal
                calorieTextView.text = buildString {
                    append("Calories: ")
                    append(WorkoutFormatter.calories)
                }
            }
        }
    }

    // Helper method to update map elements
    // - Places a green "start" marker if not yet placed
    // - Moves the red “current” marker
    // - Updates the route polyline
    // - Keeps the camera centered on the latest position
    // Code adapted from lecture demos (I_am_here_map_Kotlin)
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

    // Refreshes the route polyline by redrawing all recorded points.
    // Called each time a new location update arrives.
    private fun updatePolyline() {
        polyline?.remove()

        polyline = mMap.addPolyline(
            PolylineOptions()
                .addAll(workoutViewModel.entry.locationList)
                .color(android.graphics.Color.BLACK)
                .width(10f)
        )
    }

    // Stops the tracking service, unbinds it from the activity
    private fun stopTracking() {

        if (isBound) {
            unbindService(mapDisplayViewModel)
            isBound = false
        }

        val serviceIntent = Intent(this, TrackingService::class.java)
        stopService(serviceIntent)
    }

    // Helper method to handle the Save and Cancel button clicks:
    // - Save: Inserts the recorded workout into the database
    // - Cancel: Discards the recording and exits without saving
    private fun handleSaveAndCancelButtons() {
        saveButton.setOnClickListener {
            if (workoutViewModel.entry.locationList.isEmpty()){
                Toast.makeText(this, "No Route to Save!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val duration = (System.currentTimeMillis() - mapDisplayViewModel.startTimeMillis) / 60000.0
                workoutViewModel.entry.duration = duration
                if (!dialogShown){
                    showCommentsDialog()
                    dialogShown = true
                    supportFragmentManager.setFragmentResultListener("input_comments", this) { _, bundle ->
                        val value = bundle.getString("user_input")
                        workoutViewModel.entry.comment = value ?: ""
                    }
                    stopTracking()
                }
                else {
                    workoutViewModel.insert()
                    shouldShowToast = true
                    finish()
                }
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    // Helper method that prompts a dialog asking for user comments
    private fun showCommentsDialog(){
        val dialog = InputDialogFragment()
        val args = Bundle()
        args.putInt(InputDialogFragment.DIALOG_TYPE_KEY, InputDialogFragment.TYPE_INPUT)
        args.putString(InputDialogFragment.TITLE_KEY, "Comments")
        args.putInt(InputDialogFragment.INPUT_TYPE_KEY, InputType.TYPE_CLASS_TEXT)
        args.putString(InputDialogFragment.HINT_KEY, "How did your workout go?")
        dialog.arguments = args
        dialog.show(supportFragmentManager, "commentsDialog")

    }

    // ******************************** HISTORY MODE ******************************************* //

    // Puts the activity into history mode
    // It overrides listeners of Delete button and hides the Save and Cancel buttons.
    // Uses WorkoutViewModel to have database access
    private fun launchHistoryMode() {

        saveButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
        curSpeedTextView.visibility = View.GONE
        handleDeleteButton()

        val pos = intent.getIntExtra(ENTRY_POSITION, -1)

        workoutViewModel.allWorkouts.observe(this) { workouts ->
            if (pos != -1 && pos < workouts.size) {
                historyEntry = workouts[pos]
                displayHistoryEntry()
            }
        }
    }

    // This helper method populate the UI fields (type, distance, average speed, etc.)
    // by getting the respective values from historyEntry (from database)
    // If the map is ready, then it displays the trace (markers and polylines) of that
    // activity too
    private fun displayHistoryEntry() {
        WorkoutFormatter.initialize(this, historyEntry)

        typeTextView.text = buildString {
            append("Type: ")
            append(WorkoutFormatter.activityType)
        }
        distanceTextView.text = buildString {
            append("Distance: ")
            append(WorkoutFormatter.distance)
        }
        avgSpeedTextView.text = buildString {
            append("Average Speed: ")
            append(WorkoutFormatter.avgSpeed)
        }
        calorieTextView.text = buildString {
            append("Calories: ")
            append(WorkoutFormatter.calories)
        }
        climbTextView.text = buildString {
            append("Climb: ")
            append(WorkoutFormatter.climb)
        }
        commentsTextView.text = buildString {
            append("Comments: ")
            append(WorkoutFormatter.comment)
        }

        if (::mMap.isInitialized) {
            displayActivityTrace()
        }
    }

    // Draws the saved workout route (start marker, end marker, and polyline)
    // for a history entry and centers the map on the full path.
    private fun displayActivityTrace() {
        if (historyEntry.locationList.isEmpty()) return

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

    // Adjusts the map camera to fit the full recorded route.
    // If only one location exists, zooms directly on it; otherwise,
    // calculates bounds that include all recorded coordinates.
    private fun centerMapOnRoute(locations: ArrayList<LatLng>) {
        if (locations.isEmpty()) return

        if (locations.size == 1) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.first(), 17f))
        }
        // The following code is written with help of chatGPT
        // It helps to focus camera on an entire route (multiple locations) by calculating
        // bounds. The one in class only handled a single co-ordinate
        else {
            val builder = LatLngBounds.Builder()
            locations.forEach { builder.include(it) }
            val bounds = builder.build()
            val mapView = (supportFragmentManager.findFragmentById(R.id.md_fragment_container) as SupportMapFragment).view
            mapView?.post {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
            }
        }
    }

    // Handles the Delete button click listener
    // - Delete: Removes the historyEntry from database
    private fun handleDeleteButton(){

        deleteButton.setOnClickListener {
            workoutViewModel.allWorkouts.removeObservers(this)
            workoutViewModel.deleteEntry(historyEntry.id)
            finish()
        }
    }
}