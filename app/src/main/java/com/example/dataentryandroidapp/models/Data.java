package com.example.dataentryandroidapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable {

    private String id;
    private String fullname;
    private String location;
    private String phone;
    private String notes;

    public Data(){

    }

    public Data(String id, String fullname, String phone, String location,String notes) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.location = location;
        this.notes = notes;
    }

    protected Data(Parcel in) {
        id = in.readString();
        fullname = in.readString();
        location = in.readString();
        phone = in.readString();
        notes = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(fullname);
        parcel.writeString(location);
        parcel.writeString(phone);
        parcel.writeString(notes);
    }
}
