package com.example.madcamp2nd;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 상일 on 2016-06-19.
 */
public class FriendsListAdapter extends BaseAdapter {
    List<String> list;
    LayoutInflater inflater;
    Activity activity;
    TextView textView;

    public FriendsListAdapter() {
    }

    public FriendsListAdapter(Activity activity, List<String> list) {
        this.list = list;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            System.out.println("convertview==null");
            convertView = inflater.inflate(R.layout.friends_listview_item, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(layoutParams);
        }
        textView = (TextView) convertView.findViewById(R.id.friend_name);
        System.out.println("outside if");
        String friendName = list.get(position);
        System.out.println("got friendname");
        textView.setText(friendName);
        System.out.println("before returning in getview");

        return convertView;
    }


}

