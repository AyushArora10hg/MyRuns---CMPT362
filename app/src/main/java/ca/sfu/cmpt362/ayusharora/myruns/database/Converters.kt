package ca.sfu.cmpt362.ayusharora.myruns.database

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Calendar

class Converters {

    // Calender <-> Long conversion to store in Room Database
    // Learnt from https://developer.android.com/training/data-storage/room/referencing-data#kotlin
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply {
                timeInMillis = it
            }
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }


    // ArrayList<LatLng> <-> ByteArray conversion to store in Room Database
    // Uses an ObjectStream mediator to serialize/deserialize doubles from/to raw bytes automatically
    // as byteArray can only write or read as raw bytes.

    // The objectOutputStream automatically converts the double-type location data into raw bytes
    @TypeConverter
    fun fromLatLngList(locationList: ArrayList<LatLng>): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)

        for (latLng in locationList) {
            objectOutputStream.writeDouble(latLng.latitude)
            objectOutputStream.writeDouble(latLng.longitude)
        }

        objectOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }

    // The objectInputStream automatically convert raw bytes from byteArrayInputStream into doubles
    @TypeConverter
    fun toLatLngList(byteArray: ByteArray): ArrayList<LatLng> {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)

        val locationList = ArrayList<LatLng>()

        try {
            while (true) {
                val latitude = objectInputStream.readDouble()
                val longitude = objectInputStream.readDouble()
                locationList.add(LatLng(latitude, longitude))
            }
        } catch (_: Exception) {

        }

        objectInputStream.close()
        return locationList
    }
}