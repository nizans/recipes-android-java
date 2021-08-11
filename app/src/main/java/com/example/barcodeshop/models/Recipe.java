package com.example.barcodeshop.models;

import android.util.Log;

import com.example.barcodeshop.helpers.NutritionConstents;
import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


public class Recipe implements Serializable {

    private ArrayList<Product> products;
    private String name;
    private String description;
    private String imageSRC;
    private String id;
    public Recipe(){}
    public Recipe(ArrayList<Product> products, String recpName, String recpDesc) {
        this.products = products;
        this.name = recpName;
        this.description = recpDesc;
        this.imageSRC = null;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage() {
        return imageSRC;
    }
    public void setImage(String imageUri) {
        this.imageSRC = imageUri;
    }
    public static JSONObject createProductsJSON(ArrayList<Product> products){
        JSONObject jsonObject = new JSONObject();
        StringBuilder query = new StringBuilder();
        for (Product p : products) {
            query.append(p.getAmount()).append(" ").append(p.getUnitName()).append(" ").append(p.getName()).append(" ");
        }
        try {
            jsonObject.put("query", query.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public float getTotalRecipreNutValueByName(String name){
        float val = 0;
        for (Product p : this.products){
            try {
                JSONObject nut = p.getJsonObject();
                String amount = nut.getString(name);
                if (!amount.equals("null")){
                    val += Float.parseFloat(amount);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
                Log.d("TAG", "getNutritionValue: NoSertilizedJSON");
            }
        }
        return val;
    }

    @Exclude
    public int getTotalCals(){
        return (int)getTotalRecipreNutValueByName(NutritionConstents.CALORIES);
    }

    @Exclude
    public ArrayList<NutritionalValue> getNutValuesArrayList(){
        ArrayList<NutritionalValue> nutritionalValues = new ArrayList<>();
        int totalCals = getTotalCals();
        for (Map.Entry<String, String> entry : NutritionConstents.getHashMap().entrySet()) {
            String name = entry.getKey();
            String apiName = entry.getValue();
            int amount = (int)getTotalRecipreNutValueByName(apiName);
            int perc = calculatePercentageFromCals(totalCals, apiName, amount);
            NutritionalValue nv = new NutritionalValue(name, amount, perc);
            nutritionalValues.add(nv);
        }
        return nutritionalValues;
    }

    private int calculatePercentageFromCals(int totalCals, String nutType, int amount) {
        if (nutType == NutritionConstents.CALORIES){
            return 100;
        }
        double percFromTotalCals;
        Log.d("TAG", "calculatePercentageFromCals: "+ nutType +" "+ amount+ " "+ totalCals);
        //Proteins and carbs -- 1gram = 4cals
        if(nutType.equals(NutritionConstents.PROTEINS) || nutType.equals(NutritionConstents.TOTAL_CARBS)){
            percFromTotalCals = (double)(amount * 4) / (double)totalCals;
            Log.d("TAG", "prot or carb: " + percFromTotalCals*100);
            return (int) (percFromTotalCals * 100);
        }

        //Fats -- 1gram = 9cals
        if (nutType.equals(NutritionConstents.TOTAL_FATS)){
            percFromTotalCals = (double)(amount * 9) / (double)totalCals;
            Log.d("TAG", "fat: " + (percFromTotalCals*100));
            return (int) (percFromTotalCals * 100);
        }
        Log.d("TAG", "0");

        return 0;
    }

    @Override
    public @NotNull String toString() {
        return "Recipe{" +
                "products=" + products +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageSRC='" + imageSRC + '\'' +
                '}';
    }
}
