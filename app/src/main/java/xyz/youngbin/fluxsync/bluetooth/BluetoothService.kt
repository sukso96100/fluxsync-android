package xyz.youngbin.fluxsync.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import java.util.*

class BluetoothService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mBluetoothAdapter: BluetoothAdapter
    lateinit var mAddress: String
    var connected: Boolean = false

    override fun onCreate() {
        super.onCreate()

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        mAddress = intent!!.getStringExtra("address")
        if(!connected){
            ConnectTask().execute()
        }


        return START_STICKY
    }

    inner class ConnectTask : AsyncTask<Void, Void, BluetoothSocket>(){
        override fun doInBackground(vararg params: Void?): BluetoothSocket {
            val mServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("Serial", UUID.fromString(""))
            while (true){
                var socket = mServerSocket.accept()
                if(socket != null && (socket.remoteDevice.address == mAddress)){
                    socket.close()
                    return socket
                }else{
                    socket.close()
                }
            }

        }

        override fun onPostExecute(result: BluetoothSocket?) {
            super.onPostExecute(result)

            CommunicationTask().execute(result)
        }

    }

    inner class CommunicationTask : AsyncTask<BluetoothSocket, Void, Void>(){
        override fun doInBackground(vararg params: BluetoothSocket?): Void {

        }
    }


}
