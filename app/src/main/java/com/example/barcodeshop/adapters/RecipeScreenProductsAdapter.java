package com.example.barcodeshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.barcodeshop.R;
import com.example.barcodeshop.models.Product;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeScreenProductsAdapter extends ArrayAdapter<Product> {

    Context mContext;
    private final int resourceLayout;
    ImageView imgvGridItemImage;
    TextView tvGridItemTitle;

    public RecipeScreenProductsAdapter(@NonNull Context context, int resource, ArrayList<Product> products) {
        super(context, resource, products);
        this.mContext = context;
        resourceLayout = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        tvGridItemTitle = v.findViewById(R.id.tvGridItemTitle);
        imgvGridItemImage = v.findViewById(R.id.imgvGridItemImage);

        Product p = getItem(position);
        if (p != null){

            String imgURL = null;
            try {
                imgURL = getImageUrl(p);
            } catch (JSONException | NullPointerException e ) {
                e.printStackTrace();
            }

            Picasso.get()
                    .load(imgURL)
                    .placeholder(R.drawable.ic_baseline_broken_image_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .fit()
                    .centerCrop()
                    .into(imgvGridItemImage);

            tvGridItemTitle.setText(p.getName() + " "+ p.getAmount()+" "+p.getUnitName());
        }


        return v;
    }

    private String getImageUrl(Product p) throws JSONException {
        JSONObject jsonObject = p.getJsonObject().getJSONObject("photo");
        return jsonObject.getString("thumb");
    }


}


