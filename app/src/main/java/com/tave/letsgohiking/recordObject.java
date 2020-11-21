package com.tave.letsgohiking;

public class recordObject {

    String date;
    String title;
    String length;
    String time;
    String velocity;

    public recordObject(String date, String title, String length, String time, String velocity){
        this.date=date;
        this.title=title;
        this.length=length;
        this.time=time;
        this.velocity=velocity;
    }


    public String getDate() {return date;}
    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLength() {
        return length;
    }
    public void setLength(String length) {
        this.length = length;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getVelocity() {
        return velocity;
    }
    public void setVelocity(String velocity) {
        this.velocity = velocity;
    }
}
