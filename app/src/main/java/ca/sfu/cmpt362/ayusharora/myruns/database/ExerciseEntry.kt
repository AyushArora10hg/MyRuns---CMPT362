package ca.sfu.cmpt362.ayusharora.myruns.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar


// Code adapted from XD's demo lectures

@Entity(tableName = "workout_data")
data class ExerciseEntry (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,                          // Unique ID to each entry

    @ColumnInfo(name = "input_type")
    var inputType: Int,                         // Manual, GPS or automatic

    @ColumnInfo(name = "activity_type")
    var activityType: Int,                      // Running, cycling etc.

    @ColumnInfo(name = "date_time")
    var dateTime: Calendar,                     // Date and Time ("9:45:12 Oct 18 2025")

    @ColumnInfo(name = "duration")
    var duration: Double,                       // Exercise duration in seconds

    @ColumnInfo(name = "distance")
    var distance: Double,                       // Distance traveled in kms or miles

    @ColumnInfo(name = "average_pace")
    var avgPace: Double,                        // Average pace

    @ColumnInfo(name = "average_speed")
    var avgSpeed: Double,                       // Average speed

    @ColumnInfo(name = "calories")
    var calorie: Double,                        // Calories burnt

    @ColumnInfo(name = "climb")
    var climb: Double,                          // Climb. Either in meters or feet.

    @ColumnInfo(name = "heart_rate")
    var heartRate: Double,                      // Heart rate in bpm

    @ColumnInfo(name = "comment")
    var comment: String                         // Comments

)