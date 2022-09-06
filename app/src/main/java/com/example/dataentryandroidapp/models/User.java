package com.example.dataentryandroidapp.models;

public class User {

    private String id;
    private String fullname;
    private String email;
    private String pasword;
    private String type;
    private String adminId;

    public User(){

    }

    public User(String id, String fullname, String email, String pasword,String type,String adminId) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.pasword = pasword;
        this.type = type;
        this.adminId = adminId;
    }

    public User(String id, String email, String pasword, String type,String adminId) {
        this.id = id;
        this.email = email;
        this.pasword = pasword;
        this.type = type;
        this.adminId = adminId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasword() {
        return pasword;
    }

    public void setPasword(String pasword) {
        this.pasword = pasword;
    }
}
