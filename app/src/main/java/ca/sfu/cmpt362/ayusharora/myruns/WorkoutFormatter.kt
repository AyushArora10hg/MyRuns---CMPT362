package ca.sfu.cmpt362.ayusharora.myruns

import android.content.Context
import ca.sfu.cmpt362.ayusharora.myruns.database.ExerciseEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object WorkoutFormatter {

    private lateinit var inputTypeArray: Array<String>
    private lateinit var activityTypeArray: Array<String>
    private lateinit var entry: ExerciseEntry
    var shouldConvert = false

    fun initialize(context: Context, entry: ExerciseEntry){
        inputTypeArray = context.resources.getStringArray(R.array.input_type)
        activityTypeArray = context.resources.getStringArray(R.array.activity_type)

        this.entry = entry
    }

    val inputType: String
        get() = inputTypeArray[entry.inputType]

    val activityType: String
        get() = activityTypeArray[entry.activityType]

    val dateTime: String
        get() = this.formatDateTime(entry.dateTime)

    val duration: String
        get() = this.formatDuration(entry.duration)

    val distance: String
        get() = "$distanceValue $distanceUnit"

    val avgSpeed: String
        get() = "$speedVal $speedUnit"

    val calories: String
        get() = "${this.roundToTwoPlaces(entry.calorie)} kcal"

    val climb: String
        get() = "$climbVal $distanceUnit"

    val heartRate: String
        get() = "${this.roundToTwoPlaces(entry.heartRate)} bpm"

    val comment: String
        get() = entry.comment


    private val distanceValue: Double
        get() = if (shouldConvert) {
            this.convertKilometersToMiles(entry.distance)
        } else {
            entry.distance
        }

    private val speedVal: Double
        get() = if(shouldConvert){
            this.convertKilometersToMiles(entry.avgSpeed)
        } else {
            entry.avgSpeed
        }

    private val climbVal: Double
        get() = if (shouldConvert){
            this.convertKilometersToMiles(entry.climb)
        } else {
            entry.climb
        }

    // Returns unit strings based on shouldConvert flag
    val distanceUnit: String
        get() = if (shouldConvert) "mi" else "km"

    val speedUnit: String
        get() = "$distanceUnit/h"


    fun convertToImperial(){
        shouldConvert = true
    }

    fun convertToMetric(){
        shouldConvert = false
    }

    fun convertDistanceForStorage (distance: Double): Double{
        return if (shouldConvert) this.convertMilesToKilometers(distance) else distance
    }

    // Convert date and time from Calendar object to readable string
    // eg. 09:10:55 Oct 23 2025
    private fun formatDateTime(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // convert duration from double to readable form
    // 30.5 -> 30 min 30 sec
    private fun formatDuration(duration: Double) : String {

        val min = floor(duration).toInt()
        val sec = ((duration - min) * 60).toInt()

        return "$min min $sec sec"
    }

    private fun roundToTwoPlaces(value: Double): String{

        return  "%.2f".format(value)
    }

    // miles -> kilometers
    fun convertMilesToKilometers (mileValue: Double ) :Double {

        return "%.3f".format(mileValue * 1.60934).toDouble()
    }

    // kilometers -> miles
    fun convertKilometersToMiles (kilometerValue: Double ) :Double {

        return "%.3f".format(kilometerValue / 1.60934).toDouble()
    }

}