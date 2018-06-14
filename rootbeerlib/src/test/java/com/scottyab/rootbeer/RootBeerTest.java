package com.scottyab.rootbeer;

import android.content.Context;
import android.content.pm.PackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by matthew on 31/10/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RootBeerTest {

    @Test
    public void testIsRooted() {

        RootBeer rootBeer = Mockito.mock(RootBeer.class);

        when(rootBeer.isRooted()).thenCallRealMethod();

        when(rootBeer.detectRootManagementApps()).thenReturn(false);
        when(rootBeer.detectPotentiallyDangerousApps()).thenReturn(false);
        when(rootBeer.checkForBinary("busybox")).thenReturn(false);
        when(rootBeer.checkForBinary("su")).thenReturn(false);
        when(rootBeer.checkForDangerousProps()).thenReturn(false);
        when(rootBeer.checkForRWPaths()).thenReturn(false);
        when(rootBeer.detectTestKeys()).thenReturn(false);
        when(rootBeer.checkSuExists()).thenReturn(false);
        when(rootBeer.checkForRootNative()).thenReturn(false);

        // Test we return false when all methods return false
        assertTrue(!rootBeer.isRooted());

        when(rootBeer.checkForRootNative()).thenReturn(true);

        // Test we return true when just one returns true
        assertTrue(rootBeer.isRooted());
    }

    @Test
    public void testIsRootedWithoutBusyBoxCheck() {

        RootBeer rootBeer = Mockito.mock(RootBeer.class);

        when(rootBeer.isRooted()).thenCallRealMethod();
        when(rootBeer.isRootedWithoutBusyBoxCheck()).thenCallRealMethod();

        when(rootBeer.detectRootManagementApps()).thenReturn(false);
        when(rootBeer.detectPotentiallyDangerousApps()).thenReturn(false);
        when(rootBeer.checkForBinary("busybox")).thenReturn(true);
        when(rootBeer.checkForBinary("su")).thenReturn(false);
        when(rootBeer.checkForDangerousProps()).thenReturn(false);
        when(rootBeer.checkForRWPaths()).thenReturn(false);
        when(rootBeer.detectTestKeys()).thenReturn(false);
        when(rootBeer.checkSuExists()).thenReturn(false);
        when(rootBeer.checkForRootNative()).thenReturn(false);

        // Test we return false when all methods return false
        assertTrue(rootBeer.isRooted());

        // Test it doesn't matter what checkForBinary("busybox") returns
        assertTrue(!rootBeer.isRootedWithoutBusyBoxCheck());

    }

    @Test
    public void testDetectAppsReturnsFalseWhenNoneFound() throws Exception {

        Context context = Mockito.mock(Context.class);
        PackageManager packageManager = Mockito.mock(PackageManager.class);

        RootBeer rootBeer = new RootBeer(context);
        rootBeer.setLogging(false);

        when(context.getPackageManager()).thenReturn(packageManager);

        // Return exception for every package installed
        when(packageManager.getPackageInfo(anyString(), anyInt())).thenThrow(new PackageManager.NameNotFoundException());

        // Should be false as no packages detected
        assertFalse(rootBeer.detectPotentiallyDangerousApps());
        assertFalse(rootBeer.detectRootCloakingApps());
        assertFalse(rootBeer.detectRootManagementApps());
    }

    /**
     *
     * @param packageNameToFind - We will pretend packagemanager has this package installed, can be null for no packages installed
     * @return - Mocked Context with mocked Packagemanager
     * @throws PackageManager.NameNotFoundException
     */
    private Context getMockedContext(String packageNameToFind) throws PackageManager.NameNotFoundException {
        Context context = Mockito.mock(Context.class);
        PackageManager packageManager = Mockito.mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(packageManager);

        if (packageManager == null){
            // Return exception for all packages
            when(packageManager.getPackageInfo(anyString(), anyInt())).thenThrow(new PackageManager.NameNotFoundException());
        }
        else {
            // Return exception for every package other than one we should detect
            when(packageManager.getPackageInfo(not(eq(packageNameToFind)), anyInt())).thenThrow(new PackageManager.NameNotFoundException());
        }
        return context;
    }

    @Test
    public void testDetectPotentiallyDangerousApps() throws Exception {

        RootBeer rootBeer = new RootBeer(getMockedContext(null));
        rootBeer.setLogging(false);

        // Should be false as no packages detected
        assertFalse(rootBeer.detectPotentiallyDangerousApps());

        rootBeer = new RootBeer(getMockedContext(Const.knownDangerousAppsPackages[0]));
        rootBeer.setLogging(false);

        // Should be true as package detected
        assertTrue(rootBeer.detectPotentiallyDangerousApps());

    }

    @Test
    public void testDetectRootManagementApps() throws Exception {

        RootBeer rootBeer = new RootBeer(getMockedContext(null));
        rootBeer.setLogging(false);

        // Should be false as no packages detected
        assertFalse(rootBeer.detectRootManagementApps());

        rootBeer = new RootBeer(getMockedContext(Const.knownRootAppsPackages[0]));
        rootBeer.setLogging(false);

        // Should be true as package detected
        assertTrue(rootBeer.detectRootManagementApps());

    }

    @Test
    public void testDetectRootCloakingApps() throws Exception {

        RootBeer rootBeer = new RootBeer(getMockedContext(null));
        rootBeer.setLogging(false);

        // Should be false as no packages detected
        assertFalse(rootBeer.detectRootCloakingApps());

        rootBeer = new RootBeer(getMockedContext(Const.knownRootCloakingPackages[0]));
        rootBeer.setLogging(false);

        // Should be true as package detected
        assertTrue(rootBeer.detectRootCloakingApps());

    }

}