package org.gmetais.downloadmanager

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.gmetais.downloadmanager.databinding.BrowserBinding

class Browser(val path : String? = null) : Fragment(), BrowserAdapter.IHandler {

    private lateinit var mBinding: BrowserBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = BrowserBinding.inflate(inflater ?: LayoutInflater.from(activity))
        mBinding.filesList.layoutManager = LinearLayoutManager(mBinding.root.context)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        RequestManager.browse(path, this::update, this::onServiceFailure)
    }

    private fun update(directory: Directory) {
        activity.title = directory.path.getNameFromPath()
        mBinding.filesList.adapter = BrowserAdapter(this, directory.files.sortedBy { !it.isDirectory })
    }

    private fun onServiceFailure(msg: String) {
        Snackbar.make(mBinding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun open(path : String) {
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder, Browser(path), path.getNameFromPath())
                .addToBackStack(this.path?.getNameFromPath() ?: "root")
                .commit()
    }
}
