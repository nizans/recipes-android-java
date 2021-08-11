package com.example.barcodeshop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.barcodeshop.R;
import com.example.barcodeshop.helpers.NutritionConstents;
import com.example.barcodeshop.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    private final ArrayList<Recipe> recipes;
    Context mContext;
    private final int resourceLayout;

    ImageView recipeRowImgV;
    TextView tvRecipeRowName, tvRecipeRowDesc, tvCalories;
    ProgressBar pbFats, pbCarbs, pbProteins, pbSugers;


    public RecipeListAdapter(@NonNull Context context, int resource, ArrayList<Recipe> recipes) {
        super(context, resource, recipes);
        this.mContext = context;
        this.recipes = recipes;
        this.resourceLayout = resource;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        Recipe r = recipes.get(position);
        if (r != null){
            recipeRowImgV = v.findViewById(R.id.recipeRowImgV);
            tvRecipeRowDesc = v.findViewById(R.id.tvRecipeRowDesc);
            tvRecipeRowName = v.findViewById(R.id.tvRecipeRowName);
            pbFats = v.findViewById(R.id.pbFats);
            pbCarbs = v.findViewById(R.id.pbCarbs);
            pbProteins = v.findViewById(R.id.pbProteins);
            pbSugers = v.findViewById(R.id.pbSugers);
            tvCalories = v.findViewById(R.id.tvCalories);

            if (!r.getProducts().isEmpty()){
                int calories =(int) r.getTotalRecipreNutValueByName(NutritionConstents.CALORIES);
                tvCalories.setText("Calories: "+String.format("%d", calories));

                pbFats.setMax(calories/3);
                pbProteins.setMax(calories/3);
                pbSugers.setMax(calories/3);
                pbCarbs.setMax(calories/3);

                pbFats.setProgress((int) r.getTotalRecipreNutValueByName(NutritionConstents.TOTAL_FATS));
                pbCarbs.setProgress((int) r.getTotalRecipreNutValueByName(NutritionConstents.TOTAL_CARBS));
                pbProteins.setProgress((int) r.getTotalRecipreNutValueByName(NutritionConstents.PROTEINS));
                pbSugers.setProgress((int) r.getTotalRecipreNutValueByName(NutritionConstents.SUGARS));
            }

            tvRecipeRowName.setText(r.getName());
            String shortDescription = r.getDescription().substring(0, Math.min(r.getDescription().length(), 80));
            tvRecipeRowDesc.setText(shortDescription + "...");



            if (r.getImage() != null){
                Picasso.get()
                        .load(r.getImage())
                        .placeholder(R.drawable.ic_baseline_broken_image_24)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .fit()
                        .centerCrop()
                        .into(recipeRowImgV);
            }
            if (r.getImage() == null){
                Picasso.get()
                        .load("https://toppng.com/uploads/preview/clipart-free-seaweed-clipart-draw-food-placeholder-11562968708qhzooxrjly.png")
                        .fit().centerCrop().into(recipeRowImgV);
            }
        }
        return v;
    }
}
