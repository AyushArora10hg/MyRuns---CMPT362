package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import ca.sfu.cmpt362.ayusharora.myruns.R
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class TrackingService : Service(), LocationListener {

    companion object {
        const val TAG = "TrackingService"

        // Message Protocol
        const val MSG_LOCATION_UPDATE = 0
        const val MSG_DISTANCE_UPDATE = 1
        const val MSG_CURRENT_SPEED_UPDATE = 2
        const val MSG_AVERAGE_SPEED_UPDATE = 3
        const val MSG_CALORIE_UPDATE = 4

        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_DISTANCE = "distance"
        const val KEY_CURRENT_SPEED = "current_speed"
        const val KEY_AVERAGE_SPEED = "average_speed"
        const val KEY_CALORIES = "calories"

        // Notification
        private const val NOTIFICATION_ID = 999
        private const val CHANNEL_ID = "tracking_channel"
    }

    private lateinit var trackingBinder: TrackingBinder
    private var msgHandler: Handler? = null

    private lateinit var locationManager: LocationManager
    private lateinit var notificationManager: NotificationManager

    private var currentLocation: LatLng? = null
    private var prevLocation: LatLng? = null
    private var distance = 0.0
    private var startTime = 0L
    private var avgSpeed = 0.0
    private var calories = 0.0

    override fun onCreate() {
        super.onCreate()
        trackingBinder = TrackingBinder()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return trackingBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        msgHandler = null
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanupService()
    }

    private fun cleanupService (){
        locationManager.removeUpdates(this)
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun showNotification() {
        val intent = Intent(this, MapDisplayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_tracking_service)
        notificationBuilder.setContentTitle("Tracking Workout")
        notificationBuilder.setContentText("Your activity is being tracked")
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setAutoCancel(false)
        notificationBuilder.setOngoing(true)

        val notification = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Workout Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
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

    inner class TrackingBinder : Binder() {
        fun setMsgHandler(handler: Handler) {
            this@TrackingService.msgHandler = handler
        }
    }
}