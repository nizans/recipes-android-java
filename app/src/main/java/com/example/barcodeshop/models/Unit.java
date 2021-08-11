package com.example.barcodeshop.models;


import java.io.Serializable;

public class Unit implements Serializable {

    public enum UnitsENUM {

        grams(new Unit("grams",0,1000,0,50)),
        units(new Unit("unit", 0,50,1,1)),
        ml(new Unit("ml", 0, 1000, 0, 50)),
        liters(new Unit("liters", 0, 20, 0, 1)),
        cup(new Unit("cup", 0 , 10, 0, 1)),
        ounces(new Unit("ounces", 0, 10, 0, 1)),
        tbsp(new Unit("tbsp", 0, 10, 0, 1));
        private final Unit unit;
        UnitsENUM(Unit unit) {
            this.unit = unit;
        }
        public Unit getUnit() {
            return unit;
        }
    }

    private  String name;
    private  float valFrom;
    private  float valTo;
    private  float defaultVal;
    private  int step;

    public String getName() {
        return name;
    }
    public float getValFrom() {
        return valFrom;
    }
    public float getValTo() {
        return valTo;
    }
    public float getDefaultVal() {
        return defaultVal;
    }
    public int getStep() {
        return step;
    }

    public Unit(){}
    public Unit(String name, float valFrom, float valTo, float defaultVal, int step) {
        this.name = name;
        this.valFrom = valFrom;
        this.valTo = valTo;
        this.defaultVal = defaultVal;
        this.step = step;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "name='" + name + '\'' +
                ", valFrom=" + valFrom +
                ", valTo=" + valTo +
                ", defaultVal=" + defaultVal +
                ", step=" + step +
                '}';
    }
}
