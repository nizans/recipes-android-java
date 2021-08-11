package com.example.barcodeshop.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.barcodeshop.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiCalls {
    private static ApiCalls mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    public ApiCalls(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ApiCalls getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiCalls(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public static void makeAutoCompleteRequest(Context context,
                                               String query,
                                               Response.Listener<String> listener,
                                               Response.ErrorListener errorListener) {
        String url = "https://trackapi.nutritionix.com/v2/search/instant?query=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String,String> params = new HashMap<>();
                params.put("x-app-id", "d438a0d4");
                params.put("x-app-key", "4a591b50433008a07b3c9d7e5296cc64");
                params.put("x-remote-user-id", "0");
                return params;
            }};
        ApiCalls.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void makeNutritionValuesRequest(Context context,
                                                  JSONObject jsonObject,
                                                  Response.Listener<String> listener,
                                                  Response.ErrorListener errorListener) {
        String url = "https://trackapi.nutritionix.com/v2/natural/nutrients";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return jsonObject == null ? null : jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders()  {
                Map<String,String> params = new HashMap<>();
                params.put("x-app-id", "d438a0d4");
                params.put("x-app-key", "4a591b50433008a07b3c9d7e5296cc64");
                params.put("x-remote-user-id", "0");
                return params;
            }
        };
        ApiCalls.getInstance(context).addToRequestQueue(stringRequest);

    }


    public static void makeRandomRecipeRequst(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener, int id){
        Log.d("TAG", "makeRandomRecipeRequst: "+ id);
        final String RAND_RECIPE_URL = "https://api.spoonacular.com/recipes/"+id+"/information?apiKey=4f6fccce4bd1435c9811006cff941286&includeNutrition=false";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, RAND_RECIPE_URL, listener, errorListener);
        ApiCalls.getInstance(context).addToRequestQueue(stringRequest);

    }
}



