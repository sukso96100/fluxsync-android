package xyz.youngbin.fluxsync.connect

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

import xyz.youngbin.fluxsync.R

class TokenQRScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    lateinit var mScannerView : ZXingScannerView
    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        Log.v("Scanned!", rawResult!!.getText()); // Prints scan results
        Log.v("Scanned!", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera();
    }
}
