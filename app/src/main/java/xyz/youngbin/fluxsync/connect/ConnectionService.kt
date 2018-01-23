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
import org.json.JSONObject
import xyz.youngbin.fluxsync.FluxSyncApp
import xyz.youngbin.fluxsync.Util
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ConnectionService : Service() {

        enum class Commands(val cmd: String){
            CONNECT("connect"),
            DISCONNECT("disconnect"),
            SEND("send")
        }


    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mSocket: Socket
    lateinit var mAddress: String
    lateinit var mLocalBM: LocalBroadcastManager
    var connected: Boolean = false


    override fun onCreate() {
        super.onCreate()
        Log.d("ConnectionService","Creating Service...")
        mLocalBM = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("service","onstartcommand")

        val command = intent!!.getStringExtra("command")
        when(command){
            Commands.CONNECT.cmd-> {
                // Connect with desktop
                broadcastStatus(Util.ConnectionStatus.CONNECTING)
                mAddress = intent.getStringExtra("address") // resolve address from intent
                if(!connected){
                    mSocket = IO.socket("http:/${mAddress}")
                    mSocket.connect()
                    mSocket.on("connect", {
                        broadcastStatus(Util.ConnectionStatus.AUTHENTICATING)
                        // Authenticate with jwt token
                        Log.d("status","Authenticating")
                        val app = applicationContext as FluxSyncApp
                        // load token from preference
                        mSocket.emit("authenticate",
                                JSONObject().put("token", app.mPref.getString("jwt","token")))
                               .on("authenticated", {
                                    // connected
                                   Log.d("socket","Connected")
                                   broadcastStatus(Util.ConnectionStatus.CONNECTED)
                                   mSocket.emit("test","TEST EMIT")
                               })
                               .on("unauthorized", {
                                   // Unauthorized! cancel connection
                                   Log.d("status","unauthorized")
                                   broadcastStatus(Util.ConnectionStatus.UNAUTHORIZED)
                               })
                    })
                }
            }
            Commands.DISCONNECT.cmd -> {
                // Disconnect from desktop
                mSocket.disconnect()
                mSocket.off()
                stopSelf()
            }
            Commands.SEND.cmd -> {
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
        broadcastStatus(Util.ConnectionStatus.DISCONNECTED)
    }





    fun broadcastStatus(status: Util.ConnectionStatus){
        val intent = Intent(Util.connectionStatusFilter)
        intent.putExtra("status", status.code)
        mLocalBM.sendBroadcast(intent)
    }


}
