package xyz.youngbin.fluxsync

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast

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

        val connectionStepFilter = "xyz.youngbin.fluxsync.CONNECTION_STEP"
        val connectionStatusCodes = arrayOf("preparing","waiting","connecting","connected","disconnected","failed")
    }
}