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
    companion object { //자바 스태틱 여기다가 셋팅해놓으면 초기화 안하고 쓸 수 있다.
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
        val notificationActionFilter = "xyz.youngbin.fluxsync.NOTIFICATION_ACTION"
        val connectionStatusCodes = arrayOf(
                "preparing",
                "scanning",
                "connecting",
                "authenticating",
                "connected",
                "disconnected",
                "failed",
                "unauthorized")
        val desktopAdvertisement = "FluxSyncDesktopApp"
        val sendDataFilter = "xyz.youngbin.fluxsync.SEND_DATA"
    }
}