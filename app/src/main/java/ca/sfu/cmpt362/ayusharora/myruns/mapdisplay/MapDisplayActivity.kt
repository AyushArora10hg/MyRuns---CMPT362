package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.ViewModelFactory
import ca.sfu.cmpt362.ayusharora.myruns.WorkoutFormatter
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutDatabase
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutRepository
import ca.sfu.cmpt362.ayusharora.myruns.database.WorkoutViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val TAG = "MapDisplayActivity"
    }

    private var startTimeMillis = 0L

    private lateinit var typeTextView: TextView

    private lateinit var distanceTextView: TextView

    private lateinit var curSpeedTextView: TextView

    private lateinit var avgSpeedTextView: TextView

    private lateinit var calorieTextView: TextView

    private lateinit var climbTextView: TextView

    private lateinit var mapDisplayViewModel: MapDisplayViewModel

    private lateinit var workoutViewModel: WorkoutViewModel

    private lateinit var mMap: GoogleMap

    private var isBound = false

    private var mapCentered = false

    private var startMarker: Marker? = null

    private var endMarker : Marker? = null

    private var polyline: Polyline? = null

    private var shouldShowToast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        if (startTimeMillis == 0L){
            startTimeMillis = System.currentTimeMillis()
        }

        typeTextView = findViewById(R.id.md_stats_type)
        distanceTextView = findViewById(R.id.md_stats_distance)
        curSpeedTextView = findViewById(R.id.md_stats_current_speed)
        avgSpeedTextView = findViewById(R.id.md_stats_average_speed)
        calorieTextView = findViewById(R.id.md_stats_calories)
        climbTextView = findViewById(R.id.md_stats_climb)

        loadAndObserveDatabase()
        observeLocationChanges()
        showGoogleMap()
        handleButtonClicks()
        startTrackingService()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d(TAG, "Map is ready")

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

    }

    private fun observeLocationChanges(){


        mapDisplayViewModel = ViewModelProvider(this)[MapDisplayViewModel::class.java]

        WorkoutFormatter.initialize(this, workoutViewModel.entry)

        typeTextView.text = "Type: ${WorkoutFormatter.activityType}"
        climbTextView.text = "Climb: ${WorkoutFormatter.climb}"

        mapDisplayViewModel.currentLocation.observe(this) { location ->
            updateMapWithCurrentLocation(location)
            workoutViewModel.entry.locationList.add(location)
        }

        mapDisplayViewModel.distance.observe(this){d->
            distanceTextView.text = "Distance: ${WorkoutFormatter.distance}"
            workoutViewModel.entry.distance = d
        }

        mapDisplayViewModel.curSpeed.observe(this){speed ->
            curSpeedTextView.text = "Current Speed: ${WorkoutFormatter.convertSpeedForDisplay(speed)}"
        }

        mapDisplayViewModel.avgSpeed.observe(this){s->
            avgSpeedTextView.text = "Average Speed: ${WorkoutFormatter.avgSpeed}"
            workoutViewModel.entry.avgSpeed = s
        }

        mapDisplayViewModel.calories.observe(this){cal ->
            calorieTextView.text = "Calories: ${WorkoutFormatter.calories}"
            workoutViewModel.entry.calorie = cal
        }
    }

    private fun showGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.md_fragment_container)
                as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.d(TAG, "getMapAsync called")
    }

    private fun startTrackingService() {

        val serviceIntent = Intent(this, TrackingService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, mapDisplayViewModel, BIND_AUTO_CREATE)
    }

    private fun updateMapWithCurrentLocation(location: LatLng) {

        if(workoutViewModel.entry.locationList.size == 1){
            startMarker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
            mapCentered = true
        } else {
                endMarker?.remove()

                endMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Current")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                updatePolyline()
            }

        if (!mapCentered) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
            mapCentered = true
            Log.d(TAG, "Map centered on first location")
        }
    }

    private fun handleButtonClicks() {
        val saveButton = findViewById<Button>(R.id.md_button_save)
        saveButton.setOnClickListener {
            val duration = (System.currentTimeMillis() - startTimeMillis)/60000.0
            workoutViewModel.entry.duration = duration
            workoutViewModel.insert()
            shouldShowToast = true
            stopTrackingAndFinish()
        }

        val cancelButton = findViewById<Button>(R.id.md_button_cancel)
        cancelButton.setOnClickListener {
            Log.d(TAG, "Cancel button clicked")
            stopTrackingAndFinish()
        }
    }

    private fun stopTrackingAndFinish() {

        if (isBound) {
            unbindService(mapDisplayViewModel)
            isBound = false
        }

        val serviceIntent = Intent(this, TrackingService::class.java)
        stopService(serviceIntent)

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(mapDisplayViewModel)
            isBound = false
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

    private fun loadAndObserveDatabase(){

        val db = WorkoutDatabase.getInstance(this)
        val dao = db.workoutDatabaseDao
        val repository = WorkoutRepository(dao)
        val factory = ViewModelFactory(repository)
        workoutViewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]
        workoutViewModel.entry.inputType = intent.getIntExtra("INPUT_TYPE", -1)
        workoutViewModel.entry.activityType = intent.getIntExtra("ACTIVITY_TYPE", -1)
        workoutViewModel.allWorkouts.observe(this){ workouts->
            if (shouldShowToast && workouts.isNotEmpty()){
                Toast.makeText(this, "Entry #${workouts.last().id} saved!", Toast.LENGTH_SHORT).show()
                shouldShowToast = false
            }
        }
    }
}