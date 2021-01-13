package com.example.madcamp2nd.contacts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madcamp2nd.MainActivity;
import com.example.madcamp2nd.R;
import com.example.madcamp2nd.RetrofitAPI;
import com.example.madcamp2nd.RetrofitClient;
import com.example.madcamp2nd.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Contacts extends Fragment implements View.OnClickListener {
    private View mView;
    private ContactAdapter mAdapter;

    // 권한 받을 때 필요한 변수들
    private final int REQUEST_CODE_BOTH = 0;
    private final int REQUEST_CODE_READ_CONTACTS = 1;
    private final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;

    // Fab 버튼 사용할 때 필요한 변수들
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    FloatingActionButton fab_contacts,fab_synchronization;

    Users user;
    Call<Users> call;


    Contact[] phone_contacts; // 핸드폰 내에 있는 연락처 가져올때
    Contact[] db_contacts; // db에서 연락처 가져올 때
    List<List<String>> temp_number;
    ArrayList<Contact> temp_contacts;
    RetrofitAPI apiInterface;

    public Fragment_Contacts(Users user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contacts, container, false);

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab_contacts = mView.findViewById(R.id.floatingActionButton_contacts);
        fab_synchronization = mView.findViewById(R.id.floatingActionButton_synchronization_contacts);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        temp_number = user.getContacts();
        temp_contacts = new ArrayList<>();
        for (int x = 0; x < temp_number.size(); x++) {
            temp_contacts.add(new Contact(temp_number.get(x).get(0), temp_number.get(x).get(1)));
        }
        db_contacts = temp_contacts.toArray(new Contact[0]);

        onPermissionGranted_contacts(db_contacts);

        fab_contacts.setOnClickListener(this);
        fab_synchronization.setOnClickListener(this);

    }

    private void onPermissionGranted_contacts(Contact[] contactforshow) {
        showContacts(contactforshow); // DB에서 받아온 연락처를 띄워줌

        SearchView searchView = mView.findViewById(R.id.searchView_contacts);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });
    }

    private Contact[] loadContacts() {
        ProgressDialog pd;
        ArrayList<Contact> contacts = new ArrayList<>();

        pd = ProgressDialog.show(getContext(), "Loading Contacts", "Please Wait");

        Cursor c = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null, null, null);

        int nameColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int numberColumn = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while (c.moveToNext()) {
            String contactName = c.getString(nameColumn);
            String phNumber = c.getString(numberColumn);

            contacts.add(new Contact(contactName, phNumber));
        }
        c.close();
        pd.cancel();

        return contacts.toArray(new Contact[0]);
    }

    private void showContacts(Contact[] contacts) {

        RecyclerView recyclerView = mView.findViewById(R.id.recyclerView_contacts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ContactAdapter(contacts);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton_contacts: // Fab 버튼 닫기, 열기
                toggleFab();
                // 동기화 작업, db에서 받고, 띄워줘야함.
                break;
            case R.id.floatingActionButton_synchronization_contacts: // 동기화 버튼 누르면!
                boolean permissionReadContacts = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
                boolean permissionReadExternalStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        
                if(!permissionReadContacts && !permissionReadExternalStorage)
                    requestPermissions(new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_BOTH);
                else if(!permissionReadContacts)
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
                else if(!permissionReadExternalStorage)
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                else {
                        apiInterface = RetrofitClient.getApiService();
                        phone_contacts = loadContacts();
                        user = new Users(user.getUid(), phone_contacts);
                        Log.e("2", user.getUid());
                        call = apiInterface.contactsave(user);
                        temp_number = null;

                        call.enqueue(new Callback<Users>() {
                            @Override
                            public void onResponse(Call<Users> call, Response<Users> response) {
                                temp_number = response.body().getContacts();
                                user.setContacts(temp_number);
                                // List<List<String>> 을 Contact[]로 바꾸는 과정
                                temp_contacts = new ArrayList<>();
                                for (int x = 0; x < temp_number.size(); x++) {
                                    temp_contacts.add(new Contact( temp_number.get(x).get(0), temp_number.get(x).get(1)));
                                }
                                db_contacts = temp_contacts.toArray(new Contact[0]);
                                showContacts(db_contacts);
                            }
                            @Override
                            public void onFailure(Call<Users> call, Throwable t) {
                            }
                        });
                    }
//                }
                toggleFab();

                break;
        }
    }
    private void toggleFab() {
        if (isFabOpen) {
            fab_contacts.setImageResource(R.drawable.icon_plus);
            fab_synchronization.startAnimation(fab_close);
            fab_synchronization.setClickable(false);
            isFabOpen = false;
        } else {
            fab_contacts.setImageResource(R.drawable.icon_x);
            fab_synchronization.startAnimation(fab_open);
            fab_synchronization.setClickable(true);
            isFabOpen = true;
        }
    }
}