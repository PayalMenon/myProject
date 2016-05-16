package com.example.payalmenon.mytest.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.payalmenon.mytest.FileExtensionInfo;
import com.example.payalmenon.mytest.R;

import java.util.ArrayList;

/**
 * Created by payal.menon on 5/14/16.
 */
public class ExtensionFragment extends Fragment{
    ListView fileListView;
    private ArrayList<FileExtensionInfo> fileList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() != null) {
            fileList = ((ListActivity)getActivity()).getExtensionList();
            fileListView = (ListView) getActivity().findViewById(R.id.scan_list);
            if (fileListView != null) {
                fileListView.setAdapter(new ScanAdapterForExtension(getActivity()));
            }
        }
    }

    public class ScanAdapterForExtension extends BaseAdapter {
        Context scanContext;
        LayoutInflater inflator;

        ScanAdapterForExtension(Context context) {
            scanContext = context;
            inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return (Object) fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int size = fileList.size();
            int index = size - position - 1;
            if (convertView == null) {
                convertView = inflator.inflate(android.R.layout.simple_list_item_2, null);
                ViewHolder holder = new ViewHolder();
                holder.fileExtension = (TextView) convertView.findViewById(android.R.id.text1);
                holder.count = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.fileExtension.setText(fileList.get(index).fileExtension);
            viewHolder.count.setText(Long.toString(fileList.get(index).count));

            return convertView;
        }
    }

    public static class ViewHolder
    {
        TextView fileExtension;
        TextView count;
    }
}

