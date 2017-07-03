package xyz.youngbin.fluxsync.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

import xyz.youngbin.fluxsync.R
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.activity_scanner.*


class ScannerActivity : AppCompatActivity() {
    val REQUEST_ENABLE_BT = 1
    lateinit var mBluetoothAdapter: BluetoothAdapter
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var mAdapter: BluetoothDeviceListAdapter
    lateinit var mDatas: ArrayList<BluetoothDeviceInfo>
    var isScanning: Boolean = false

    // Receiver for scanning devices
    val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                Log.d("Scan","Adding New Device Item")
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the name and address to an array adapter to show in a ListView
                mDatas.add(BluetoothDeviceInfo(device.name, device.address, false))
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_scanner)

        // Setup RecyclerView
        mDatas = ArrayList<BluetoothDeviceInfo>()
        mLayoutManager = LinearLayoutManager(this)
        list.layoutManager = mLayoutManager

        mAdapter = BluetoothDeviceListAdapter(mDatas, this, View.OnClickListener {
            v: View ->
            val position = list.getChildAdapterPosition(v)
            var connectIntent = Intent(this, ConnectActivity::class.java)
            connectIntent.putExtra("address", mDatas[position].address)
            startActivity(connectIntent)
        })
        list.adapter = mAdapter

        //Check if bluetooth is on
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(mBluetoothAdapter != null){
            if (!mBluetoothAdapter.isEnabled) {
                // Show bluetooth request dialog
                Log.d("Scan","Requesting bluetooth")

                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }else{
                Log.d("Scan","Registering Receiver")
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(mReceiver, filter)
                scanDevices()
            }
        }



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            Log.d("Scan","Registering Receiver")
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(mReceiver, filter)
            scanDevices()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mReceiver)
        }catch (e: IllegalArgumentException ){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.scanner, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.refresh ->
                if(!isScanning){
                    scanDevices()
                }
        }
        return super.onOptionsItemSelected(item)
    }

    // Scans devices
    fun scanDevices(){
        Log.d("Scan","Scanning...")
        if(!isScanning){
            isScanning = true
            status.text = getString(R.string.bluetooth_status_scanning)
            mDatas.clear()
            mAdapter.notifyDataSetChanged()
            // Show paired devices first
            val pairedDevices = mBluetoothAdapter.bondedDevices
            // If there are paired devices
            if (pairedDevices.size > 0) {
                // Loop through paired devices
                for (device in pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    Log.d("Scan","Adding Paired Device Item")
                    mDatas.add(BluetoothDeviceInfo(device.name, device.address, true))
                }
                mAdapter.notifyDataSetChanged()

            }
            // then show not paired devices
            mBluetoothAdapter.startDiscovery()
            status.text = getString(R.string.bluetooth_status_scanned)
            isScanning = false
        }
    }
    // Data Class for device list view
    data class BluetoothDeviceInfo(var name: String, var address: String, var isPaired: Boolean)

    // Adapter for Scanned Device list
    class BluetoothDeviceListAdapter
    (dataSet: ArrayList<BluetoothDeviceInfo>, context: Context, listener: View.OnClickListener)
        : RecyclerView.Adapter<BluetoothDeviceListAdapter.ViewHolder>() {
        var mDataSet: ArrayList<BluetoothDeviceInfo>
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
            val item: BluetoothDeviceInfo = mDataSet.get(position)
            var status: String
            if(item.isPaired){
                status = mContext.getString(R.string.bluetooth_paired)
            }else{
                status = mContext.getString(R.string.bluetooth_not_paired)
            }
            holder?.txtTitle?.text = item.name
            holder?.txtInfo?.text = "${status} | ${item.address}"
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

