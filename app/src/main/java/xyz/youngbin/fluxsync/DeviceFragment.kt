package xyz.youngbin.fluxsync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_device.*
import xyz.youngbin.fluxsync.connect.ConnectionService
import xyz.youngbin.fluxsync.connect.ScannerActivity
import xyz.youngbin.fluxsync.connect.TokenQRScannerActivity


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DeviceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DeviceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeviceFragment : Fragment() {

    // TODO: Rename and change types of parameters
//    private var mParam1: String? = null
//    private var mParam2: String? = null
    lateinit var app: FluxSyncApp // 여기에서 선언을 한번 해주고 // 저걸 하면 shared preferences가 있다.
    lateinit var mLocalBM: LocalBroadcastManager

    val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent!!.getStringExtra("status")){
                "connected" -> {
                    status.text = getString(R.string.device_connected)
                    remoteName.text = app.mPref.getString("remoteName", getString(R.string.no_device))
                }
                "disconnected" -> status.text = getString(R.string.connection_disconnected)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (arguments != null) {
//            mParam1 = arguments.getString(ARG_PARAM1)
//            mParam2 = arguments.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val layout = inflater!!.inflate(R.layout.fragment_device, container, false)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = context!!.applicationContext as FluxSyncApp  //초기화를 해주는 부분 FluxSynapp 이전역에 쓴다는 뜻으로 여기서 쓰인다.
        remoteName.text = app.mPref.getString("remoteName",getString(R.string.no_device)) //

        val remoteId = app.mPref.getString("remoteId", null) //정보가 들어왔는지 가져오는 부분
        val remoteAddr = app.mPref.getString("remoteAddr", null)
        val remoteName_Value = app.mPref.getString("remoteName", null)

        if(remoteId != null && remoteAddr != null && remoteName_Value != null){
            button.text = "Disconnect"
            button.setOnClickListener {
                app.mPref.edit().remove("remoteId").apply()
                app.mPref.edit().remove("remoteAddr").apply()
                app.mPref.edit().remove("remoteName").apply() //이부분이 clear shared preference 해서 나오는 부분 이고 이런식으로 값을 날려야 한다.


                val disconnectIntent = Intent(activity, ConnectionService::class.java)  // 인텐트를 보내기 위해서 인텐트를 하나 만들어줘야 한다.  클래스 자바는 자바 클래스라는 느ㄸㅅ이다
                disconnectIntent.putExtra("command", "disconnect") //이부분에서 dissconnectitent 부분에 putExtra로 값을 넣는 다는 뜻이고 command를 해서 disconnect 값을 넣는다.
                activity.startService(disconnectIntent) //그리고 인텐트이므로 activity로 실행하고 disconnetintent 를 한다.


            }//여기서 정보 3개를 삭제하고 connection Service 에서 disconnect 부분으로 인텐트해서 보내게 하면 된다.
        }else{
            button.setOnClickListener { startActivity(Intent(activity, ScannerActivity::class.java)) }
        }

        mLocalBM = LocalBroadcastManager.getInstance(activity)

//        val intent: Intent = Intent(activity, TokenQRScannerActivity::class.java)
//        intent.putExtra("action",0);
//        startActivity(intent)
        remoteName.text = app.mPref.getString("remoteName", getString(R.string.no_device)) // remoteName 은 키값 이고 뒤에꺼는 키값없을떄 어떻게 할지 한다.

        button2.setOnClickListener{
            val sendIntent = Intent(activity, ConnectionService::class.java) // 이부분에서 인텐트를 만들고 보낸다.
            sendIntent.putExtra("command", "send")//커맨드로 send 할 때 아래부분을 같이 보낸다는 뜻으로 쓴다.
            sendIntent.putExtra("eventName", "notify")  //connection Service 부분에 있는 커넥 부분에 있는걸 받아서 보낸다 .
            sendIntent.putExtra("content", "test" )
            activity.startService(sendIntent)
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }



    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment DeviceFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): DeviceFragment {
            val fragment = DeviceFragment()
            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
