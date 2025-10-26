package ca.sfu.cmpt362.ayusharora.myruns.database

import androidx.room.TypeConverter
import java.util.Calendar

// Calender <-> Long conversion to store in Room Database
// Learnt from https://developer.android.com/training/data-storage/room/referencing-data#kotlin
class Converters {

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
}