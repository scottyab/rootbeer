package com.scottyab.rootbeer.sample;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.scottyab.rootbeer.RootBeer;
import com.scottyab.rootbeer.util.Utils;

import java.util.ArrayList;

import uk.co.barbuzz.beerprogressview.BeerProgressView;

/**
 * class to pretend we are doing some really clever stuff that takes time
 *
 * Old skool Async - this could been nicer but just threw together at the mo
 */
public class CheckRootTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final String TAG = "CheckRootTask";
    private static final int SLEEP_TIME = 70;
    private final BeerProgressView mBeerProgressView;
    private final Context mContext;
    private ArrayList<ImageView> mCheckRootimageViewList;

    private OnCheckRootFinishedListener mListener;
    private Drawable redCross;
    private Drawable greenTick;
    private boolean mIsCheck;


    public interface OnCheckRootFinishedListener {
        void onCheckRootFinished(boolean isRooted);
    }

    public CheckRootTask(Context ctx, OnCheckRootFinishedListener listener,
                         BeerProgressView beerProgressView, ArrayList<ImageView> checkRootimageViewList) {
        mListener  = listener;
        mBeerProgressView = beerProgressView;
        mContext = ctx;
        mCheckRootimageViewList = checkRootimageViewList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        redCross = mContext.getResources().getDrawable(R.drawable.ic_cross_red_24dp);
        greenTick = mContext.getResources().getDrawable(R.drawable.ic_tick_green_24dp);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Integer value = values[0];
        mBeerProgressView.setBeerProgress(value);

        int index = (value / 8) - 1;
        if (index >= 0 & index < mCheckRootimageViewList.size()) {
            mCheckRootimageViewList.get(index).setImageDrawable(mIsCheck ? greenTick : redCross);
        }

        mIsCheck = false;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        RootBeer check = new RootBeer(mContext);

        for (int i = 0; i < 90; i++) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {

            }
            switch (i) {
                case 8:
                    mIsCheck = check.detectRootManagementApps();
                    break;
                case 16:
                    mIsCheck = check.detectPotentiallyDangerousApps();
                    break;
                case 24:
                    mIsCheck = check.detectTestKeys();
                    break;
                case 32:
                    mIsCheck = check.checkForBusyBoxBinary();
                    break;
                case 40:
                    mIsCheck = check.checkForSuBinary();
                    break;
                case 48:
                    mIsCheck = check.checkSuExists();
                    break;
                case 56:
                    mIsCheck = check.checkForRWPaths();
                    break;
                case 64:
                    mIsCheck = check.checkForDangerousProps();
                    break;
                case 72:
                    mIsCheck = check.checkForRootNative();
                    break;
                case 80:
                    mIsCheck = check.detectRootCloakingApps();
                    break;
                case 88:
                    mIsCheck = Utils.isSelinuxFlagInEnabled();
                    break;
                default:
                    publishProgress(i);
            }
        }
        return check.isRooted();
    }

    @Override
    protected void onPostExecute(Boolean isRooted) {
        super.onPostExecute(isRooted);
        mListener.onCheckRootFinished(isRooted);
    }

}
