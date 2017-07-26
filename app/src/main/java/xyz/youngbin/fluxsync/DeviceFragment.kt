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
import xyz.youngbin.fluxsync.connect.ScannerActivity


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
    lateinit var app: FluxSyncApp
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
        app = context!!.applicationContext as FluxSyncApp
        mLocalBM = LocalBroadcastManager.getInstance(activity)
        val layout = inflater!!.inflate(R.layout.fragment_device, container, false)
        val mButton = layout.findViewById(R.id.button)
        mButton.setOnClickListener {
            startActivity(Intent(activity, ScannerActivity::class.java))
        }
        remoteName.text = app.mPref.getString("remoteName", getString(R.string.no_device))
        return layout
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
