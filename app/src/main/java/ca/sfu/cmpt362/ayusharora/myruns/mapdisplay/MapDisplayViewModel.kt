package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapDisplayViewModel : ViewModel(), ServiceConnection {

    private var trackingService: TrackingService? = null

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

        val binder = iBinder as TrackingService.MyBinder
        trackingService = binder.getService()

        binder.setLocationUpdateListener { location ->
            _currentLocation.postValue(location)
        }

        binder.setDistanceUpdateListener { dist ->
            _distance.postValue(dist)
        }

        binder.setCurrentSpeedListener { curSpeed ->
            _curSpeed.postValue(curSpeed)
        }

        binder.setAverageSpeedListener { avgSpeed ->
            _avgSpeed.postValue(avgSpeed)
        }

        binder.setCalorieListener { cal ->
            _calories.postValue(cal)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        trackingService = null
    }

}
