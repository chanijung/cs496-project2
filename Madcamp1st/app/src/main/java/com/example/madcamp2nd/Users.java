package com.example.madcamp2nd;

import android.util.Log;

import com.example.madcamp2nd.contacts.Contact;
import com.example.madcamp2nd.images.Image;
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

    @SerializedName("gallery")
    @Expose
    private List<String> gallery;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("profile")
    @Expose
    private String profile;

    @SerializedName("profiles")
    @Expose
    private List<String> profiles;

    @SerializedName("relation")
    @Expose
    private List<String> relation;

    @SerializedName("myfriends")
    @Expose
    private List<String> myfriends;

    //Constructor
    public Users(){
    }
    public Users(String uid) {
        this.uid = uid;
    }

    public Users(String uid, List<List<String>> contacts) {
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

    public List<String> getGallery() {
        return gallery;
    }

    public void setGallery(List<String> gallery) {
        this.gallery = gallery;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public List<String> getRelation() {
        return relation;
    }

    public void setRelation(List<String> relation) {
        this.relation = relation;
    }

    public List<String> getMyfriends() {
        return myfriends;
    }

    public void setMyfriends(List<String> myfriends) {
        this.myfriends = myfriends;
    }

//    public boolean isRight() {
//        return isRight;
//    }
//
//    public void setRight(boolean right) {
//        isRight = right;
//    }
}
