package com.example.barcodeshop.Test;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.barcodeshop.activities.addRecipe.AddDescriptionActivity;
import com.example.barcodeshop.helpers.ApiCalls;
import com.example.barcodeshop.models.Product;
import com.example.barcodeshop.models.Recipe;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.barcodeshop.helpers.StaticCodes.RQ_NEXT_ACTIVITY;

public class RandomContentGenerator {





    public static void uploadRandomRecipeToDB(Context context, Response.Listener<String> listener){
        ApiCalls.makeRandomRecipeRequst(context, listener,
                error -> {
                    Log.d("TAG", "Error fetching recipe");
                    error.printStackTrace();
                },ThreadLocalRandom.current().nextInt(1, 7000 + 1));
    }

    public static void pushRandomRecipeToDb(Context context, String result){
        JSONObject jsonObject;
        JSONArray jsonArray;
        String recipeName;
        String description;
        String imageUri;
        Recipe newRecipe;
        ArrayList<Product> products = new ArrayList<>();

        try {
            jsonObject = new JSONObject(result);
            recipeName = jsonObject.getString("title");
            description = jsonObject.getString("summary").replaceAll("<[^>]*>","");
            imageUri = jsonObject.getString("image");
            jsonArray = jsonObject.getJSONArray("extendedIngredients");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject productJson = jsonArray.getJSONObject(i);
                Product p = new Product();
                p.setName(productJson.getString("nameClean"));
                p.setAmount(productJson.getString("amount"));
                p.setUnitName(productJson.getString("unit"));
                products.add(p);
            }

            newRecipe = new Recipe(products, recipeName, description);
            newRecipe.setImage(imageUri);

            ApiCalls.makeNutritionValuesRequest(context, Recipe.createProductsJSON(products), response -> {
                try{
                    JSONObject respObj = new JSONObject(response);
                    Log.d("TAG", "onCreate: RESPONSE " + respObj.toString());
                    JSONArray productsArray = respObj.getJSONArray("foods");
                    for (int i = 0; i < productsArray.length(); i++) {
                        products.get(i).setSerializedJSON(productsArray.getJSONObject(i).toString());
                    }
                    saveRecipe(newRecipe);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void saveRecipe(Recipe recipe) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://nitzan-android-final-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference pushedRef = mDatabase.child("users_recipes").child(userId).push();
        String recipeID = pushedRef.getKey();
        pushedRef.setValue(recipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("TAG", "RandomRecipe: Successful");
            } else {
                Log.d("TAG", "RandomRecipe: Faild");
            }
        });
    }




}
