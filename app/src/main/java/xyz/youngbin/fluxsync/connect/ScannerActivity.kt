package xyz.youngbin.fluxsync.connect


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

import xyz.youngbin.fluxsync.R
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.util.Log
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scanner.*
import android.net.nsd.NsdServiceInfo
import xyz.youngbin.fluxsync.Util


class ScannerActivity : AppCompatActivity() {
    lateinit var mNsdManager: NsdManager
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var mAdapter: DeviceListAdapter
    lateinit var mDatas: ArrayList<DeviceInfo>
    var isScanning: Boolean = false

    // Nstwork service discovery listener for scanning devices
    val mListener = object : NsdManager.DiscoveryListener {

        //  Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            isScanning = true
            Log.d("discovery","started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            Log.d("found", service.toString())
            if(service.serviceName == Util.desktopAdvertisement){
                mNsdManager.resolveService(service, mResolver)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            Log.d("lost", service.toString())
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
        }

        override fun onDiscoveryStopped(serviceType: String) {
            isScanning = false
            runOnUiThread {
                status.text = getString(R.string.device_status_scanned)
            }
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            isScanning = false
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            isScanning = false
            mNsdManager.stopServiceDiscovery(this)
        }
    };

    // Listener that resolves network service
    val mResolver = object : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            // Just do nothing
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            // Add Item to list
            val item = serviceInfo?.attributes
            mDatas.add(DeviceInfo(item!!.get("hostname")!!.toString(charset("utf-8")),
                    item.get("deviceid")!!.toString(charset("utf-8")),
                    "${serviceInfo.host}:${serviceInfo.port}"))
            runOnUiThread {
                mAdapter.notifyDataSetChanged()
            }


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_scanner)

        // Setup RecyclerView
        mDatas = ArrayList<DeviceInfo>()
        mLayoutManager = LinearLayoutManager(this)
        list.layoutManager = mLayoutManager

        // Set Adapter on RecyclerView
        mAdapter = DeviceListAdapter(mDatas, this, View.OnClickListener {
            // On device selected
            v: View ->
            val position = list.getChildAdapterPosition(v)
            Log.d("Device",mDatas[position].name)
            var connectIntent = Intent(this, ConnectActivity::class.java)
            connectIntent.putExtra("address", mDatas[position].address)
            connectIntent.putExtra("name", mDatas[position].name)
            startActivity(connectIntent)
            finish()
        })
        list.adapter = mAdapter

        cancel.setOnClickListener{
            finish()
        }


        refresh.setOnClickListener{
            if(!isScanning){
                scanDevices()
            }
        }

        //Check if wifi is on
        val mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        if(!mWifiManager.isWifiEnabled){
            Toast.makeText(this, getString(R.string.wifi_info_on), Toast.LENGTH_LONG).show()
            mWifiManager.setWifiEnabled(true)
        }
        mNsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
        scanDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mNsdManager.stopServiceDiscovery(mListener)
        }catch (e: IllegalArgumentException ){
            e.printStackTrace()
        }
    }

    // Scans devices
    fun scanDevices(){
        Log.d("Scan","Scanning...")
        if(!isScanning){
            isScanning = true
            try {
                mNsdManager.stopServiceDiscovery(mListener)
            }catch (e: Exception){
                e.printStackTrace()
            }
            // Start network discovery
            mNsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, mListener)
            status.text = getString(R.string.device_status_scanning)
        }
    }
    // Data Class for device list view
    data class DeviceInfo(var name: String, var deviceId: String, var address: String)

    // Adapter for Scanned Device list
    class DeviceListAdapter
    (dataSet: ArrayList<DeviceInfo>, context: Context, listener: View.OnClickListener)
        : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {
        var mDataSet: ArrayList<DeviceInfo>
        var mContext: Context
        var mListener: View.OnClickListener

        init {
            mDataSet = dataSet
            mContext = context
            mListener = listener
        }

        // Returns count of data
        override fun getItemCount(): Int {
            return mDataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val item: DeviceInfo = mDataSet.get(position)

            holder?.txtTitle?.text = item.name
            holder?.txtInfo?.text = item.deviceId
            holder?.item?.setOnClickListener(mListener)

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            var item: View
            var txtTitle: TextView
            var txtInfo: TextView
            init {
                item = v
                txtTitle = v.findViewById(R.id.name) as TextView
                txtInfo = v.findViewById(R.id.info) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
            var v: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false)
            var holder: ViewHolder = ViewHolder(v)
            return holder
        }

    }


}

