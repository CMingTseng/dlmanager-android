package org.gmetais.downloadmanager.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import org.gmetais.downloadmanager.*
import org.gmetais.downloadmanager.model.DirectoryModel

class Browser : BaseBrowser(), BrowserAdapter.IHandler {

    val mCurrentDirectory: DirectoryModel by lazy { ViewModelProviders.of(this, DirectoryModel.Factory(arguments?.getString("path"))).get(DirectoryModel::class.java) }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = arguments?.getString("path")?.getNameFromPath() ?: "root"
        mCurrentDirectory.directory.observe(this, Observer { update(it!!) })
        showProgress()
    }

    private fun update(directory: Directory) {
        showProgress(false)
        mBinding.filesList.adapter = BrowserAdapter(this, directory.files.sortedBy { !it.isDirectory })
    }

    private fun onServiceFailure(msg: String) {
        Snackbar.make(mBinding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun open(file: File) {
        if (file.isDirectory) {
            val browser = Browser()
            val bundle = Bundle(1)
            bundle.putString("path", file.path)
            browser.arguments = bundle
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder, browser, file.path.getNameFromPath())
                    .addToBackStack(activity.title.toString())
                    .commit()
        } else {
            val linkCreatorDialog = LinkCreatorDialog()
            val args = Bundle(1)
            args.putString("path", file.path)
            linkCreatorDialog.arguments = args
            linkCreatorDialog.show(activity.supportFragmentManager, "linking park")
        }
    }
}
