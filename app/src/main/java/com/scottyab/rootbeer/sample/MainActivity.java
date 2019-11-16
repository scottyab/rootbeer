package com.scottyab.rootbeer.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import uk.co.barbuzz.beerprogressview.BeerProgressView;


public class MainActivity extends AppCompatActivity
        implements CheckRootTask.OnCheckRootFinishedListener {

    private static final String GITHUB_LINK = "https://github.com/scottyab/rootbeer";

    private AlertDialog infoDialog;
    private BeerProgressView beerView;
    private MainActivity mActivity;
    private TextViewFont isRootedText;
    private ArrayList<ImageView> checkRootImageViewList;
    private TextView isRootedTextDisclaimer;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beerView = findViewById(R.id.loadingRootCheckBeerView);
        isRootedText = findViewById(R.id.content_main_is_rooted_text);
        isRootedTextDisclaimer = findViewById(R.id.content_mainisRootedTextDisclaimer);

        ImageView rootCheck1ImageView = findViewById(R.id.content_main_root_check_image_1);
        ImageView rootCheck2ImageView = findViewById(R.id.content_main_root_check_image_2);
        ImageView rootCheck3ImageView = findViewById(R.id.content_main_root_check_image_3);
        ImageView rootCheck4ImageView = findViewById(R.id.content_main_root_check_image_4);
        ImageView rootCheck5ImageView = findViewById(R.id.content_main_root_check_image_5);
        ImageView rootCheck6ImageView = findViewById(R.id.content_main_root_check_image_6);
        ImageView rootCheck7ImageView = findViewById(R.id.content_main_root_check_image_7);
        ImageView rootCheck8ImageView = findViewById(R.id.content_main_root_check_image_8);
        ImageView rootCheck9ImageView = findViewById(R.id.content_main_root_check_image_9);
        ImageView rootCheck10ImageView = findViewById(R.id.content_main_root_check_image_10);
        ImageView rootCheck11ImageView = findViewById(R.id.content_main_root_check_image_11);
        checkRootImageViewList = new ArrayList<>();
        checkRootImageViewList.add(rootCheck1ImageView);
        checkRootImageViewList.add(rootCheck2ImageView);
        checkRootImageViewList.add(rootCheck3ImageView);
        checkRootImageViewList.add(rootCheck4ImageView);
        checkRootImageViewList.add(rootCheck5ImageView);
        checkRootImageViewList.add(rootCheck6ImageView);
        checkRootImageViewList.add(rootCheck7ImageView);
        checkRootImageViewList.add(rootCheck8ImageView);
        checkRootImageViewList.add(rootCheck9ImageView);
        checkRootImageViewList.add(rootCheck10ImageView);
        checkRootImageViewList.add(rootCheck11ImageView);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.setVisibility(View.GONE);
                resetRootCheckImages();
                CheckRootTask checkRootTask = new CheckRootTask(mActivity, mActivity, beerView,
                        checkRootImageViewList);
                checkRootTask.execute(true);
            }
        });
    }

    private void resetRootCheckImages() {
        isRootedText.setVisibility(View.GONE);
        for (ImageView imageView : checkRootImageViewList) {
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
        //do nothing if already showing
        if (infoDialog == null || !infoDialog.isShowing()) {
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
        floatingActionButton.setVisibility(View.VISIBLE);
        isRootedText.setText(isRooted ? "ROOTED*" : "NOT ROOTED");
        isRootedTextDisclaimer.setVisibility(isRooted ? View.VISIBLE : View.GONE);
        isRootedText.setTextColor(isRooted ? getResources().getColor(R.color.fail) : getResources().getColor(R.color.pass));
        isRootedText.setVisibility(View.VISIBLE);
    }

}
