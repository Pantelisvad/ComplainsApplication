package com.example.asfalitoapp;

public class Report {
    private String desc;
    private String url;
    private double latitude;
    private double longtitude;

    public Report(String desc, String url, double latitude, double longtitude) {
        this.desc = desc;
        this.url = url;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
