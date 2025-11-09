package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import com.google.android.gms.maps.model.LatLng

class MapDisplayViewModel : ViewModel(), ServiceConnection {

    private var trackingService: TrackingService? = null

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng>
        get() = _currentLocation

    private val _distance = MutableLiveData<Double>()
    val distance: LiveData<Double>
        get() = _distance

    val entry: ExerciseEntry = ExerciseEntry()

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {

        val binder = iBinder as TrackingService.MyBinder
        trackingService = binder.getService()

        binder.setLocationUpdateListener { location ->
            _currentLocation.postValue(location)
            entry.locationList.add(location)
        }

        binder.setDistanceUpdateListener { dist ->
            _distance.postValue(dist)
            entry.distance = dist
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        trackingService = null
    }

}
