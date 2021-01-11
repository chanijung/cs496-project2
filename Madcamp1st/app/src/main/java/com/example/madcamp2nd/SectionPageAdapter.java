package com.example.madcamp2nd;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.madcamp2nd.contacts.Contact;
import com.example.madcamp2nd.contacts.Fragment_Contacts;
import com.example.madcamp2nd.games.Fragment_Games;
import com.example.madcamp2nd.images.Fragment_Images;

public class SectionPageAdapter extends FragmentStateAdapter {
    Users user;

    public SectionPageAdapter(FragmentActivity fa, Users user) {
        super(fa);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new Fragment_Contacts(user);
            case 1:
                return new Fragment_Images(user);
            case 2:
                return new Fragment_Games();
        }

        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}