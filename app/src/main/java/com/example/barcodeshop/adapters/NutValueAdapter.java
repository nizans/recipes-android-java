package com.example.barcodeshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.barcodeshop.R;
import com.example.barcodeshop.models.NutritionalValue;

import java.util.ArrayList;

public class NutValueAdapter extends ArrayAdapter<NutritionalValue> {

    Context mContext;
    int resourceLayout;
    TextView title, tvNutPer;
    ProgressBar progressBar;
    public NutValueAdapter(@NonNull Context context, int resource, ArrayList<NutritionalValue> nutritionalValues) {
        super(context, resource, nutritionalValues);
        this.mContext = context;
        this.resourceLayout = resource;
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
        NutritionalValue nv = getItem(position);

        if (nv != null){
            title = v.findViewById(R.id.tvNutValName);
            progressBar = v.findViewById(R.id.pbNutValue);
            if (nv.getName().matches("Calories")){
                progressBar.setVisibility(View.GONE);
            }
            if (nv.getName().matches("Sugars")){
                title.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            title.setText(nv.toString());
            progressBar.setProgress(nv.getPerc());
        }

        return v;
    }
}
