package com.example.barcodeshop.helpers;

import java.util.LinkedHashMap;

public class NutritionConstents {

    public static final String CALORIES = "nf_calories";
    public static final String TOTAL_FATS = "nf_total_fat";
    public static final String TOTAL_CARBS = "nf_total_carbohydrate";
    public static final String SUGARS = "nf_sugars";
    public static final String PROTEINS = "nf_protein";

    public static LinkedHashMap<String, String> getHashMap(){
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("Calories", "nf_calories");
        hashMap.put("Proteins", "nf_protein");
        hashMap.put("Sugars", "nf_sugars");
        hashMap.put("Total Carbs", "nf_total_carbohydrate");
        hashMap.put("Total Fats", "nf_total_fat");
        return hashMap;
    }


}
