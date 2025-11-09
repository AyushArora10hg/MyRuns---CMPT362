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
    val entry: ExerciseEntry = ExerciseEntry()

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng>
        get() = _currentLocation

    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {

        val binder = iBinder as TrackingService.MyBinder
        trackingService = binder.getService()

        binder.setLocationUpdateListener { location ->

            entry.locationList.add(location)
            _currentLocation.postValue(location)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        trackingService = null
    }

}
