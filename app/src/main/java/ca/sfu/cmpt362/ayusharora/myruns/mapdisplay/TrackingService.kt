package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class TrackingService : Service(), LocationListener {

    companion object {
        const val TAG = "TrackingService"

        // Message types
        const val MSG_LOCATION_UPDATE = 0
        const val MSG_DISTANCE_UPDATE = 1
        const val MSG_CURRENT_SPEED_UPDATE = 2
        const val MSG_AVERAGE_SPEED_UPDATE = 3
        const val MSG_CALORIE_UPDATE = 4

        // Bundle keys
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_DISTANCE = "distance"
        const val KEY_CURRENT_SPEED = "current_speed"
        const val KEY_AVERAGE_SPEED = "average_speed"
        const val KEY_CALORIES = "calories"
    }

    private lateinit var myBinder: MyBinder
    private var msgHandler: Handler? = null

    private lateinit var locationManager: LocationManager

    private var currentLocation: LatLng? = null
    private var prevLocation: LatLng? = null
    private var distance = 0.0
    private var startTime = 0L
    private var avgSpeed = 0.0
    private var calories = 0.0

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

    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
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
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (lastLocation != null && System.currentTimeMillis() - lastLocation.time < 2000) {
                onLocationChanged(lastLocation)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting last known location", e)
        }
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = LatLng(location.latitude, location.longitude)
        updateActivityStats()

        if (msgHandler != null) {
            sendLocationUpdate(currentLocation!!)
            sendCurrentSpeedUpdate(location.speed * 3.6)
            sendDistanceUpdate(distance)
            sendAverageSpeedUpdate(avgSpeed)
            sendCalorieUpdate(calories)
        }
    }

    private fun sendLocationUpdate(location: LatLng) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putDouble(KEY_LATITUDE, location.latitude)
                putDouble(KEY_LONGITUDE, location.longitude)
            }
            val message = handler.obtainMessage().apply {
                what = MSG_LOCATION_UPDATE
                data = bundle
            }
            handler.sendMessage(message)
        }
    }

    private fun sendDistanceUpdate(distance: Double) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putDouble(KEY_DISTANCE, distance)
            }
            val message = handler.obtainMessage().apply {
                what = MSG_DISTANCE_UPDATE
                data = bundle
            }
            handler.sendMessage(message)
        }
    }

    private fun sendCurrentSpeedUpdate(speed: Double) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putDouble(KEY_CURRENT_SPEED, speed)
            }
            val message = handler.obtainMessage().apply {
                what = MSG_CURRENT_SPEED_UPDATE
                data = bundle
            }
            handler.sendMessage(message)
        }
    }

    private fun sendAverageSpeedUpdate(speed: Double) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putDouble(KEY_AVERAGE_SPEED, speed)
            }
            val message = handler.obtainMessage().apply {
                what = MSG_AVERAGE_SPEED_UPDATE
                data = bundle
            }
            handler.sendMessage(message)
        }
    }

    private fun sendCalorieUpdate(calories: Double) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putDouble(KEY_CALORIES, calories)
            }
            val message = handler.obtainMessage().apply {
                what = MSG_CALORIE_UPDATE
                data = bundle
            }
            handler.sendMessage(message)
        }
    }

    private fun updateActivityStats() {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }
        val totalTimeSec = (System.currentTimeMillis() - startTime) / 1000.0

        if (totalTimeSec < 2) return

        if (prevLocation != null) {
            val deltaDist = SphericalUtil.computeDistanceBetween(prevLocation, currentLocation) / 1000.0
            distance += deltaDist
            avgSpeed = (distance / totalTimeSec) * 3600.0
            calories += deltaDist * 50
        }
        prevLocation = currentLocation
    }

    inner class MyBinder : Binder() {
        fun setMsgHandler(handler: Handler) {
            this@TrackingService.msgHandler = handler
        }

        fun getService(): TrackingService {
            return this@TrackingService
        }
    }
}