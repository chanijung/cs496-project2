package com.example.madcamp2nd;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.madcamp2nd.contacts.Contact;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;


public class MainActivity extends FragmentActivity {
//    private final int REQUEST_CODE_BOTH = 0;
//    private final int REQUEST_CODE_READ_CONTACTS = 1;
//    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;

    Users user;

    private EditText userId, userPwd;
    private long lastTimeBackPressed;
    private Context mContext;

//    private LoginButton btn_Facebook_Login;
//    private Button btn_custom_login;
//    private CallbackManager callbackManager;
//    private LoginCallback loginCallack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.madcamp2nd.R.layout.activity_main);

        user = (Users) getIntent().getSerializableExtra("user");
        Log.e("tag", "dddd" + user.getUid());
        createView();

//        callbackManager = CallbackManager.Factory.create();
//        loginCallack = new LoginCallback();
//
//        btn_custom_login = (Button) findViewById(R.id.btn_custom_login);
//        btn_custom_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoginManager loginManager = LoginManager.getInstance();
//                loginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile","email"));
//                loginManager.registerCallback(callbackManager, loginCallack);
//            }
//        });


//        boolean permissionReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
//        boolean permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//
//        if(!permissionReadContacts && !permissionReadExternalStorage)
//            requestPermissions(new String[]{
//                            Manifest.permission.READ_CONTACTS,
//                            Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_CODE_BOTH);
//        else if(!permissionReadContacts)
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
//        else if(!permissionReadExternalStorage)
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
//        else
//            createView();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_CODE_BOTH || requestCode == REQUEST_CODE_READ_CONTACTS || requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE)
//            createView();
//    }

    private void createView() {
        ViewPager2 mViewPager = findViewById(R.id.viewPager_main);
        SectionPageAdapter adapter = new SectionPageAdapter(this, user);

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout_main);
        new TabLayoutMediator(tabLayout, mViewPager, (tab, position) -> {
            ImageView imgView = new ImageView(this);
            switch (position) {
                case 0:
                    imgView.setImageResource(R.drawable.tab_icon_contacts);
                    break;
                case 1:
                    imgView.setImageResource(R.drawable.tab_icon_images);
                    break;
                case 2:
                    imgView.setImageResource(R.drawable.tab_icon_games);
            }
            imgView.setPadding(10, 10, 10, 10);
            tab.setCustomView(imgView);
        }).attach();

    }

}