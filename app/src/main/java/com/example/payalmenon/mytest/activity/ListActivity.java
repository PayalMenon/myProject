package com.example.payalmenon.mytest.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.ActionBar.Tab;

import com.example.payalmenon.mytest.CommonUtil;
import com.example.payalmenon.mytest.FileExtensionInfo;
import com.example.payalmenon.mytest.FileInfo;

import java.util.ArrayList;

/**
 * Created by payal.menon on 5/14/16.
 */
public class ListActivity extends Activity {

    ArrayList<FileInfo> fileList;
    ArrayList<FileExtensionInfo> extensionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(null != getIntent())
        {
            fileList = (ArrayList<FileInfo>) getIntent().getSerializableExtra(CommonUtil.INTENT_FILE_LIST);
            extensionList = (ArrayList<FileExtensionInfo>) getIntent().getSerializableExtra(CommonUtil.INTENT_EXTENSION_FILE_LIST);
        }
        ActionBar actionBar = this.getActionBar();

        Tab filesTab = actionBar.newTab();
        filesTab.setText("Files Info");
        filesTab.setTabListener(new TabListener(this, "Files Info"));
        actionBar.addTab(filesTab);

        Tab extensionTab = actionBar.newTab();
        extensionTab.setText("Extension Info");
        extensionTab.setTabListener(new TabListener(this, "Extension Info"));
        actionBar.addTab(extensionTab);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }


    public ArrayList<FileInfo> getFileList()
    {
        return fileList;
    }

    public ArrayList<FileExtensionInfo> getExtensionList()
    {
        return extensionList;
    }

    public class TabListener implements ActionBar.TabListener
    {

        Fragment tabFragment;
        Activity myAcitivty;
        String fragmentTag;

        public TabListener(Activity activity, String tag){
            myAcitivty = activity;
            fragmentTag = tag;
        }

        public TabListener(Activity activity, Fragment fragemnt, String tag)
        {
            tabFragment = fragemnt;
            myAcitivty = activity;
            fragmentTag = tag;
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            FragmentManager fm = myAcitivty.getFragmentManager();
            Fragment mFragment = fm.findFragmentByTag(fragmentTag);
            switch (tab.getPosition()) {
                case 0:
                    if(mFragment == null) {
                        mFragment = new FileFragment();
                        ft.add(android.R.id.content, mFragment, null);
                    }
                    else {
                        ft.show(mFragment);
                    }
                    break;
                case 1:
                    if(mFragment == null) {
                        mFragment = new ExtensionFragment();
                        ft.add(android.R.id.content, mFragment, null);
                    }
                    else {
                        ft.show(mFragment);
                    }
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            FragmentManager fm = myAcitivty.getFragmentManager();
            Fragment fragment = fm.findFragmentByTag(fragmentTag);
            fm.popBackStackImmediate();
            if(fragment != null) {
                ft.hide(fragment);
            }
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            myAcitivty.getFragmentManager().popBackStackImmediate();
        }
    }
}
