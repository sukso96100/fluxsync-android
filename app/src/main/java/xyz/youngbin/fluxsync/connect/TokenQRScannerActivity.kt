package xyz.youngbin.fluxsync.connect

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_token_qrscanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

import xyz.youngbin.fluxsync.R
import xyz.youngbin.fluxsync.Util
import java.util.jar.Manifest

class TokenQRScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    lateinit var mScannerView : ZXingScannerView
    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        desc.text = getString(R.string.qr_found)
        Log.v("Scanned!", rawResult!!.getText()) // Prints scan results
        Log.v("Scanned!", rawResult.getBarcodeFormat().toString()) // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
