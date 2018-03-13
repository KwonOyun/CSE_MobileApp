package com.example.oyun.cse;

/**
 * Created by oyun on 2018-02-03.
 */

public class Information {

    String title;
    String number;
    String writer;
    String time;
    String url;

    public Information(String title, String number, String writer, String time, String url){
        this.title = title;
        this.number = number;
        this.writer = writer;
        this.time = time;
        this.url = url;
    }

    public String getUrl() {return url;}

    public String getTitle() {
        return title;
    }

    public String getNumber() {
        return number;
    }

    public String getWriter() {
        return writer;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUrl(String url) {this.url = url;}
}
