package ca.sfu.cmpt362.ayusharora.myruns

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object Util {

    // Ask for user permission
    // Code copied from XD's lecture demos
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions(activity: Activity?) {

        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_EXTERNAL_STORAGE),
                0)
        }
    }

    //Format bitmap
    // Code copied from XD's lecture demos
    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    // Convert date and time from Calendar object to readable string
    // eg. 09:10:55 October 23 2025
    fun formatDateTime(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("h:mm:ss MMM dd yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // miles -> kilometers
    fun convertMilesToKilometers (distanceInMiles: Double ) :Double {

        return "%.3f".format(distanceInMiles * 1.6094).toDouble()
    }

    // kilometers -> miles
    fun convertKilometersToMiles (distanceInKilometers: Double ) :Double {

        return "%.3f".format(distanceInKilometers / 1.6094).toDouble()
    }

    // convert duration from double to readable form
    // 30.5 -> 30 min 30 sec
    fun formatDuration(duration: Double) : String {

        val min = floor(duration).toInt()
        val sec = ((duration - min) * 60).toInt()

        return "$min min $sec sec"
    }

    fun roundToTwoPlaces(value: Double): String{

        return  "%.2f".format(value)
    }
}