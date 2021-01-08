package com.example.madcamp1st;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends FragmentActivity {
    private final int REQUEST_CODE_BOTH = 0;
    private final int REQUEST_CODE_READ_CONTACTS = 1;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.madcamp1st.R.layout.activity_main);

        boolean permissionReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!permissionReadContacts && !permissionReadExternalStorage)
            requestPermissions(new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_BOTH);
        else if(!permissionReadContacts)
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        else if(!permissionReadExternalStorage)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        else
            createView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_BOTH || requestCode == REQUEST_CODE_READ_CONTACTS || requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE)
            createView();
    }

    private void createView() {
        ViewPager2 mViewPager = findViewById(R.id.viewPager_main);
        SectionPageAdapter adapter = new SectionPageAdapter(this);

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