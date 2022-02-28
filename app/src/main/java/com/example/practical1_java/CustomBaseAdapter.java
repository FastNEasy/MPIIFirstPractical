package com.example.practical1_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> nameList;
    ArrayList<String> dateList;
    LayoutInflater inflater;
    int img;

    public CustomBaseAdapter(Context ctx, ArrayList<String> nameList, ArrayList<String> dateList, int img){
        this.context = ctx;
        this.nameList = nameList;
        this.dateList = dateList;
        this.img = img;
        inflater = LayoutInflater.from(ctx);
    }
    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.customlayout, null);
        TextView txtView = (TextView) convertView.findViewById(R.id.textView);
        ImageView imgview = (ImageView) convertView.findViewById(R.id.imgIcon);
        TextView txtView2 = (TextView) convertView.findViewById(R.id.textView2);
        txtView.setText(nameList.get(position));
        txtView2.setText(dateList.get(position));
        imgview.setImageResource(R.drawable.ic_baseline_folder_24);
        return convertView;
    }
}
