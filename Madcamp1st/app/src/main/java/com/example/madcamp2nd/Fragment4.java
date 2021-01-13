package com.example.madcamp2nd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment4 extends Fragment implements View.OnClickListener {

    private ListView listView;
    public static ArrayList<MusicDto> list;

    Users user;
    Intent intent;

    // Fab 버튼 사용할 때 필요한 변수들
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    FloatingActionButton fab_music,fab_addfriend, fab_RFF;


    public Fragment4() {

        // Required empty public constructor
    }

    public Fragment4(Users user) {
        this.user = user;
        // Required empty public constructor
    }

    public static com.example.madcamp2nd.Fragment4 newInstance(String param1, String param2) {
        com.example.madcamp2nd.Fragment4 fragment = new com.example.madcamp2nd.Fragment4();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_4, container, false);

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab_music = view.findViewById(R.id.floatingActionButton_music);
        fab_addfriend = view.findViewById(R.id.floatingActionButton_addfriend);
        fab_RFF = view.findViewById(R.id.floatingActionButton_RecFromFriend);


        getMusicList(); // 디바이스 안에 있는 mp3 파일 리스트를 조회하여 LIst를 만듭니다.
        listView = (ListView) view.findViewById(R.id.listview);
        MusicListAdapter adapter = new MusicListAdapter(getActivity(), list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MusicActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("playlist", list);
                intent.putExtra("user",user);
                System.out.println("before starting activity");
                startActivity(intent);
                System.out.println("after starting activity");
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab_music.setOnClickListener(this);
        fab_addfriend.setOnClickListener(this);
        fab_RFF.setOnClickListener(this);
    }
    public void getMusicList() {
        list = new ArrayList<>();
        //가져오고 싶은 컬럼 명을 나열합니다. 음악의 아이디, 앰블럼 아이디, 제목, 아티스트 정보를 가져옵니다.
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        while (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ;

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        //    Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        //            null, null, null, null);
        //    System.out.println("here");
        System.out.println("cursor.get_count(): " + cursor.getCount());
        while (cursor.moveToNext()) {
            MusicDto musicDto = new MusicDto();
            musicDto.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicDto.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicDto.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicDto.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            list.add(musicDto);
            System.out.println(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            System.out.println(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        }
        cursor.close();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton_music: // Fab 버튼 닫기, 열기
                Log.e("Fragment_music", "touch");
                toggleFab();
                // 동기화 작업, db에서 받고, 띄워줘야함.
                break;
            case R.id.floatingActionButton_addfriend: // friendmaker 액티비티 실행
                Log.e("Fragment_music", "addfr");
                toggleFab();
                intent = new Intent(getActivity().getApplicationContext(), FriendMaker.class);
                intent.putExtra("user", user);
                startActivity(intent);

                break;
            case R.id.floatingActionButton_RecFromFriend:
                Log.e("Fragment_music", "RFF");
                toggleFab();
                intent = new Intent(getActivity().getApplicationContext(), RecommendList.class);
                intent.putExtra("user", user);
                startActivity(intent);

        }
    }

    private void toggleFab() {
        Log.e("Fragment_Images", "toggleFab");
        if (isFabOpen) {
            fab_music.setImageResource(R.drawable.icon_plus);
            fab_addfriend.startAnimation(fab_close);
            fab_addfriend.setClickable(false);
            fab_RFF.startAnimation(fab_close);
            fab_RFF.setClickable(false);
            isFabOpen = false;
        } else {
            fab_music.setImageResource(R.drawable.icon_x);
            fab_addfriend.startAnimation(fab_open);
            fab_addfriend.setClickable(true);
            fab_RFF.startAnimation(fab_open);
            fab_RFF.setClickable(true);
            isFabOpen = true;
        }
    }
}


