package com.example.payalmenon.mytest.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import com.example.payalmenon.mytest.CommonUtil;
import com.example.payalmenon.mytest.FileExtensionInfo;
import com.example.payalmenon.mytest.FileInfo;
import com.example.payalmenon.mytest.R;
import com.example.payalmenon.mytest.activity.MainActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by payal.menon on 5/9/16.
 */
public class ScanAsyncTask extends AsyncTask<Void, Integer, Void> {

    private static final String EXTERNAL_SD_PATH = "/mnt";
    public static final int SCAN_NOTIFICATION_ID = 121;
    private ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
    private HashMap<String, Integer> fileHash = new HashMap<String, Integer>();
    private long avgFileSize;
    private ArrayList<FileExtensionInfo> extensionList = new ArrayList<FileExtensionInfo>();
    private int progess;
    private int totalSize;
    private int fileCount;
    private MainActivity mainActivity;
    private LocalBroadcastManager broadcastManager;
    private NotificationManager manager;

    public ScanAsyncTask(MainActivity activity)
    {
        mainActivity = activity;
        manager = (NotificationManager)activity.getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onPreExecute() {
        broadcastManager = LocalBroadcastManager.getInstance(mainActivity);
        super.onPreExecute();
        mainActivity.updateProgress("Scan Started");
        postNotificationForUpdate(mainActivity.getApplicationContext(), "Scan Started");
    }

    @Override
    protected Void doInBackground(Void... params) {
        progess = 10;
        publishProgress(progess);
        startScan();
        progess = 100;
        publishProgress(progess);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Intent i = new Intent(CommonUtil.INTENT_SCAN_PROGRESS);
        i.putExtra(CommonUtil.SCAN_PROGRESS_VALUE, values[0]);
        broadcastManager.sendBroadcast(i);
        postNotificationForUpdate(mainActivity.getApplicationContext(), "Scan progressed to " + values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent i = new Intent(CommonUtil.INTENT_SCAN_COMPLETE);
        broadcastManager.sendBroadcast(i);
        postNotificationForUpdate(mainActivity.getApplicationContext(), "Scan complete ");
        dismissNotification();

        ScanResults results = new ScanResults();
        results.allFilesInfo = fileList;
        results.allFileExtensionInfo = extensionList;
        mainActivity.updateUI(results);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        mainActivity.updateProgress("Scan cancelled");
        postNotificationForUpdate(mainActivity.getApplicationContext(), "Scan cancelled ");
        dismissNotification();

    }

    public void startScan()
    {
        File externalDirectory =  new File(Environment.getExternalStorageDirectory().getPath());
       // File externalDirectory =  new File(EXTERNAL_SD_PATH); for testing

        getListFiles(externalDirectory);
        avgFileSize = totalSize/fileCount;
        System.out.println("avgFileSize = " + avgFileSize);

        Iterator<String> keysIterator = fileHash.keySet().iterator();
        while(keysIterator.hasNext())
        {
            FileExtensionInfo info = new FileExtensionInfo();
            info.fileExtension = keysIterator.next();
            info.count =  fileHash.get(info.fileExtension);
            extensionList.add(info);
        }

        publishProgress(20);
        getTopTenFiles();
        getTopFiveExtensions();
    }

    private void getTopTenFiles()
    {
        int count = fileList.size();
        sortFilesOnSize(0, (count - 1));
        for (int i = (count-1); i > (count - 11); i--)
        {
            FileInfo info = fileList.get(i);
            System.out.println("Top Size FileName = " + info.fileName + "and size = " + info.fileSize);
        }
    }

    private void getTopFiveExtensions()
    {
        int count = extensionList.size();
        sortFilesOnCount(0, (count - 1));
        for (int i = (count - 1); i > (count - 6); i--)
        {
            FileExtensionInfo info = extensionList.get(i);
            System.out.println("Frequently used extension = " + info.fileExtension + " and count = " + info.count);
        }
    }

    private void sortFilesOnSize(int startIndex, int endIndex)
    {
        if(startIndex < endIndex) {
            int pivotIndex = getPivotIndex(startIndex, endIndex);
            sortFilesOnSize(startIndex, pivotIndex - 1);
            sortFilesOnSize(pivotIndex + 1, endIndex);
        }
    }

    private int getPivotIndex(int startIndex, int endIndex)
    {
        int pivotIndex = startIndex;
        FileInfo info = fileList.get(endIndex);
        long pivot = info.fileSize;

        for (int i = startIndex; i < endIndex; i++)
        {
            FileInfo fileInfo = fileList.get(i);
            if(fileInfo.fileSize < pivot)
            {
                swap(pivotIndex, i);
                pivotIndex++;
            }
        }
        swap(pivotIndex, endIndex);
        return pivotIndex;
    }

    private void swap(int firstIndex, int secondIndex)
    {
        FileInfo temp = fileList.get(firstIndex);
        FileInfo temp2 = fileList.get(secondIndex);
        fileList.set(firstIndex, temp2);
        fileList.set(secondIndex, temp);
    }

    private void sortFilesOnCount(int startIndex, int endIndex)
    {
        if(startIndex < endIndex) {
            int pivotIndex = getPivotIndexForCount(startIndex, endIndex);
            sortFilesOnCount(startIndex, pivotIndex - 1);
            sortFilesOnCount(pivotIndex + 1, endIndex);
        }
    }

    private int getPivotIndexForCount(int startIndex, int endIndex)
    {
        int pivotIndex = startIndex;
        FileExtensionInfo info = extensionList.get(endIndex);
        long pivot = info.count;

        for (int i = startIndex; i < endIndex; i++)
        {
            FileExtensionInfo fileInfo = extensionList.get(i);
            if(fileInfo.count < pivot)
            {
                swapForCount(pivotIndex, i);
                pivotIndex++;
            }
        }
        swapForCount(pivotIndex, endIndex);
        return pivotIndex;
    }

    private void swapForCount(int firstIndex, int secondIndex)
    {
        FileExtensionInfo temp = extensionList.get(firstIndex);
        FileExtensionInfo temp2 = extensionList.get(secondIndex);
        extensionList.set(firstIndex, temp2);
        extensionList.set(secondIndex, temp);
    }



    public class ScanResults implements Serializable
    {
        private static final long serialVersionUID = 3L;
        public ArrayList<FileInfo> allFilesInfo;
        public ArrayList<FileExtensionInfo> allFileExtensionInfo;
    }

    private void getListFiles(File parentDir) {

        File[] files = parentDir.listFiles();
        if(null == files)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                getListFiles(file);}
            else {
                int count = 0;
                FileInfo info = new FileInfo();
                info.fileName = file.getName();
                info.fileSize = file.length();
                info.fileExtension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                fileList.add(info);

                totalSize += info.fileSize;

                if (fileHash.isEmpty())
                {
                    count = count + 1;
                    fileHash.put(info.fileExtension, (count));
                }
                else
                {
                    if (fileHash.containsKey(info.fileExtension))
                    {
                        count = fileHash.get(info.fileExtension);
                    }
                    count = count + 1;
                    fileHash.put(info.fileExtension, (count));
                }

                fileHash.put(info.fileExtension, count);

                fileCount++;
                progess = progess+1;
                publishProgress(progess);
            }
        }
    }

    private void postNotificationForUpdate(Context context, String updateString)
    {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("MyApp").setContentText(updateString).setAutoCancel(true).
                setAutoCancel(false).setSmallIcon(R.mipmap.ic_launcher).
                setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        manager.notify(SCAN_NOTIFICATION_ID, builder.build());
    }

    private void dismissNotification()
    {
        manager.cancel(SCAN_NOTIFICATION_ID);
    }
}
