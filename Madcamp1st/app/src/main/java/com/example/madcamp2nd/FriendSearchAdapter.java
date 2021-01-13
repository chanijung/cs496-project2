package com.example.madcamp2nd;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendSearchAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    Users user;
    Users friend;
    List<String> relation;

    RetrofitAPI apiInterface;
    Call<Users> call;

    public FriendSearchAdapter(Users user, Users friend) {
        this.user = user;
        this.friend = friend;
    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.textview_profile, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView profile = (ImageView) convertView.findViewById(R.id.profile) ;
        TextView name = (TextView) convertView.findViewById(R.id.name) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(convertView)
                .load(listViewItem.getUrl())
                .into(profile);
        name.setText(listViewItem.getName());

        ImageButton check = (ImageButton) convertView.findViewById(R.id.check);
        check.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(v.getContext(), friend.getName()+ "님과 친구가 되었습니다.", Toast.LENGTH_SHORT).show();
                relation = new ArrayList<>();
//                relation.add(user.getUid());
                relation.add(user.getProfile());
                relation.add(friend.getProfile());
                friend.setRelation(relation);
                apiInterface = RetrofitClient.getApiService();
                Log.e("Get name", friend.getName());
                call = apiInterface.makefriend(friend);
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        if (response.body() != null) {

                        }
                    }
                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                    }
                });
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String url, String name, Drawable check) {
        ListViewItem item = new ListViewItem();

        item.setUrl(url);
        item.setName(name);

        listViewItemList.add(item);
    }
}