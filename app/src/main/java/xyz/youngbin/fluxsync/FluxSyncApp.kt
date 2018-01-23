package xyz.youngbin.fluxsync

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.Preference
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.util.*

/**
 * Created by youngbin on 2017. 7. 22..
 */
class FluxSyncApp : Application() {

    lateinit var deviceId : String
    lateinit var hostname : String
    lateinit var mPref : SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // Init SharedPreference
        mPref = PreferenceManager.getDefaultSharedPreferences(this)
        deviceId = mPref.getString("deviceId","none")
        if(deviceId == "none"){
            deviceId = UUID.randomUUID().toString()
            mPref.edit().putString("deviceId", deviceId).apply()
        }

        hostname = mPref.getString("hostname","none")
        if(hostname == "none"){
            hostname = "${Build.MODEL} ${Build.ID}"
            mPref.edit().putString("hostname", hostname).apply()
        }

        // TODO - broadcast a implicit intent locally to start automatic connection
        val mLocalBM = LocalBroadcastManager.getInstance(this)
        mLocalBM.sendBroadcast(Intent(Util.appStartFilter))
    }
}