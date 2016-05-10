package com.sixteenplusfour.beerprogressview.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.sixteenplusfour.beerprogressview.BeerProgressView;

public class MainActivity extends AppCompatActivity {

    private MainActivity mActivity;
    private BottomBar mBottomBar;
    private BeerProgressView mBeerProgressView;
    private AsyncTask<Boolean, Integer, Boolean> mPourBeerTask;
    private AlertDialog infoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_github) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getResources().getString(R.string.github_link)));
            startActivity(i);
            return true;
        } else if (id == R.id.action_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        if (infoDialog != null && infoDialog.isShowing()) {
            //do nothing if already showing
        } else {
            infoDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.info_details)
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("More info", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(getResources().getString(R.string.github_link))));
                        }
                    })
                    .create();
            infoDialog.show();
        }
    }

    private void initViews(Bundle savedInstanceState) {
        mActivity = this;
        mBeerProgressView = (BeerProgressView) findViewById(R.id.content_main_beer_progress_view);

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.menu_bottom_bar, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                // The user selected item
                if (menuItemId == R.id.lager_item) {
                    pourBeer(ContextCompat.getColor(mActivity, R.color.lager),
                            ContextCompat.getColor(mActivity, R.color.lager_bubble));
                } else if (menuItemId == R.id.ale_item) {
                    pourBeer(ContextCompat.getColor(mActivity, R.color.ale),
                            ContextCompat.getColor(mActivity, R.color.ale_bubble));
                } else if (menuItemId == R.id.stout_item) {
                    pourBeer(ContextCompat.getColor(mActivity, R.color.stout),
                            ContextCompat.getColor(mActivity, R.color.stout_bubble));
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
    }

    private void pourBeer(int beerColour, int bubbleColour) {
        if (mPourBeerTask != null) {
            mPourBeerTask.cancel(true);
            mPourBeerTask = null;
            mBeerProgressView.setBeerProgress(0);
        }
        mBeerProgressView.setBeerColor(beerColour);
        mBeerProgressView.setBubbleColor(bubbleColour);
        mPourBeerTask = new PourBeerTask(this, mBeerProgressView).execute(true);
    }
}
