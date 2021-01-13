package com.example.madcamp2nd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.core.content.ContextCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendMaker extends Activity {
    EditText edit_name;
    ImageButton search_btn;
    List<String> friends_list;

    RetrofitAPI apiInterface;
    Users user = new Users();
    Users friend;
    Call<Users> call;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendmaker);

        user = (Users) getIntent().getSerializableExtra("user");
        edit_name = (EditText) findViewById(R.id.edit_name);
        search_btn = (ImageButton) findViewById(R.id.search_btn);

        search_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // get friends list from db
                apiInterface = RetrofitClient.getApiService();
                friend = new Users();
                String friend_name = edit_name.getText().toString();
                friend.setName(friend_name);
                call = apiInterface.findfriend(friend);
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        if (response.body() != null) {
                            FriendSearchAdapter adapter = new FriendSearchAdapter(user, friend);
                            ListView listview = (ListView) findViewById(R.id.listView_samename);
                            listview.setAdapter((ListAdapter) adapter);

                            friends_list = response.body().getProfiles();
                            for (int x = 0; x < friends_list.size(); x++) {
                                friend.setProfile(friends_list.get(x));
                                adapter.addItem(friends_list.get(x), // profile
                                        edit_name.getText().toString(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_check));
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                    }
                });
            }
        });

    }
}
