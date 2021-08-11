package com.example.barcodeshop.activities.addRecipe;

import android.app.ActionBar;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.barcodeshop.R;
import com.example.barcodeshop.adapters.ProductListAdapter;
import com.example.barcodeshop.dialogs.NewProductDialog;
import com.example.barcodeshop.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.barcodeshop.R.layout.activity_add_products;
import static com.example.barcodeshop.helpers.StaticCodes.RESULT_ERROR;
import static com.example.barcodeshop.helpers.StaticCodes.RQ_NEXT_ACTIVITY;


public class AddProductsActivity extends AppCompatActivity {


    ListView lvProducts;
    FloatingActionButton fabAddProduct;
    FloatingActionButton fabNext;
    ProductListAdapter productListAdapter;
    ArrayList<Product> products;
    Toolbar toolbar;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_products);
        products = new ArrayList<>();
        lvProducts = findViewById(R.id.lvProducts);
        fabAddProduct = findViewById(R.id.fabAddProuct);
        fabNext = findViewById(R.id.fabNext);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("Choose Products");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        productListAdapter = new ProductListAdapter(this, R.layout.product_row, products);
        lvProducts.setAdapter(productListAdapter);
        productListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                toggleNextBtn(productListAdapter.getCount() > 0);
            }

            private void toggleNextBtn(boolean enable) {
                fabNext.setEnabled(enable);
            }

        });

        fabAddProduct.setOnClickListener(v -> {
            //products.add(new Product());
            NewProductDialog dialog = new NewProductDialog(this, value -> {
                Product p = new Product();
                p.setName(value);
                products.add(p);
                productListAdapter.notifyDataSetChanged();
            });

            int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);

            dialog.show(getSupportFragmentManager(), "SHOW");

        });

        fabNext.setOnClickListener(v -> {
            Intent intent =  new Intent(this, AddDescriptionActivity.class);
            intent.putExtra("products", products);
            //noinspection deprecation
            startActivityForResult(intent, RQ_NEXT_ACTIVITY);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_NEXT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                finish();
            }
            if (resultCode == RESULT_ERROR){
                Toast.makeText(this, "Error fetching data. Please provide correct product names.", Toast.LENGTH_LONG).show();
            }

        }
    }
}