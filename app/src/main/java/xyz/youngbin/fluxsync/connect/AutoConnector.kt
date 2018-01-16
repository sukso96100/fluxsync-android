package xyz.youngbin.fluxsync.connect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.druk.dnssd.*
import xyz.youngbin.fluxsync.Util
import java.net.InetAddress

class AutoConnector : BroadcastReceiver() {
    lateinit var mDnsSdBrowser: DNSSD
    lateinit var mBrowseService: DNSSDService

    override fun onReceive(context: Context, intent: Intent) {
        // Start Connection Service
        if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
            mBrowseService = mDnsSdBrowser.browse(Util.serviceType,  object : BrowseListener {
                override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun serviceFound(browser: DNSSDService?, flags: Int, ifIndex: Int, serviceName: String?,
                                          regType: String?, domain: String?) {
                    Log.d("found","start resolving")
                    // Resolve the found service
                    resolveService(context, flags, ifIndex, serviceName, regType, domain)
                }

                override fun serviceLost(browser: DNSSDService?, flags: Int, ifIndex: Int, serviceName: String?,
                                         regType: String?, domain: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    // Listener that resolves network service
    fun resolveService(context: Context, flags: Int, ifIndex: Int, serviceName: String?, regType: String?, domain: String?){
        mDnsSdBrowser.resolve(flags, ifIndex, serviceName, regType, domain, object : ResolveListener {
            override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun serviceResolved(resolver: DNSSDService?, flags: Int, ifIndex: Int, fullName: String?,
                                         hostName: String?, port: Int, txtRecord: MutableMap<String, String>?) {
                Log.d("resolved","${fullName} / ${port} / ${txtRecord.toString()}")
                // Query resolved service for ip address
                queryService(context, flags, ifIndex, serviceName, hostName, port, txtRecord)
            }

        })
    }


    fun queryService(context: Context, flags: Int, ifIndex: Int, serviceName: String?, hostName: String?,
                     port: Int, txtRecord: MutableMap<String, String>?){
        mDnsSdBrowser.queryRecord(flags, ifIndex, hostName,1,1, object : QueryListener {
            override fun queryAnswered(query: DNSSDService?, flags: Int, ifIndex: Int, fullName: String?,
                                       rrtype: Int, rrclass: Int, rdata: InetAddress?, ttl: Int) {

                Log.d("query","${fullName} / ${rdata as InetAddress}:${port} / ${txtRecord.toString()}")
                val ioIntent = Intent(context, ConnectionService::class.java)
                ioIntent.putExtra("command", ConnectionService.Commands.CONNECT.cmd)
                ioIntent.putExtra("address", "${rdata}:${port}")
                context.startService(ioIntent)

            }

            override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}
