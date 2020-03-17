package com.scottyab.rootbeer.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.scottyab.rootbeer.sample.CheckForRootWorker.Companion.progressMax
import com.scottyab.rootbeer.sample.extensions.hide
import com.scottyab.rootbeer.sample.extensions.show
import com.scottyab.rootbeer.sample.ui.RootItemAdapter
import com.scottyab.rootbeer.sample.ui.ScopedActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ScopedActivity() {

    private var infoDialog: AlertDialog? = null
    private val rootItemAdapter =
        RootItemAdapter()
    private val checkForRoot = CheckForRootWorker(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        resetProgressView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        fab.setOnClickListener { checkForRoot() }
        rootResultsRecycler.layoutManager = LinearLayoutManager(this)
        rootResultsRecycler.adapter = rootItemAdapter
    }

    private fun resetProgressView() {
        rootBeerProgressView.max = progressMax
        rootBeerProgressView.beerProgress = 0
        rootBeerProgressView.show()
    }

    private fun checkForRoot() {
        fab.hide()
        isRootedTextView.hide()
        rootItemAdapter.clear()
        resetProgressView()

        launch {
            val isRooted = checkForRoot.invoke { rootItemResult, progress ->
                rootItemAdapter.add(rootItemResult)
                rootBeerProgressView.beerProgress = progress
                Timber.d("progress= $progress")
            }
            onRootCheckFinished(isRooted)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_github -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(GITHUB_LINK)
                startActivity(i)
                true
            }
            R.id.action_info -> {
                showInfoDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        //do nothing if already showing
        if (infoDialog?.isShowing != true) {
            infoDialog = AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.info_details)
                .setCancelable(true)
                .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("More info") { dialog, _ ->
                    dialog.dismiss()
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(GITHUB_LINK)
                        )
                    )
                }
                .create()
            infoDialog?.show()
        }
    }

    private fun onRootCheckFinished(isRooted: Boolean) {
        fab.show()
        isRootedTextView.update(isRooted = isRooted)
        isRootedTextView.show()
    }

    companion object {
        private const val GITHUB_LINK = "https://github.com/scottyab/rootbeer"
    }
}

