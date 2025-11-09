package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class TrackingService : Service(), LocationListener {

    companion object {
        const val TAG = "TrackingService"
    }

    private lateinit var myBinder: MyBinder

    private lateinit var locationManager: LocationManager

    private var currentLocation: LatLng? = null

    private var prevLocation : LatLng? = null

    private var distance = 0.0

    override fun onCreate() {
        super.onCreate()
        myBinder = MyBinder()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permission not granted")
            return
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    this
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting NETWORK_PROVIDER updates", e)
            }
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    this
                )
                Log.d(TAG, "Requesting GPS provider updates")
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting GPS_PROVIDER updates", e)
            }
        }

        try {
            val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            val lastLocation = gpsLoc?:netLoc

            if (lastLocation != null) {
                onLocationChanged(lastLocation)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting last known location", e)
        }
    }


    override fun onLocationChanged(location: Location) {
        currentLocation = LatLng(location.latitude, location.longitude)
        myBinder.notifyLocationUpdate(currentLocation!!)
        updateDistance()
    }

    // ====================================================


    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Service onUnbind() called")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy() called")
        locationManager.removeUpdates(this)
    }

    // Adapted from: https://www.geeksforgeeks.org/android/how-to-calculate-distance-between-two-locations-in-android/
    private fun updateDistance(){

        if (prevLocation != null){
            distance += SphericalUtil.computeDistanceBetween(prevLocation, currentLocation)/1000.0
            myBinder.notifyDistanceUpdate(distance)
        }
        prevLocation = currentLocation
    }

    inner class MyBinder : Binder() {
        private var locationUpdateListener: ((LatLng) -> Unit)? = null
        private var distanceUpdateListener: ((Double) -> Unit)? = null

        fun getService(): TrackingService {
            return this@TrackingService
        }

        fun setLocationUpdateListener(listener: (LatLng) -> Unit) {
            locationUpdateListener = listener
        }

        fun setDistanceUpdateListener(listener: (Double) -> Unit) {
            distanceUpdateListener = listener
        }

        fun notifyLocationUpdate(location: LatLng) {
            locationUpdateListener?.invoke(location)
        }

        fun notifyDistanceUpdate(distance : Double){
            distanceUpdateListener?.invoke(distance)
        }
    }
}