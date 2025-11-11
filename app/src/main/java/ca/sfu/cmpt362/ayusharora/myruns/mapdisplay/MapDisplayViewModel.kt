package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapDisplayViewModel : ViewModel(), ServiceConnection {

    private var myMessageHandler: MyMessageHandler = MyMessageHandler(Looper.getMainLooper())

    var startTimeMillis: Long = 0L

    fun initializeStartTime() {
        if (startTimeMillis == 0L) {
            startTimeMillis = System.currentTimeMillis()
        }
    }

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng>
        get() = _currentLocation

    private val _distance = MutableLiveData<Double>()
    val distance: LiveData<Double>
        get() = _distance

    private val _curSpeed = MutableLiveData<Double>()
    val curSpeed: LiveData<Double>
        get() = _curSpeed

    private val _avgSpeed = MutableLiveData<Double>()
    val avgSpeed: LiveData<Double>
        get() = _avgSpeed

    private val _calories = MutableLiveData<Double>()
    val calories: LiveData<Double>
        get() = _calories

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {

        val tempBinder = iBinder as TrackingService.TrackingBinder
        tempBinder.setMsgHandler(myMessageHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        myMessageHandler.removeCallbacksAndMessages(null)
    }

    inner class MyMessageHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data

            when (msg.what) {
                TrackingService.MSG_LOCATION_UPDATE -> {
                    val latitude = bundle.getDouble(TrackingService.KEY_LATITUDE)
                    val longitude = bundle.getDouble(TrackingService.KEY_LONGITUDE)
                    _currentLocation.value = LatLng(latitude, longitude)
                }

                TrackingService.MSG_DISTANCE_UPDATE -> {
                    val distance = bundle.getDouble(TrackingService.KEY_DISTANCE)
                    _distance.value = distance
                }

                TrackingService.MSG_CURRENT_SPEED_UPDATE -> {
                    val speed = bundle.getDouble(TrackingService.KEY_CURRENT_SPEED)
                    _curSpeed.value = speed
                }

                TrackingService.MSG_AVERAGE_SPEED_UPDATE -> {
                    val speed = bundle.getDouble(TrackingService.KEY_AVERAGE_SPEED)
                    _avgSpeed.value = speed
                }

                TrackingService.MSG_CALORIE_UPDATE -> {
                    val calories = bundle.getDouble(TrackingService.KEY_CALORIES)
                    _calories.value = calories
                }
            }
        }
    }
}