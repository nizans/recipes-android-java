package com.example.barcodeshop.models;
import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.example.barcodeshop.models.Unit.UnitsENUM;

public class Product implements Serializable {
    private String name;
    private String amount;
    private transient Unit unit;
    private String unitName;
    private String serializedJSON;


    public Product(){
        setUnit(UnitsENUM.grams.getUnit());
        this.amount = String.valueOf(unit.getDefaultVal());
        this.name = "";
    }

    public Product(String name, String amount, Unit unit){
        this.name = name;
        this.amount = amount;
        setUnit(unit);
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAmount() {
        return this.amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Exclude
    public Unit getUnit() {
        return this.unit;
    }
    @Exclude
    public void setUnit(Unit unit) {
        this.unit = unit;
        setUnitName(unit.getName());
    }

    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSerializedJSON() {
        return this.serializedJSON;
    }
    public void setSerializedJSON(String serializedJSON) {
        this.serializedJSON = serializedJSON;

    }

    @Override
    public @NotNull String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", amount='" + amount + '\'' +
                ", unit=" + unit +
                '}';
    }

    @Exclude
    public JSONObject getJsonObject(){
        if (this.serializedJSON == null){
            return  null;
        }
        try {
            return new JSONObject(this.serializedJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
