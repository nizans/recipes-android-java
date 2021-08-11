package com.example.barcodeshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Response;
import com.example.barcodeshop.Test.RandomContentGenerator;
import com.example.barcodeshop.activities.RecipeScreenActivity;
import com.example.barcodeshop.activities.SignInActivity;
import com.example.barcodeshop.activities.addRecipe.AddProductsActivity;
import com.example.barcodeshop.adapters.RecipeListAdapter;
import com.example.barcodeshop.dialogs.LoadingDialog;
import com.example.barcodeshop.models.Recipe;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.barcodeshop.helpers.StaticCodes.REQUEST_CODE_PERMISSIONS;
import static com.example.barcodeshop.helpers.StaticCodes.REQUIRED_PERMISSIONS;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    TextView tvNavName, tvNavSubName;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ArrayList<Recipe> recipes;
    RecipeListAdapter recipesAdapter;
    ListView lvRecipes;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null){
            //No user signed in - redirect
            Toast.makeText(this, "Please Sign in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            ref = FirebaseDatabase.getInstance("https://nitzan-android-final-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference().child("users_recipes").child(user.getUid());
            lvRecipes = findViewById(R.id.lvRecipes);
            lvRecipes.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(MainActivity.this, RecipeScreenActivity.class);
                intent.putExtra("recipe", recipes.get(position));
                startActivity(intent);
            });

            lvRecipes.setOnItemLongClickListener((parent, view, position, id) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Recipe?").setMessage("Are you sure?").setPositiveButton("Delete",
                        (dialog, which) -> {

                            deleteRecipe(recipesAdapter.getItem(position));
                            Toast.makeText(MainActivity.this, "Recipe Deleted.", Toast.LENGTH_LONG).show();;
                        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
                return true;
            });


            setUpToolbar();
            updateNavUI(user);
            makeUserRecipesRequest();

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    makeUserRecipesRequest();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            if(!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (drawerLayout.isOpen())
            drawerLayout.close();
    }

    private void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateNavUI(FirebaseUser user) {
        tvNavName = navigationView.getHeaderView(0).findViewById(R.id.tvNavName);
        tvNavSubName = navigationView.getHeaderView(0).findViewById(R.id.tvNavSubName);
        try {
            tvNavName.setText(user.getEmail());
            tvNavSubName.setText("Hello " + user.getDisplayName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permmision : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permmision) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()){
                case (R.id.recipe_screen):
                    intent = new Intent(this, AddProductsActivity.class);
                    startActivity(intent);
                    return false;
                case (R.id.menu_signout):
                    user = null;
                    mAuth.signOut();
                    startActivity(new Intent(this, SignInActivity.class));
                case (R.id.menu_random_recipe):
                    LoadingDialog loadingDialog = new LoadingDialog(this);
                    loadingDialog.startLoadingAnimation(false);
                    RandomContentGenerator.uploadRandomRecipeToDB(MainActivity.this, response -> {
                        RandomContentGenerator.pushRandomRecipeToDb(MainActivity.this, response);
                        loadingDialog.dismissDialog();
                    });
                default:
                    return false;
            }
    }

    public void makeUserRecipesRequest(){
        recipes = new ArrayList<>();
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingAnimation(false);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Recipe r = dataSnapshot.getValue(Recipe.class);
                    assert r != null;
                    r.setId(dataSnapshot.getKey());
                    recipes.add(r);
                }
                recipesAdapter = new RecipeListAdapter(MainActivity.this, R.layout.recipe_row, recipes);

                lvRecipes.setAdapter(recipesAdapter);
                loadingDialog.dismissDialog();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }



        });
    }

    public void deleteRecipe(Recipe r){
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        loadingDialog.startLoadingAnimation(false);
        DatabaseReference recipeRef = ref.child(r.getId());
        recipeRef.removeValue((error, ref) -> {
            Toast.makeText(MainActivity.this, "Recipe Deleted", Toast.LENGTH_LONG).show();
            loadingDialog.dismissDialog();

        });





    }
}






