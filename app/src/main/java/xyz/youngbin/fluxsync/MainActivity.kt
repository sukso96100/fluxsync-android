package xyz.youngbin.fluxsync

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: MainPagerAdapter
    private lateinit var mPager: ViewPager
    val REQUEST_BT_PERMISSION = 2

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                mPager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                mPager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                mPager.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = MainPagerAdapter(supportFragmentManager)
        mPager = findViewById<ViewPager>(R.id.pager)
        mPager.adapter = mAdapter

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Check permission
//        Util.reqPermission(this, Manifest.permission.BLUETOOTH_ADMIN, REQUEST_BT_PERMISSION, getString(R.string.permission_bluetooth))
    }




        inner class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

            override fun getCount(): Int {
                return 3
            }

            override fun getItem(position: Int): Fragment {
                when(position){
                    0 ->{
                        return DeviceFragment.newInstance()
                    }
                    1 ->{
                        return DeviceFragment.newInstance()
                    }
                    2 ->{
                        return DeviceFragment.newInstance()
                    }
                }
                return DeviceFragment.newInstance()
            }

        }



}
