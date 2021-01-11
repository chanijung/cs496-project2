package com.example.madcamp2nd;

import android.util.Log;

import com.example.madcamp2nd.contacts.Contact;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {

//    private boolean isRight = false;
    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("contacts")
    @Expose
    private List<List<String>> contacts;
    List<String> temp;
    public Users (String uid) {
        this.uid = uid;
    }
    public Users (String uid,  List<List<String>> contacts) {
        this.uid = uid;
        this.contacts = contacts;
    }
    public Users (String uid, Contact[] contacts) {
        this.uid = uid;
        this.contacts = new ArrayList<List<String>>();

        for (int x = 0; x < contacts.length; x++) {
            temp = new ArrayList<String>();
            temp.add(contacts[x].name);
            temp.add(contacts[x].number);
            this.contacts.add(temp);
        }
//        this.contact = new ArrayList<Contact>();
//        Log.e("tag", "in user");
//        for (int x = 0; x < contacts.length; x++) {
//
//            temp.name = contacts[x].name;
//            temp.number =contacts[x].number;
//            this.contact.add(temp);
//        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<List<String>> getContacts() {
        return contacts;
    }

    public void setContacts(List<List<String>> contacts) {
        this.contacts = contacts;
    }


//    public boolean isRight() {
//        return isRight;
//    }
//
//    public void setRight(boolean right) {
//        isRight = right;
//    }
}
