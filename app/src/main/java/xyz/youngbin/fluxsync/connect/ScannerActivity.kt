package xyz.youngbin.fluxsync.connect


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

import xyz.youngbin.fluxsync.R
import android.net.wifi.WifiManager
import android.util.Log
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scanner.*
import com.github.druk.dnssd.*
import java.net.InetAddress


class ScannerActivity : AppCompatActivity() {

    lateinit var mDnsSdBrowser: DNSSD
    lateinit var mBrowseService: DNSSDService
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var mAdapter: DeviceListAdapter
    lateinit var mDatas: ArrayList<DeviceInfo>
    var isScanning: Boolean = false

    val mBrowser = object : BrowseListener{
        override fun operationFailed(service: DNSSDService?, errorCode: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun serviceFound(browser: DNSSDService?, flags: Int, ifIndex: Int, serviceName: String?, regType: String?, domain: String?) {
            Log.d("found","start resolving")
            resolveService(flags, ifIndex, serviceName, regType, domain)
        }

        override fun serviceLost(browser: DNSSDService?, flags: Int, ifIndex: Int, serviceName: String?, regType: String?, domain: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }


    // Listener that resolves network service
    fun resolveService(flags: Int, ifIndex: Int, serviceName: String?, regType: String?, domain: String?){
        mDnsSdBrowser.resolve(flags, ifIndex, serviceName, regType, domain, object : ResolveListener{
            override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun serviceResolved(resolver: DNSSDService?, flags: Int, ifIndex: Int, fullName: String?,
                                         hostName: String?, port: Int, txtRecord: MutableMap<String, String>?) {
                Log.d("resolved","${fullName} / ${port} / ${txtRecord.toString()}")
                queryService(flags, ifIndex, serviceName, hostName, port, txtRecord)
            }

        })
    }


    fun queryService(flags: Int, ifIndex: Int, serviceName: String?, hostName: String?,
                     port: Int, txtRecord: MutableMap<String, String>?){
        mDnsSdBrowser.queryRecord(flags, ifIndex, hostName,1,1, object : QueryListener {
            override fun queryAnswered(query: DNSSDService?, flags: Int, ifIndex: Int, fullName: String?,
                                       rrtype: Int, rrclass: Int, rdata: InetAddress?, ttl: Int) {
                Log.d("query","${fullName} / ${rdata as InetAddress}:${port} / ${txtRecord.toString()}")
                runOnUiThread {
                    mDatas.add(DeviceInfo(hostName, txtRecord!!["deviceid"], "${rdata as InetAddress}:${port}"))
                    mAdapter.notifyDataSetChanged()}
            }

            override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
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
            connectIntent.putExtra("id", mDatas[position].remoteId) //mdatas 로 하고 한 것은 정확히 알아야 될 값을 알아야 하기 때문이다.
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
        //

        connect_manual.setOnClickListener {
            var tokenIntent =  Intent(this, TokenQRScannerActivity::class.java) // 엑티비티 부분이라 this 하면된다.
            tokenIntent.putExtra("isManual", true) // 뒤에 값 부분은 딱히 나타낼 필요없이 true 정도로만 나누면 된다.
            startActivity(tokenIntent) //액티비티 시작

        }//이

        //Check if wifi is on
        val mWifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if(!mWifiManager.isWifiEnabled){
            Toast.makeText(this, getString(R.string.wifi_info_on), Toast.LENGTH_LONG).show()
            mWifiManager.setWifiEnabled(true)
        }
        scanDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBrowseService.stop()
    }

    // Scans devices
    fun scanDevices(){
        isScanning = true
        Log.d("Scan","Scanning...")
        mDnsSdBrowser = DNSSDEmbedded()
        mBrowseService = mDnsSdBrowser.browse("_http._tcp", mBrowser)
    }
    // Data Class for device list view
    data class DeviceInfo(var name: String?, var remoteId: String?, var address: String?)

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
            holder?.txtInfo?.text = item.remoteId
            holder?.item?.setOnClickListener(mListener)

        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            var item: View
            var txtTitle: TextView
            var txtInfo: TextView
            init {
                item = v
                txtTitle = v.findViewById<TextView>(R.id.name)
                txtInfo = v.findViewById<TextView>(R.id.info)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
            val v: View = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false)
            val holder: ViewHolder = ViewHolder(v)
            return holder
        }

    }


}

