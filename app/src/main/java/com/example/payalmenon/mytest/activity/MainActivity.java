package com.example.payalmenon.mytest.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.payalmenon.mytest.CommonUtil;
import com.example.payalmenon.mytest.R;
import com.example.payalmenon.mytest.task.ScanAsyncTask;

public class MainActivity extends Activity{

    private static int BUTTON_SCAN_START = 1;
    private static int BUTTON_SCAN_STOP = 0;

    private TextView displayText;
    private ScanAsyncTask scanTask;
    private BroadcastReceiver scanUpdateReceiver;
    private Button scanButton;
    private ProgressBar scanProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (true == isPermissionNeeded())
        {
            askPermission();
        }
        else
        {
            startScanInitialization();
        }
    }

    private void startScanInitialization()
    {
        displayText = (TextView) findViewById(R.id.display_text);
        scanProgress = (ProgressBar) findViewById(R.id.scan_progress);

        scanButton = (Button) findViewById(R.id.scanButton);
        setScanStartText();
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag() == BUTTON_SCAN_START) {
                    startScan();
                    displayText.setVisibility(View.GONE);
                    scanProgress.setVisibility(View.VISIBLE);
                    setScanStopText();
                } else if (v.getTag() == BUTTON_SCAN_STOP) {
                    scanTask.cancel(true);
                    displayText.setVisibility(View.VISIBLE);
                    scanProgress.setVisibility(View.GONE);
                    setScanStartText();

                }
            }
        });

        scanUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(CommonUtil.INTENT_SCAN_COMPLETE))
                {
                    displayText.setText(getApplicationContext().getString(R.string.scan_complete_text));
                    displayText.setVisibility(View.GONE);
                    scanProgress.setVisibility(View.GONE);
                    setScanStartText();
                }
                else if (intent.getAction().equals(CommonUtil.INTENT_SCAN_PROGRESS))
                {
                    int progress = intent.getIntExtra(CommonUtil.SCAN_PROGRESS_VALUE, 0);
                    scanProgress.setProgress(progress);
                    displayText.setText("Scan progressed to " + progress + "%");
                }
            }
        };

        displayText.setVisibility(View.VISIBLE);
        scanProgress.setVisibility(View.GONE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(scanUpdateReceiver, new IntentFilter(CommonUtil.INTENT_SCAN_COMPLETE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(scanUpdateReceiver, new IntentFilter(CommonUtil.INTENT_SCAN_PROGRESS));
    }

    private boolean isPermissionNeeded()
    {
        Boolean permissionNeeded = false;
        if(true == (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ) {
            if (PackageManager.PERMISSION_GRANTED != getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                permissionNeeded = true;
            }
        }
        return permissionNeeded;
    }

    private void askPermission()
    {
        this.requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, CommonUtil.REQUEST_PERMISSION_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            startScanInitialization();
        }
       else
        {
            // assuming that the user will give permission
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (false == isPermissionNeeded())
            displayText.setVisibility(View.VISIBLE);
    }

    private void setScanStartText()
    {
        scanButton.setText(getApplicationContext().getString(R.string.start_scan_text));
        scanButton.setTag(BUTTON_SCAN_START);
    }

    private void setScanStopText()
    {
        scanButton.setText(getApplicationContext().getString(R.string.stop_scan_text));
        scanButton.setTag(BUTTON_SCAN_STOP);
    }

    private void startScan()
    {
        scanTask = new ScanAsyncTask(MainActivity.this);
        scanTask.execute();
    }

    public void updateProgress(String updateText)
    {
        displayText.setText(updateText);
    }

    public void updateUI(ScanAsyncTask.ScanResults results)
    {
        Intent listIntent = new Intent(this, ListActivity.class);
        listIntent.putExtra(CommonUtil.INTENT_FILE_LIST, results.allFilesInfo);
        listIntent.putExtra(CommonUtil.INTENT_EXTENSION_FILE_LIST, results.allFileExtensionInfo);
        startActivity(listIntent);
    }
}
