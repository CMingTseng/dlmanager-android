package org.gmetais.downloadmanager.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.gmetais.downloadmanager.*
import org.gmetais.downloadmanager.ui.fragments.Browser
import org.gmetais.downloadmanager.ui.fragments.Preferences
import org.gmetais.downloadmanager.ui.fragments.SharesBrowser

class MainActivity : AppCompatActivity(), LifecycleRegistryOwner, NetworkHelper.NetworkController {

    var mAlertDialog : AlertDialog? = null
    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState === null)
            addFragment(R.id.fragment_placeholder, SharesBrowser(), "shares")
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onStart() {
        super.onStart()
        NetworkHelper.attach(this)
    }

    override fun onStop() {
        super.onStop()
        mAlertDialog?.dismiss()
    }

    override fun onBackPressed() {
        if ("shares" == supportFragmentManager.findFragmentById(R.id.fragment_placeholder)?.tag) {
            finish()
            return
        }
        super.onBackPressed()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            navigation.selectedItemId -> false
            R.id.navigation_shares -> {
                replaceFragment(R.id.fragment_placeholder, SharesBrowser(), "shares")
                true
            }
            R.id.navigation_browse -> {
                with(supportFragmentManager) {
                    if (popBackStackImmediate("root", android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE))
                        removeFragment("shares")
                    else
                        replaceFragment(R.id.fragment_placeholder, Browser(), "browser")
                }
                true
            }
            R.id.navigation_settings -> {
                replaceFragment(R.id.fragment_placeholder, Preferences(), "settings")
                true
            }
            else -> false
        }
    }

    override fun onConnectionChanged(disconnected: Boolean) = showNetworkDialog(disconnected)
}
