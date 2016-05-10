package com.sixteenplusfour.beerprogressview.sample;

import android.content.Context;
import android.os.AsyncTask;

import com.sixteenplusfour.beerprogressview.BeerProgressView;

/**
 * Old school Async to update progress view gradually
 */
public class PourBeerTask extends AsyncTask<Boolean, Integer, Boolean> {

    private static final int SLEEP_TIME = 70;
    private final BeerProgressView mBeerProgressView;
    private final Context mContext;

    public PourBeerTask(Context ctx, BeerProgressView beerProgressView) {
        mBeerProgressView = beerProgressView;
        mContext = ctx;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mBeerProgressView.setBeerProgress(values[0]);
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        for (int i = 0; i < 90; i++) {
            publishProgress(i);
            try {
                if (i > 10) Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {

            }
        }
        return true;
    }

}
