package xyz.youngbin.fluxsync.connect

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
            desc.text = getString(R.string.qr_found)
            var qrData: JsonElement
            try {
            val app = applicationContext as FluxSyncApp
            qrData = JsonParser().parse(rawResult!!.text)
            // Save token
            app.mPref.edit().putString("jwt", qrData.asJsonObject.get("jwt").asString).apply()
            app.mPref.edit().putString("key", qrData.asJsonObject.get("key").asString).apply()
            setResult(Activity.RESULT_OK)
            finish()
        }catch (e: JsonParseException){
            desc.text = getString(R.string.qr_invalid)
        }catch (e: Exception){
            desc.text = getString(R.string.qr_invalid)
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
