package xyz.youngbin.fluxsync.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_connection.*
import xyz.youngbin.fluxsync.R

class ConnectActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        Toast.makeText(this, intent.getStringExtra("address"), Toast.LENGTH_LONG).show()

        status.text = getString(R.string.bluetooth_connecting).format(intent.getStringExtra("name"))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }
}
