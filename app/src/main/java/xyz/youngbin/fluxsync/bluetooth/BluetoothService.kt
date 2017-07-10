package xyz.youngbin.fluxsync.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import xyz.youngbin.fluxsync.Util
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
    lateinit var mLocalBM: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mLocalBM = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        var command = intent!!.getStringExtra("command")
        when(command){
            "connect" -> {
                broadcastStatus(0)
                mAddress = intent!!.getStringExtra("address")
                if(!connected){
                    ConnectTask().execute()
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
        mSocket.close()
        ConnectTask().cancel(true)
        ReadTask().cancel(true)
        WriteTask().cancel(true)
        broadcastStatus(4)
    }

    inner class ConnectTask : AsyncTask<Unit, Unit, BluetoothSocket>(){
        override fun doInBackground(vararg params: Unit?): BluetoothSocket {
            broadcastStatus(1)
            val mServerSocket = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord("Serial", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            while (true){
                var socket = mServerSocket.accept()
                if(socket != null && (socket.remoteDevice.address == mAddress)){
                    socket.close()
                    broadcastStatus(2)
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
            broadcastStatus(3)
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
                // Read and send data via LocalBroadcast
                bytes = mInput.read(buffer)
                var sendIntent = Intent(Util.connectionStepFilter)
                sendIntent.putExtra("data", bytes)
                mLocalBM.sendBroadcast(sendIntent)
            }
        }


    }

    inner class WriteTask : AsyncTask<ByteArray, Unit, Unit>(){
        override fun doInBackground(vararg params: ByteArray?): Unit{
            try{
                // Send data to desktop app
                mOutput.write(params[0])
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

    fun broadcastStatus(statusCode: Int){
        val intent = Intent(Util.connectionStepFilter)
        intent.putExtra("status",Util.connectionStatusCodes[statusCode])
        mLocalBM.sendBroadcast(intent)
    }


}
