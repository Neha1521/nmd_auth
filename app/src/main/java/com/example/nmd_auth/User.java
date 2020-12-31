package com.example.nmd_auth;

public class User {

    public String sUsername, sPassword, sMail, sPhone, sLoc1, sLoc2, sLoc3;

    public User(){

    }

    public User(String sUsername, String sPassword, String sMail, String sPhone){
        this.sUsername = sUsername;
        this.sPassword = sPassword;
        this.sMail = sMail;
        this.sPhone = sPhone;
    }

    public User(String sLoc1, String sLoc2, String sLoc3){
        this.sLoc1 = sLoc1;
        this.sLoc2 = sLoc2;
        this.sLoc3 = sLoc3;
    }


}
