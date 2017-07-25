package xyz.youngbin.fluxsync.connect

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import xyz.youngbin.fluxsync.Util
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectionService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mSocket: Socket
    lateinit var mAddress: String
    var connected: Boolean = false]

    override fun onCreate() {
        super.onCreate()
        Log.d("ConnectionService","Creating Service...")

//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        mLocalBM = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        var command = intent!!.getStringExtra("command")
        when(command){
            "connect" -> {
                broadcastStatus(0)
                mAddress = intent!!.getStringExtra("address")
                if(!connected){
                    mSocket = IO.socket(mAddress)
                    mSocket.connect()
                }
            }
            "disconnect" -> {
                stopSelf()
            }
            "send" -> {
                // Send Data using outputstream
            }
        }



        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            mSocket.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
        broadcastStatus(4)
    }





    fun broadcastStatus(statusCode: Int){
        val intent = Intent(Util.connectionStepFilter)
        intent.putExtra("status",Util.connectionStatusCodes[statusCode])
        mLocalBM.sendBroadcast(intent)
    }




}
