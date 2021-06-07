package com.example.data;

import java.time.LocalDateTime;

public class FirebaseLogin {
    private String username;
    private String password;
    private LocalDateTime dateTime;

    public FirebaseLogin(String username, String password, LocalDateTime dateTime){
        this.username=username;
        this.password=password;
        this.dateTime=dateTime;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
