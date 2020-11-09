package com.example.waterdongdong;

public class Data {

    public String category;
    public String d_name;
    public int cal;
    public String time;
    public String weekDay;
    public int cnt;

    public Data(){

    }

    public Data(String category, String d_name, int cal, String time, String weekDay, int cnt) {
        this.category = category;
        this.d_name = d_name;
        this.cal = cal;
        this.time = time;
        this.weekDay = weekDay;
        this.cnt = cnt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getD_name() {
        return d_name;
    }

    public void setD_name(String d_name) {
        this.d_name = d_name;
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
