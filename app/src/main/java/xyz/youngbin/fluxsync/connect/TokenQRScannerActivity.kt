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

    lateinit var mScannerView : ZXingScannerView
    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        desc.text = getString(R.string.qr_found)
        var qrData: JsonElement
        try {
            val app = applicationContext as FluxSyncApp
            qrData = JsonParser().parse(rawResult!!.text)
            // Save jwt token and key for encryption
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


        setContentView(R.layout.activity_token_qrscanner)

        mScannerView = ZXingScannerView(this)
        frame.addView(mScannerView)
        mScannerView.setAutoFocus(true)

        flash.setOnClickListener {
            mScannerView.flash = !mScannerView.flash
        }

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
