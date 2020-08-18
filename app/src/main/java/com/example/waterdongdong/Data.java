package com.example.waterdongdong;

public class Data {

    public String category;
    public String d_name;
    public int temp;
    public int intake;

    public Data(){

    }

    public Data(String category, String d_name, int temp, int intake) {
        this.category = category;
        this.d_name = d_name;
        this.temp = temp;
        this.intake = intake;
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

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getIntake() {
        return intake;
    }

    public void setIntake(int intake) {
        this.intake = intake;
    }

    @Override
    public String toString() {
        return "Data{" +
                "catagory='" + category + '\'' +
                ", d_name='" + d_name + '\'' +
                ", temp='" + temp + '\'' +
                ", intake='" + intake + '\'' +
                '}';
    }
}
