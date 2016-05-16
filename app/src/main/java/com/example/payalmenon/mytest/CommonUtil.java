package com.example.payalmenon.mytest;

import java.util.ArrayList;

/**
 * Created by payal.menon on 5/8/16.
 */
public class CommonUtil {

    public static String INTENT_SCAN_PROGRESS = "scan_progress";
    public static String INTENT_SCAN_COMPLETE = "scan_complete";
    public static String SCAN_PROGRESS_VALUE = "scan_progress_value";
    public static String INTENT_FILE_LIST = "scan_file_list";
    public static String INTENT_EXTENSION_FILE_LIST = "scan_extension_file_list";
    public static final int REQUEST_PERMISSION_STORAGE = 1111;

    public ArrayList<FileInfo> allFilesInfo;
    public ArrayList<FileExtensionInfo> allFileExtensionInfo;

}
