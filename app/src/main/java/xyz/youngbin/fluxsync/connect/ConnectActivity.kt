package xyz.youngbin.fluxsync.connect

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_connection.*
import xyz.youngbin.fluxsync.R
import xyz.youngbin.fluxsync.Util
import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import android.net.nsd.NsdServiceInfo
import android.net.nsd.NsdManager
import android.os.Handler
import android.util.Log
import xyz.youngbin.fluxsync.FluxSyncApp
import java.util.*


class ConnectActivity : AppCompatActivity() {
    lateinit var mLocalBM : LocalBroadcastManager
    lateinit var remoteName : String
    lateinit var deviceAddress : String
    lateinit var remoteId : String
    var connectStatus : String = "preparing"
    val REQUEST_SCAN_QR = 10
    lateinit var app: FluxSyncApp
    val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            connectStatus = intent!!.getStringExtra("status")
            when(intent.getStringExtra("status")){
                "connecting" -> status.text = getString(R.string.connection_connecting).format(remoteName)
                "authenticating" -> status.text = getString(R.string.connection_authenticating)
                "connected" -> {
                    status.text = getString(R.string.connection_connected).format(remoteName)
                    app.mPref.edit().putString("remoteName", remoteName).apply()
                    app.mPref.edit().putString("remoteId", remoteId).apply()
                    finish()
                }
                "disconnected" -> status.text = getString(R.string.connection_disconnected)
                "failed" -> status.text = getString(R.string.connection_failed)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        Util.lockScreenOrientation(this)

        app = applicationContext as FluxSyncApp
        mLocalBM = LocalBroadcastManager.getInstance(this)
        deviceAddress = intent.getStringExtra("address")
        remoteName = intent.getStringExtra("name")
        remoteId = intent.getStringExtra("id")
        Log.d("Device",remoteName)
        status.text = getString(R.string.connection_preparing)
        desc.text = getString(R.string.activity_connect_desc).format(app.hostname)
        cancel.setOnClickListener {
            status.text = getString(R.string.connection_canceling)
            finish()
        }

        AuthClient(deviceAddress).sendInfo(this, {
            result: Boolean ->
            Handler().postDelayed({
                status.text = getString(R.string.connection_scanning)
                startActivityForResult(Intent(this, TokenQRScannerActivity::class.java), REQUEST_SCAN_QR)
            },3000)
        })


    }



    override fun onDestroy() {
        super.onDestroy()
        if(connectStatus != "connected"){

            stopService(Intent(this, ConnectionService::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_SCAN_QR && resultCode== Activity.RESULT_OK){
            Log.d("connect","connecting")
            status.text = getString(R.string.connection_connecting).format(remoteName)
            mLocalBM.registerReceiver(receiver, IntentFilter(Util.connectionStatusFilter))
            val ioIntent = Intent(this, ConnectionService::class.java)
            ioIntent.putExtra("command", "connect")
            ioIntent.putExtra("address", deviceAddress)
            startService(ioIntent)
        }
    }
}
