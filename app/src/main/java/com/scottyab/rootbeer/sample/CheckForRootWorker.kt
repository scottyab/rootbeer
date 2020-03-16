package com.scottyab.rootbeer.sample

import android.content.Context
import com.scottyab.rootbeer.RootBeer
import com.scottyab.rootbeer.util.Utils
import kotlinx.coroutines.delay
import timber.log.Timber

class CheckForRootWorker(context: Context) {

    private val rootBeer = RootBeer(context);

    suspend operator fun invoke(resultAction: (RootItemResult, Int) -> Unit): Boolean {
        val results = getRootResults()
        val itemProgress = progressMax / (results.size - 1)

        results.forEachIndexed { index, rootItemResult ->
            Timber.d("[$index] $rootItemResult")
            //This is just for the effect in the UI
            delay(artificialDelayInMilli)
            resultAction.invoke(rootItemResult, index * itemProgress)
        }
        return results.map { it.result }.find { it } ?: false
    }

    private fun getRootResults() = listOf(
        RootItemResult("Root Management Apps", rootBeer.detectRootManagementApps()),
        RootItemResult("Potentially Dangerous Apps", rootBeer.detectPotentiallyDangerousApps()),
        RootItemResult("Root Cloaking Apps", rootBeer.detectRootCloakingApps()),
        RootItemResult("TestKeys", rootBeer.detectTestKeys()),
        RootItemResult("BusyBoxBinary", rootBeer.checkForBusyBoxBinary()),
        RootItemResult("SU Binary", rootBeer.checkForSuBinary()),
        RootItemResult("2nd SU Binary check", rootBeer.checkSuExists()),
        RootItemResult("For RW Paths", rootBeer.checkForRWPaths()),
        RootItemResult("Dangerous Props", rootBeer.checkForDangerousProps()),
        RootItemResult("Root via native check", rootBeer.checkForRootNative()),
        RootItemResult("SE linux Flag Is Enabled", Utils.isSelinuxFlagInEnabled()),
        RootItemResult("Magisk specific checks", rootBeer.checkForMagiskBinary())
    )

    companion object {
        private const val artificialDelayInMilli = 150L
        const val progressMax = 100
    }
}

