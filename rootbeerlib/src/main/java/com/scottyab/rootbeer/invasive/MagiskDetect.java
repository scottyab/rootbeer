package com.scottyab.rootbeer.invasive;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.scottyab.rootbeer.util.QLog;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MagiskDetect {

    // TODO: Replace direct access with something like RootBeer.invasiveCheck().detectMagisk()

    // Example hiding package name:
    // "com.Qbv2Z1r.Ye.YDAeG"

    // MD5s should all be lowercase
    private static String[] fingerprints = {"938230585bf194acfd8d20112a65e196"};

    /**
     * Try to detect magisk using the fingerprint of it's management icon
     *
     * Warning: This is going to be cpu-intensive so don't perform this check often
     *
     * @param context - context to grab package manager from
     *
     * @return true if magisk icon was detected false otherwise
     */
    public static boolean detectMagisk(Context context) {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if (pm.getLaunchIntentForPackage(packageInfo.packageName) == null){
                // Ignore non launchables
                continue;
            }
            QLog.d(packageInfo.packageName);

            Drawable logo;
            try {
                logo = pm.getApplicationIcon(packageInfo.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            if (isDrawableMagisk(logo)){
                QLog.e("Magisk Detected Package Name :" + packageInfo.packageName);
                return true;
            }
        }
        return false;
    }

    public static boolean isDrawableMagisk(Drawable drawable){
        if (drawable == null) {
            return false;
        }

        // FIXME: could be more perfomant to reuse bitmaps
        Bitmap bitmap = drawableToBitmap(drawable);
        byte[] pngArray = bitmapToPNGByteArray(bitmap);

        String md5 = getMD5(pngArray);

        QLog.d("MD5 is: "+md5);

        return Arrays.asList(fingerprints).contains(md5);
    }

    public static byte[] bitmapToPNGByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String getMD5(byte[] input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input);

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
