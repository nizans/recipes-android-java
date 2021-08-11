package com.example.barcodeshop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.barcodeshop.R;
import com.example.barcodeshop.adapters.NutValueAdapter;
import com.example.barcodeshop.adapters.RecipeScreenProductsAdapter;
import com.example.barcodeshop.models.Product;
import com.example.barcodeshop.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeScreenActivity extends AppCompatActivity {

    GridView gvRecipeProducts;
    ImageView imgvRecipeScreenImg;
    TextView tvName, tvDesc;
    Button btnIngredients;
    ArrayList<Product> products;
    Recipe recipe;
    RecipeScreenProductsAdapter recipeScreenProductsAdapter;
    ListView lvNutValues;
    NutValueAdapter nutValueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_screen);
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        products = recipe.getProducts();
        gvRecipeProducts = findViewById(R.id.gvRecipeProducts);
        tvName = findViewById(R.id.tvRecipeScreenName);
        tvDesc = findViewById(R.id.tvRecipeScreenDesc);
        imgvRecipeScreenImg = findViewById(R.id.imgvRecipeScreenImg);
        btnIngredients = findViewById(R.id.btnIngredients);
        lvNutValues = findViewById(R.id.lvNutValues);
        nutValueAdapter = new NutValueAdapter(this, R.layout.nut_value_row, recipe.getNutValuesArrayList());
        lvNutValues.setAdapter(nutValueAdapter);
        setListViewHeight(lvNutValues);
        Picasso.get()
                .load(recipe.getImage())
                .placeholder(R.drawable.ic_baseline_broken_image_24)
                .error(R.drawable.ic_baseline_broken_image_24)
                .fit()
                .centerCrop()
                .into(imgvRecipeScreenImg);

        tvName.setText(recipe.getName());
        tvDesc.setText(recipe.getDescription());
        recipeScreenProductsAdapter = new RecipeScreenProductsAdapter(this, R.layout.recipe_grid_item, products);
        gvRecipeProducts.setAdapter(recipeScreenProductsAdapter);
        hideGridView(gvRecipeProducts);
        Log.d("TAG", "onCreate: "+ gvRecipeProducts.getMeasuredHeight());
        recipeScreenProductsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setGridViewHeight(gvRecipeProducts);
            }
        });
        btnIngredients.setOnClickListener(v -> {
            if (gvRecipeProducts.getVisibility() == View.VISIBLE){
                hideGridView(gvRecipeProducts);
                btnIngredients.setText("Show ingredients");
            } else {
                setGridViewHeight(gvRecipeProducts);
                btnIngredients.setText("Hide ingredients");
            }
        });


    }
    private void setGridViewHeight(GridView gridview) {
        if (recipeScreenProductsAdapter.isEmpty()) {
            return;
        }
        gridview.setVisibility(View.VISIBLE);
        int numColumns= gridview.getNumColumns();
        Log.d("TAG", "setGridViewHeight: " + numColumns);
        int totalHeight = 0;
        for (int i = 0; i < recipeScreenProductsAdapter.getCount(); i += 3) {
            View listItem = recipeScreenProductsAdapter.getView(i, null, gridview);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            if (i == 0){
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        params.height = totalHeight;
        gridview.setLayoutParams(params);
    }
    private void setListViewHeight(ListView listView) {
        if (nutValueAdapter.isEmpty()) {
            return;
        }
        listView.setVisibility(View.VISIBLE);
        int totalHeight = 0;
        for (int i = 0; i < nutValueAdapter.getCount(); i++) {
            View listItem = nutValueAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            if (i == 0){
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
    private void hideGridView(GridView gridview){
        gridview.setVisibility(View.GONE);
    }

}