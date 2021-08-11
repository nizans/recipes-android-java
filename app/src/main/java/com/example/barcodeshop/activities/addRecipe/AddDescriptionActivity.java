package com.example.barcodeshop.activities.addRecipe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.barcodeshop.dialogs.LoadingDialog;
import com.example.barcodeshop.R;
import com.example.barcodeshop.activities.CameraActivity;
import com.example.barcodeshop.helpers.ApiCalls;
import com.example.barcodeshop.helpers.ImageRotationDetectionHelper;
import com.example.barcodeshop.helpers.Uri2PathUtil;
import com.example.barcodeshop.models.Product;
import com.example.barcodeshop.models.Recipe;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.barcodeshop.helpers.StaticCodes.RESULT_ERROR;
import static com.example.barcodeshop.helpers.StaticCodes.RQ_NEXT_ACTIVITY;

public class AddDescriptionActivity extends AppCompatActivity {

    private static final int RQ_IMAGE_PICKER = 103;
    private static final int RQ_IMAGE_CAPTURE = 104;

    EditText etRecipeName, etRecipeDesc;
    Button btnOpenGallery, btnOpenCamera;
    ImageView imageView;
    Toolbar toolbar;
    Uri imageUri;
    ArrayList<Product> products;
    ExtendedFloatingActionButton efabSave;
    DatabaseReference mDatabase;
    String userId;
    LoadingDialog loadingDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);
        loadingDialog = new LoadingDialog(this);
        initActivity();
        hideUI();
        loadingDialog.startLoadingAnimation(false);
        try {
            //noinspection unchecked
            products = (ArrayList<Product>) getIntent().getSerializableExtra("products");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiCalls.makeNutritionValuesRequest(this,
                Recipe.createProductsJSON(products),
                response -> {
                    try {
                        JSONObject respObj = new JSONObject(response);
                        Log.d("TAG", "onCreate: RESPONSE " + respObj.toString());
                        JSONArray productsArray = respObj.getJSONArray("foods");
                        for (int i = 0; i < products.size(); i++) {
                            products.get(i).setSerializedJSON(productsArray.getJSONObject(i).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                    loadingDialog.dismissDialog();
                    showUI();
                },
                error -> {
                    error.printStackTrace();
                    setResult(RESULT_ERROR);
                    finish();
                });
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance("https://nitzan-android-final-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


    }


    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return super.onSupportNavigateUp();
    }

    private void initActivity(){
        imageView = findViewById(R.id.imgvRecipe);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("Recipe Info");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etRecipeDesc = findViewById(R.id.etRecipeDesc);
        etRecipeName = findViewById(R.id.etRecipeName);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        efabSave = findViewById(R.id.efabSave);
        efabSave.setOnClickListener(v -> createRecipe());
        //noinspection deprecation
        btnOpenCamera.setOnClickListener(v ->
                startActivityForResult(new Intent(this, CameraActivity.class), RQ_IMAGE_CAPTURE));
        btnOpenGallery.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //noinspection deprecation
            startActivityForResult(photoPickerIntent, RQ_IMAGE_PICKER);
        });
        etRecipeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                efabSave.setEnabled(!etRecipeName.getText().toString().matches(""));
            }
        });
    }


    private void hideUI(){
        etRecipeDesc.setVisibility(View.INVISIBLE);
        btnOpenCamera.setVisibility(View.INVISIBLE);
        etRecipeName.setVisibility(View.INVISIBLE);
        efabSave.setVisibility(View.INVISIBLE);
        btnOpenGallery.setVisibility(View.INVISIBLE);
    }


    private void showUI(){
        etRecipeDesc.setVisibility(View.VISIBLE);
        btnOpenCamera.setVisibility(View.VISIBLE);
        etRecipeName.setVisibility(View.VISIBLE);
        efabSave.setVisibility(View.VISIBLE);
        btnOpenGallery.setVisibility(View.VISIBLE);
    }


    private void createRecipe(){
        String recipeName = etRecipeName.getText().toString().trim();
        String recipeDesc = etRecipeDesc.getText().toString().trim();
        if (recipeName.isEmpty()){
            etRecipeName.setError("Name required");
            etRecipeName.requestFocus();
            return;
        }
        Recipe recipe = new Recipe(products, recipeName, recipeDesc);
        saveRecipe(recipe);
    }


    private void saveRecipe(Recipe recipe) {
        loadingDialog.startLoadingAnimation(false);
        DatabaseReference pushedRef = mDatabase.child("users_recipes").child(userId).push();
        String recipeID = pushedRef.getKey();
        if (imageUri != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imagesRef = storage.getReference().child("recipes_images/"+recipeID+"/");
            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.continueWithTask(
                    task -> {
                        if (!task.isSuccessful()) {
                            throw (Objects.requireNonNull(task.getException()));
                        }
                        return imagesRef.getDownloadUrl();
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String imageUri = task.getResult().toString();
                            Log.d("TAG", "saveRecipe: " + imageUri);
                            recipe.setImage(imageUri);
                            pushToDB(recipe, pushedRef);
                        } else {
                            Log.d("TAG", "createAndSaveRecipe: ERROR");
                        }
                    });
        } else {
            pushToDB(recipe, pushedRef);
        }
    }


    private void pushToDB(Recipe recipe, DatabaseReference pushedRef){
        pushedRef.setValue(recipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(AddDescriptionActivity.this, "Recipe saved",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
            } else {
                Toast.makeText(AddDescriptionActivity.this, "Failed",Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
            }
            finishActivity(RQ_NEXT_ACTIVITY);
            finish();
            loadingDialog.dismissDialog();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (data != null){
                imageUri = data.getData();
                int rotation = 0;
                if (requestCode == RQ_IMAGE_PICKER){
                    String fullPath = Uri2PathUtil.getRealPathFromUri(this, data.getData());
                    rotation = ImageRotationDetectionHelper.getCameraPhotoOrientation(fullPath);
                }
                Picasso.get()
                        .load(data.getData())
                        .noPlaceholder()
                        .centerCrop()
                        .fit()
                        .rotate(rotation)
                        .into(imageView);
            }
        }
    }
}


