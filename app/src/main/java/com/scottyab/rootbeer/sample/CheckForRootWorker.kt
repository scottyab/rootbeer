package com.scottyab.rootbeer.sample

import android.content.Context
import com.scottyab.rootbeer.RootBeer
import com.scottyab.rootbeer.util.Utils

class CheckForRootWorker(context: Context) {

    private val rootBeer = RootBeer(context)

    suspend operator fun invoke(): List<RootItemResult> = getRootResults()

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
}

