package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    companion object {
        const val TAG = "MapDisplayActivity"
    }

    private lateinit var mapDisplayViewModel: MapDisplayViewModel

    private lateinit var mMap: GoogleMap

    private var isBound = false

    private var mapCentered = false

    private lateinit var  markerOptions: MarkerOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        observeLocationChanges()
        showGoogleMap()
        handleButtonClicks()
        startTrackingService()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        Log.d(TAG, "Map is ready")

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        markerOptions = MarkerOptions()
    }

    private fun observeLocationChanges(){

        mapDisplayViewModel = ViewModelProvider(this)[MapDisplayViewModel::class.java]
        mapDisplayViewModel.currentLocation.observe(this) { location ->
            Log.d(TAG, "New location received from ViewModel: $location")
            updateMapWithCurrentLocation(location)
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

        markerOptions.position(location)
        mMap.addMarker(markerOptions)

        if (!mapCentered) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
            mapCentered = true
            Log.d(TAG, "Map centered on first location")
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        markerOptions.position(latLng)
        mMap.addMarker(markerOptions)
        Log.d(TAG, "Manual marker added at: $latLng")
    }

    private fun handleButtonClicks() {
        val saveButton = findViewById<Button>(R.id.md_button_save)
        saveButton.setOnClickListener {
            Log.d(TAG, "Save button clicked")
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
}