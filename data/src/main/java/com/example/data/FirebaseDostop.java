package com.example.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FirebaseDostop {
    private String ime_paketnika;
    private double positionLat;
    private double positionLong;
    private LocalDateTime dateTime;



    public FirebaseDostop(String name, double PosLat, double PosLong, LocalDateTime dt){
        this.ime_paketnika=name;
        this.positionLat =PosLat;
        this.positionLong =PosLong;
        this.dateTime=dt;

    }

    public double getPositionLat() {
        return positionLat;
    }

    public double getPositionLong() {
        return positionLong;
    }

    public String getIme_paketnika() {
        return ime_paketnika;
    }

    public void setIme_paketnika(String ime_paketnika) {
        this.ime_paketnika = ime_paketnika;
    }

    public void setPositionLat(double positionLat) {
        this.positionLat = positionLat;
    }

    public void setPositionLong(double positionLong) {
        this.positionLong = positionLong;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
