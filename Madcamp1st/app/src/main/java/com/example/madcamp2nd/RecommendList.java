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

public class RecommendList extends Activity {
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
        setContentView(R.layout.activity_recommendlist);

        user = (Users) getIntent().getSerializableExtra("user");

        // get friends list from db
        apiInterface = RetrofitClient.getApiService();
        call = apiInterface.getmessage(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Log.e( "recommendlist", "dddd recommendlist ok");
                if (response.body() != null) {
                    RecommendListAdapter adapter = new RecommendListAdapter(user, friend);
                    ListView listview = (ListView) findViewById(R.id.listView_recommendlist);
                    listview.setAdapter((ListAdapter) adapter);

                    List<List<String>> rec = response.body().getRecommend();
                    for (int x = 0; x < friends_list.size(); x++) {
                        adapter.addItem(rec.get(x).get(0),
                                rec.get(x).get(1), rec.get(x).get(2), rec.get(x).get(3), rec.get(x).get(4));
                    }
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.e("recommendlist", "dddd recommendlist fail");
            }
        });



    }
}
