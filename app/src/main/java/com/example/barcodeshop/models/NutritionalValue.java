package com.example.barcodeshop.models;

public class NutritionalValue {

    private String name;
    private int amount;
    private int perc;
    NutritionalValue(String name, int amount, int perc){
        this.name = name;
        this.amount = amount;
        this.perc = perc;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAmount(int amount) {
        this.amount += amount;
    }


    public int calPercentageFromTotalCal(int totalCals){

        return 0;
    }
    @Override
    public String toString() {
        return this.name + ": " + this.amount + "g | " + getPerc()+"%";
    }

    public int getPerc() {
        return perc;
    }

    public void setPerc(int perc) {
        this.perc = perc;
    }
}
