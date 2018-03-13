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
        super.onStartCommand(intent, flags, startId) // 이쪽에서 인텐트 부분은 되어 있고
        Log.d("service","onstartcommand")

        val command = intent!!.getStringExtra("command")
        when(command){
            "connect" -> {
                broadcastStatus(2)
                mAddress = intent.getStringExtra("address")
                if(!connected){
                    if(!mAddress.contains("/")) { mAddress = "/${mAddress}"}
                    mSocket = IO.socket("http:/${mAddress}")
                    mSocket.connect()
                    mSocket.on("connect", {
                        broadcastStatus(3)
                        // Authenticate with jwt token
                        Log.d("status","Authenticating")
                        val app = applicationContext as FluxSyncApp
                        mSocket.emit("authenticate", JSONObject().put("token", app.mPref.getString("jwt","token")))
                               .on("authenticated", {
                                    // connected
                                   Log.d("socket","Connected")
                                   broadcastStatus(4)
                                   mSocket.emit("test","TEST EMIT")
                               })
                               .on("unauthorized", {
                                   // Unauthorized! cancel connection
                                   Log.d("status","unauthorized")
                                   broadcastStatus(7)
                               })
                    })
                }
            }
            "disconnect" -> {
                mSocket.disconnect()
                mSocket.off()
                stopSelf()
            }
            "send" -> { //커맨드가 send 이고 이 커맨드를 입력했을때
                Log.d("ConnectionService","Sending data")
                val eventName = intent.getStringExtra("eventName") // 두가지 변수를 만들어 주고 들어갈 것을 지정 해준다. 커맨드를 eventName 으로 하고
                val content = intent.getStringExtra("content")      //
                mSocket.emit(eventName, content)// 그냥 소켓이름에 들어갈 것 두가지
//                var TokenIntent = Intent(this, TokenQRScannerActivity::class.java)
//                var Send_intent = intent(createPackageContext(this), )
//                Send_intent.putExtra("address",mDatas[position].address)
//

//            TokenIntent.putExtra("address", mDatas[position].address)
//            startActivity(TokenIntent)
                // Send Data using outputstream

                //서비스 스타트를 해서 인텐트(데이터, 커맨드 처럼 )를 실어보낸다. disconnect 와 같음
                //추가로 데스크탑에 전송할 메세지를 같이 보낸다.
                //보낼 떄 emit 함수 사용  앞에가 이벤트 채널 뒤에가 실제 내용
                //
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
        val intent = Intent(Util.connectionStatusFilter)
        intent.putExtra("status",Util.connectionStatusCodes[statusCode])
        mLocalBM.sendBroadcast(intent)
    }




}
