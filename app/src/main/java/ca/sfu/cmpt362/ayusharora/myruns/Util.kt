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
    fun checkPermissions(activity: Activity?) {
        activity ?: return

        val permissions = mutableListOf<String>()

        // Camera permission (available on all versions)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        // Storage permissions - different for different Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Android 12 and below use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Request all needed permissions at once
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), 0)
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

    // miles -> kilometers
    fun convertMilesToKilometers (distanceInMiles: Double ) :Double {

        return "%.3f".format(distanceInMiles * 1.60934).toDouble()
    }
}