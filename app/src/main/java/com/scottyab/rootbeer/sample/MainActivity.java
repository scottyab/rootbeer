package com.scottyab.rootbeer.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import uk.co.barbuzz.beerprogressview.BeerProgressView;


public class MainActivity extends AppCompatActivity
        implements CheckRootTask.OnCheckRootFinishedListener {

    private static final String GITHUB_LINK = "https://github.com/scottyab/rootbeer";

    private AlertDialog infoDialog;
    private BeerProgressView beerView;
    private MainActivity mActivity;
    private TextViewFont isRootedText;
    private ArrayList<ImageView> checkRootimageViewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beerView = (BeerProgressView) findViewById(R.id.loadingRootCheckBeerView);
        isRootedText = (TextViewFont) findViewById(R.id.content_main_is_rooted_text);

        ImageView rootCheck1ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_1);
        ImageView rootCheck2ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_2);
        ImageView rootCheck3ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_3);
        ImageView rootCheck4ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_4);
        ImageView rootCheck5ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_5);
        ImageView rootCheck6ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_6);
        ImageView rootCheck7ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_7);
        ImageView rootCheck8ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_8);
        ImageView rootCheck9ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_9);
        ImageView rootCheck10ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_10);
        ImageView rootCheck11ImageView = (ImageView) findViewById(R.id.content_main_root_check_image_11);
        checkRootimageViewList = new ArrayList<>();
        checkRootimageViewList.add(rootCheck1ImageView);
        checkRootimageViewList.add(rootCheck2ImageView);
        checkRootimageViewList.add(rootCheck3ImageView);
        checkRootimageViewList.add(rootCheck4ImageView);
        checkRootimageViewList.add(rootCheck5ImageView);
        checkRootimageViewList.add(rootCheck6ImageView);
        checkRootimageViewList.add(rootCheck7ImageView);
        checkRootimageViewList.add(rootCheck8ImageView);
        checkRootimageViewList.add(rootCheck9ImageView);
        checkRootimageViewList.add(rootCheck10ImageView);
        checkRootimageViewList.add(rootCheck11ImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetRootCheckImages();
                CheckRootTask checkRootTask = new CheckRootTask(mActivity, mActivity, beerView,
                        checkRootimageViewList);
                checkRootTask.execute(true);
            }
        });
    }

    private void resetRootCheckImages() {
        isRootedText.setVisibility(View.GONE);
        for (ImageView imageView : checkRootimageViewList) {
            imageView.setImageDrawable(null);
        }
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
            i.setData(Uri.parse(GITHUB_LINK));
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
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
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
                                    Uri.parse(GITHUB_LINK)));
                        }
                    })
                    .create();
            infoDialog.show();
        }
    }

    @Override
    public void onCheckRootFinished(boolean isRooted) {
        isRootedText.setText(isRooted ? "ROOTED" : "NOT ROOTED");
        isRootedText.setTextColor(isRooted ? getResources().getColor(R.color.fail) : getResources().getColor(R.color.pass));
        isRootedText.setVisibility(View.VISIBLE);
    }

}
