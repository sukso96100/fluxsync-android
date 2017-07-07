package xyz.youngbin.fluxsync.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.Buffer
import java.util.*

class BluetoothService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var mBluetoothAdapter: BluetoothAdapter
    lateinit var mAddress: String
    var connected: Boolean = false
    lateinit var mSocket: BluetoothSocket
    lateinit var mInput: InputStream
    lateinit var mOutput: OutputStream

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

    override fun onDestroy() {
        super.onDestroy()
        mSocket.close()
        ConnectTask().cancel(true)
        ReadTask().cancel(true)
        WriteTask().cancel(true)

    }

    inner class ConnectTask : AsyncTask<Unit, Unit, BluetoothSocket>(){
        override fun doInBackground(vararg params: Unit?): BluetoothSocket {
            val mServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("Serial", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
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

        override fun onPostExecute(result: BluetoothSocket) {
            super.onPostExecute(result)

            mSocket = result
            try {
                mInput = mSocket.inputStream
                mOutput = mSocket.outputStream
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        override fun onCancelled() {
            super.onCancelled()
        }

    }

    inner class ReadTask : AsyncTask<Unit, Unit, Unit>(){
        override fun doInBackground(vararg params: Unit?) {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true){
                bytes = mInput.read(buffer)
            }
        }


    }

    inner class WriteTask : AsyncTask<BluetoothSocket, Unit, Unit>(){
        override fun doInBackground(vararg params: BluetoothSocket?): Unit{
            params[0]!!.inputStream
            params[0]!!.outputStream

        }
    }


}
