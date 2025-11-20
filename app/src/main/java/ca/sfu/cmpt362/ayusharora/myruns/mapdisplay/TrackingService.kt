package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import ca.sfu.cmpt362.ayusharora.myruns.weka.classifiers.WekaWrapper
import ca.sfu.cmpt362.ayusharora.myruns.weka.fft.FFT
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.*
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instances
import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.sqrt

class TrackingService : Service(), LocationListener, SensorEventListener {

    companion object {
        // For logging/debugging
        const val TAG = "TrackingService"

        // Input Types
        const val INPUT_TYPE_GPS = 1
        const val INPUT_TYPE_AUTOMATIC = 2

        // Message Types
        const val MSG_LOCATION_UPDATE = 0
        const val MSG_DISTANCE_UPDATE = 1
        const val MSG_CURRENT_SPEED_UPDATE = 2
        const val MSG_AVERAGE_SPEED_UPDATE = 3
        const val MSG_CALORIE_UPDATE = 4
        const val MSG_ACTIVITY_TYPE_UPDATE = 5

        // Fields that can be updated and sent via messages
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_ACTIVITY_TYPE = "activity_type"
        const val KEY_DISTANCE = "distance"
        const val KEY_CURRENT_SPEED = "current_speed"
        const val KEY_AVERAGE_SPEED = "average_speed"
        const val KEY_CALORIES = "calories"

        // Notification
        private const val NOTIFICATION_ID = 999
        private const val CHANNEL_ID = "tracking_channel"

        // Sensor Constants
        const val ACCELEROMETER_BLOCK_CAPACITY = 64
        const val ACCELEROMETER_BUFFER_CAPACITY = 2048
        const val FEATURE_VECTOR_SIZE = 65
    }

    // The interface of service exposed to activities/fragments
    private lateinit var trackingBinder: TrackingBinder
    // For sending messages: set by the class connected to this service
    private var msgHandler: Handler? = null

    // For getting location updates
    private lateinit var locationManager: LocationManager
    // For notifying service is running in background
    private lateinit var notificationManager: NotificationManager

    // For stats tracking
    private var currentLocation: LatLng? = null
    private var prevLocation: LatLng? = null
    private var inputType = INPUT_TYPE_GPS
    private var distance = 0.0
    private var startTime = 0L
    private var avgSpeed = 0.0
    private var calories = 0.0

    // Sensor management
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // ArrayBlockingQueue to store accelerometer readings
    private var mAccBuffer = ArrayBlockingQueue<Double>(ACCELEROMETER_BUFFER_CAPACITY)

    // Activity recognition counters for majority voting
    private val activityCounts = mutableMapOf(
        0 to 0, // Standing
        1 to 0, // Walking
        2 to 0, // Running
        3 to 0  // Other
    )
    private var totalClassifications = 0
    private var detectedActivityType = -1

    // Job for classification coroutine
    private var classificationJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        trackingBinder = TrackingBinder()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        inputType = intent?.getIntExtra(MapDisplayActivity.INPUT_TYPE, INPUT_TYPE_GPS) ?: INPUT_TYPE_GPS
        startLocationUpdates()
        if (inputType == INPUT_TYPE_AUTOMATIC) {
            startActivityRecognition()
        }
        showNotification()
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

    // Helper method
    // Stops receiving location updates and clears the service notification
    // Stops activity recognition in automatic mode
    private fun cleanupService (){
        locationManager.removeUpdates(this)
        notificationManager.cancel(NOTIFICATION_ID)
        if (inputType == INPUT_TYPE_AUTOMATIC) {
            stopActivityRecognition()
        }
    }

    // Helper method that shows notification in phone's notification bar
    // Code entirely taken from lecture demos (BindDemoKotlin)
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

    // This helper method checks if location permissions are granted
    // If yes, it requests for location updates from location manager
    // Upon getting location updates, it calls onLocationChanged() method
    private fun startLocationUpdates() {
        // Permission verification
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

        // Request for location updates
        // Code adapted from lecture demos (I_am_here_map_Kotlin)
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

            // The 2 sec check ensures location is very recent
            if (lastLocation != null && System.currentTimeMillis() - lastLocation.time < 2000) {
                onLocationChanged(lastLocation)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting last known location", e)
        }
    }

    // When new location is detected, this helper method computes the necessary stats like
    // distance, average speed, current speed, calories etc. and sends messages to the client.
    override fun onLocationChanged(location: Location) {

        currentLocation = LatLng(location.latitude, location.longitude)
        updateActivityStats()

        if (msgHandler != null) {
            sendLocationUpdate(currentLocation!!)
            sendCurrentSpeedUpdate(location.speed * 3.6) // location.speed is in m/s. * 3.6 converts it to km/hr
            sendDistanceUpdate(distance)
            sendAverageSpeedUpdate(avgSpeed)
            sendCalorieUpdate(calories)
        }
    }

    // Computes and updates tracking statistics (distance, speed, calories).
    //
    // GPS Stabilization: Stats calculation begins after a 2-second delay to mitigate GPS
    // initialization errors. Initial GPS readings can have positional inaccuracies of several
    // meters, which would produce artificially inflated speed values when divided by near-zero
    // elapsed time. The delay allows the GPS signal to stabilize.
    //
    // Calculation Methods:
    // - Distance: Cumulative sum of great-circle distances between consecutive location points
    //   using SphericalUtil.computeDistanceBetween()
    //   Reference: https://www.geeksforgeeks.org/android/how-to-calculate-distance-between-two-locations-in-android/
    // - Average Speed: (Total Distance / Total Time) × 3600 (converted to km/h)
    // - Calories: Distance (km) × 60 (simplified estimate; can be improved if required using more metrics like
    //   activity type, speed, user weight etc.)
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
            calories += deltaDist * 60
        }
        prevLocation = currentLocation
    }

    // Sends location updates to the bound service
    // Code adapted from lecture demos (BindDemoKotlin)
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

    // Sends distance updates to the bound service
    // Code adapted from lecture demos (BindDemoKotlin)
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

    // Sends current speed updates to the bound service
    // Code adapted from lecture demos (BindDemoKotlin)
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

    // Sends average updates to the bound service
    // Code adapted from lecture demos (BindDemoKotlin)
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

    // Sends calorie updates to the bound service
    // Code adapted from lecture demos (BindDemoKotlin)
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

    // Sends detected activity type updates to the bound service
    // Code adapted from lecture demos (BindDemoKotlin)
    private fun sendActivityTypeUpdate(activityType: Int) {
        msgHandler?.let { handler ->
            val bundle = Bundle().apply {
                putInt(KEY_ACTIVITY_TYPE, activityType)
            }
            val message = handler.obtainMessage().apply {
                what = MSG_ACTIVITY_TYPE_UPDATE
                data = bundle
            }
            handler.sendMessage(message)
        }
    }

    // Starts activity recognition using accelerometer
    // Code adapted from lecture demos (ShakeSensorKotlin)
    private fun startActivityRecognition() {

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            Log.e(TAG, "Accelerometer not available!")
            return
        }
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        // Start classification job in a separate thread
        classificationJob = CoroutineScope(Dispatchers.Default).launch {
            startClassificationLoop()
        }
    }

    // Stops activity recognition
    private fun stopActivityRecognition() {

        sensorManager.unregisterListener(this)
        classificationJob?.cancel()
        mAccBuffer.clear()
    }

    // Called when accelerometer data is received
    // Code adapted from lecture demos (ShakeSensorKotlin and MyRunsDataCollectorKotlin)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val m = sqrt(
                (event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2]).toDouble()
            )

            try {
                mAccBuffer.add(m)
            } catch (_: IllegalStateException) {
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

    // Classification loop that continuously processes accelerometer data
    // The code is derived from lecture demos (MyRunsDataCollector)
    // The AsyncTask logic from demo is replaced with a coroutine
    private suspend fun startClassificationLoop() = withContext(Dispatchers.Default) {

        var blockSize = 0
        val accBlock = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
        val im = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)

        while (isActive) {
            try {
                accBlock[blockSize++] = mAccBuffer.take()
                if (blockSize == ACCELEROMETER_BLOCK_CAPACITY) {
                    blockSize = 0

                    val featureVector = extractFeatures(accBlock, im)
                    val predictedActivity = classifyActivity(featureVector)
                    Log.d(TAG, "Classification complete: $predictedActivity")

                    activityCounts[predictedActivity] = (activityCounts[predictedActivity] ?: 0) + 1
                    totalClassifications++

                    val majorityActivity = activityCounts.maxByOrNull { it.value }?.key ?: 0
                    if (detectedActivityType == -1 || majorityActivity != detectedActivityType) {
                        Log.d(TAG, "Activity changed from $detectedActivityType to $majorityActivity (counts: $activityCounts)")
                        detectedActivityType = majorityActivity
                        withContext(Dispatchers.Main) {
                            onActivityDetected(detectedActivityType)
                        }
                    } else {
                        Log.d(TAG, "Majority activity unchanged: $majorityActivity (counts: $activityCounts)")
                    }
                }

            } catch (_: Exception){
                Log.e(TAG, "Error in classification loop")
            }
        }
    }

    // Extracts feature vector from 64 accelerometer readings using FFT
    // Code is the modified version from demo (MyRunsDataCollector)
    private fun extractFeatures(accBlock: DoubleArray, im: DoubleArray): DoubleArray {
        val features = DoubleArray(FEATURE_VECTOR_SIZE)
        var max = Double.MIN_VALUE
        for (value in accBlock) {
            if (max < value) {
                max = value
            }
        }

        val fft = FFT(ACCELEROMETER_BLOCK_CAPACITY)
        fft.fft(accBlock, im)
        for (i in 0 until ACCELEROMETER_BLOCK_CAPACITY) {
            val magnitude = sqrt(accBlock[i] * accBlock[i] + im[i] * im[i])
            features[i] = magnitude
            im[i] = 0.0
        }
        features[ACCELEROMETER_BLOCK_CAPACITY] = max

        return features
    }

    // Classifies activity using Weka classifier
    // Took AIs help to understand how to create a DenseInstance structure to call
    // WekaClassifier methods
    private fun classifyActivity(features: DoubleArray): Int {
        try {
            val attributes = ArrayList<Attribute>()
            for (i in 0 until FEATURE_VECTOR_SIZE) {
                attributes.add(Attribute("feature$i"))
            }

            val classValues = ArrayList<String>()
            classValues.add("0")
            classValues.add("1")
            classValues.add("2")
            classValues.add("3")
            attributes.add(Attribute("activity", classValues))

            val dataset = Instances("ActivityRecognition", attributes, 1)
            dataset.setClassIndex(dataset.numAttributes() - 1)

            val instance = DenseInstance(FEATURE_VECTOR_SIZE + 1)
            instance.setDataset(dataset)

            for (i in 0 until FEATURE_VECTOR_SIZE) {
                instance.setValue(i, features[i])
            }

            val wekaClassifier = WekaWrapper()
            val prediction = wekaClassifier.classifyInstance(instance)
            return prediction.toInt()

        } catch (e: Exception) {
            Log.e(TAG, "Classification failed", e)
            return 0
        }
    }

    // This method is called when activity is classified
    // It updates the detected activity type and notifies the activity
    private fun onActivityDetected(activityType: Int) {
        Log.d(TAG, "onActivityDetected called with type: $activityType")
        detectedActivityType = activityType

        if (msgHandler == null) {
            return
        }
        Log.d(TAG, "Sending activity type update: $activityType")
        sendActivityTypeUpdate(activityType)
    }

    // If the app is closed, this method kills the service running in background
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        cleanupService()
        stopSelf()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    // Binder class that provides the interface for clients to communicate with this service.
    // Allows the bound entity to set a Handler for receiving tracking updates.
    inner class TrackingBinder : Binder() {
        fun setMsgHandler(handler: Handler) {
            this@TrackingService.msgHandler = handler
        }
    }
}