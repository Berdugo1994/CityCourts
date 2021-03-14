package com.example.mycitycourts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterHelperClass {
    String username;
    String password;
    String email;
    String Birthdate;
    int level;
    public RegisterHelperClass() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RegisterHelperClass(String username, String password, String email, String birthdate, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        Birthdate = birthdate;
        this.phone = phone;
        this.level =0;
        this.date= new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;
}