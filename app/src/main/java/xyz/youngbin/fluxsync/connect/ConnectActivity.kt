package xyz.youngbin.fluxsync.connect

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
import xyz.youngbin.fluxsync.FluxSyncApp
import java.util.*


class ConnectActivity : AppCompatActivity() {
    lateinit var mLocalBM : LocalBroadcastManager
    lateinit var deviceName : String
    lateinit var deviceAddress : String
    var connectStatus : String = "preparing"
    val REQUEST_BLUETOOTH_DISCOVERABLE = 10
    val DISCOVERABLE_DURATION = 300
    val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            connectStatus = intent!!.getStringExtra("status")
            when(intent!!.getStringExtra("status")){
                "preparing" -> status.text = getString(R.string.bluetooth_preparing)
                "waiting" -> status.text = getString(R.string.bluetooth_waiting).format(deviceName)
                "connecting" -> status.text = getString(R.string.bluetooth_connecting).format(deviceName)
                "connected" -> status.text = getString(R.string.bluetooth_connected).format(deviceName)
                "disconnected" -> status.text = getString(R.string.bluetooth_disconnected)
                "failed" -> status.text = getString(R.string.bluetooth_failed)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        var app: FluxSyncApp = applicationContext as FluxSyncApp

        deviceAddress = intent.getStringExtra("address")
        deviceName = intent.getStringExtra("name")
        status.text = getString(R.string.bluetooth_preparing)
        desc.text = getString(R.string.activity_connect_desc).format(app.hostname)
        cancel.setOnClickListener {
            status.text = getString(R.string.bluetooth_canceling)
            finish()
        }

        AuthClient(deviceAddress).sendInfo(this, {
            result: Boolean ->
            Toast.makeText(this,"DONE!",Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                startActivityForResult(Intent(this, TokenQRScannerActivity::class.java), 0)
            },3000)
        })


    }



    override fun onDestroy() {
        super.onDestroy()
        if(connectStatus != "connected"){
            stopService(Intent(this, BluetoothService::class.java))
        }
    }


}
