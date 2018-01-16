package xyz.youngbin.fluxsync

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import java.util.*
import android.content.pm.ActivityInfo
import android.R.attr.orientation
import android.content.Context
import android.content.res.Configuration


/**
 * Created by youngbin on 2017. 7. 1..
 */
class Util{
    companion object {
        fun reqPermission(activity: Activity, permission: String, requestCode: Int, reason: String){
            // Here, thisActivity is the current activity
            Log.d("permission","checking if granted")
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("permission","not granted")
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    Log.d("permission","showing reason")
                    Toast.makeText(activity, reason, Toast.LENGTH_LONG).show()
                } else {
                    // No explanation needed, we can request the permission.
                    Log.d("permission","requesting permission - ${permission}")
                    ActivityCompat.requestPermissions(activity, Array(1){permission}, requestCode);

                }
            }
        }

        fun lockScreenOrientation(activity: Activity){
            val currentOrientation = activity.resources.configuration.orientation
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }

        val connectionStatusFilter = "xyz.youngbin.fluxsync.CONNECTION_STATUS"

        val serviceType = "_fluxsync._tcp"

    }
    enum class ConnectionStatus(val code: Int){
        PREPARING(0),
        SCANNING(1),
        CONNECTING(2),
        AUTHENTICATING(3),
        CONNECTED(4),
        DISCONNECTED(5),
        FAILED(6),
        UNAUTHORIZED(7)
    }
}