package xyz.youngbin.fluxsync.bluetooth

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

class ConnectActivity : AppCompatActivity() {
    lateinit var mLocalBM : LocalBroadcastManager
    lateinit var deviceName : String
    val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
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
        val address = intent.getStringExtra("address")
        deviceName = intent.getStringExtra("name")
        status.text = getString(R.string.bluetooth_preparing)

        var connectNewIntent = Intent(this, BluetoothService::class.java)
        connectNewIntent.putExtra("command", "connect")
        connectNewIntent.putExtra("address", address)
        startService(connectNewIntent)

        val filter = IntentFilter(Util.connectionStepFilter)
        mLocalBM = LocalBroadcastManager.getInstance(this)
        mLocalBM.registerReceiver(receiver, filter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }
}
