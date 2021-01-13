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

// LoginActivity부터 시작.
public class MainActivity extends FragmentActivity {
    Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.madcamp2nd.R.layout.activity_main);

        user = (Users) getIntent().getSerializableExtra("user");
        createView();
    }

    private void createView() {
        ViewPager2 mViewPager = findViewById(R.id.viewPager_main);
        SectionPageAdapter adapter = new SectionPageAdapter(this, user);

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout_main);
        new TabLayoutMediator(tabLayout, mViewPager, (tab, position) -> {
            ImageView imgView = new ImageView(this);
            switch (position) {
                case 0:
                    imgView.setImageResource(R.drawable.contact);
                    break;
                case 1:
                    imgView.setImageResource(R.drawable.gallery);
                    break;
                case 2:
                    imgView.setImageResource(R.drawable.music);
            }
            imgView.setPadding(5, 5, 5, 5);
            tab.setCustomView(imgView);
        }).attach();

    }

}