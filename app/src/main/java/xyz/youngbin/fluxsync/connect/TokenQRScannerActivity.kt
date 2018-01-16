package xyz.youngbin.fluxsync.connect

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_token_qrscanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.json.JSONObject
import xyz.youngbin.fluxsync.FluxSyncApp

import xyz.youngbin.fluxsync.R
import xyz.youngbin.fluxsync.Util
import java.util.jar.Manifest

class TokenQRScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {


    // 처리받아서 인증하는 부분 여기다가 다시 인증받는 것을 추가로하고 예외처리를 하면 된다.
    lateinit var mScannerView : ZXingScannerView
    var isManual: Boolean = false

    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        //조건문으로 true 일때 qr 코드에 따라 작동되게하고 false 일때 밑에 부분으로 되게한다.
        val app = applicationContext as FluxSyncApp
        if (isManual == false) {
            desc.text = getString(R.string.qr_found)
            var qrData: JsonElement
            try {
                qrData = JsonParser().parse(rawResult!!.text)
                // Save token
                app.mPref.edit().putString("jwt", qrData.asJsonObject.get("jwt").asString).apply()
                app.mPref.edit().putString("key", qrData.asJsonObject.get("key").asString).apply()
                setResult(Activity.RESULT_OK)
                finish()
            } catch (e: JsonParseException) {
                desc.text = getString(R.string.qr_invalid)
            } catch (e: Exception) {
                desc.text = getString(R.string.qr_invalid)
            }
        }else{
            Log.d("d",rawResult!!.text) // 값을 텍스트로 한다는 의미
            Toast.makeText(this, rawResult!!.text, Toast.LENGTH_LONG).show()//화면에 메시 뜨게 하는것 //주소를 만들어서 intent로 connectacttivity 로 보내면 된다 .
//            finish() //이부분은 인텐트 아래 부분으로 다 되면 qr코드가 꺼지게 만든다.
        }


            // If you would like to resume scanning, call this method below:
            mScannerView.resumeCameraPreview(this)



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.lockScreenOrientation(this)
        // Request Camera Permission
        Util.reqPermission(this, android.Manifest.permission.CAMERA, 1, getString(R.string.permission_camera))

//intent 보고 여기서 처리한다. 안드로이드에서 액티비티 실행할 때 이걸로한다. 인텐트에 풋엑스트라르 추가해서 이벤트를보낸다다

        setContentView(R.layout.activity_token_qrscanner)

        isManual = intent.getBooleanExtra("isManual",false) //받는 부분으로 보낸 걸 받는다.

        mScannerView = ZXingScannerView(this)
        frame.addView(mScannerView)
        mScannerView.setAutoFocus(true)

        flash.setOnClickListener {
            mScannerView.flash = !mScannerView.flash
        }
//        val action = intent.getIntExtra("action",0)


    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()         // Start camera on resume
        desc.text = getString(R.string.activity_qr_desc)
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }


}
