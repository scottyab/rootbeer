package com.scottyab.rootchecker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.rootCheckButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRootCheck();
            }
        });

        results = (TextView)findViewById(R.id.rootResults);

    }

    private void doRootCheck() {
        RootCheck check = new RootCheck(this);
        StringBuilder b = new StringBuilder();
        b.append("If any of the below are true the root check \'might\' indicate device is rooted\n");

        b.append("\ndetectRootManagementApps: ");
        b.append(check.detectRootManagementApps());

        b.append("\ndetectPotentiallyDangerousApps: ");
        b.append(check.detectPotentiallyDangerousApps());

        b.append("\ndetectTestKeys: ");
        b.append(check.detectTestKeys());

        b.append("\ncheckForBusyBoxBinary: ");
        b.append(check.checkForBusyBoxBinary());

        b.append("\ncheckForSuBinary: ");
        b.append(check.checkForSuBinary());

        b.append("\ncheckSuExists: ");
        b.append(check.checkSuExists());

        b.append("\ncheckForRWSystem: ");
        b.append(check.checkForRWSystem());

        b.append("\ncheckForDangerousProps: ");
        b.append(check.checkForDangerousProps());

        b.append("\ncheckForRootNative: ");
        b.append(check.checkForRootNative());

        b.append("\ndetectRootCloakingApps: ");
        b.append(check.detectRootCloakingApps());

        results.setText(b.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
