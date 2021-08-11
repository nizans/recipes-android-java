package com.example.barcodeshop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.barcodeshop.R;
import com.example.barcodeshop.models.Product;
import com.example.barcodeshop.models.Unit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.barcodeshop.models.Unit.UnitsENUM;

public class ProductListAdapter extends ArrayAdapter<Product>  {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;

    private final int resourceLayout;
    private final Context mContext;
    private Product p;
    private Slider unitsSlider;
    private TextView tvAmount, tvUnits, tvProductName;



    public ProductListAdapter(@NonNull Context context, int resource, ArrayList<Product> products ) {
        super(context, resource, products);
        mContext = context;
        resourceLayout = resource;

    }


    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent ) {
        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        p = getItem(position);
        if(p != null){
            tvProductName = v.findViewById(R.id.tvProductName);
            tvUnits = v.findViewById(R.id.tvUnitsType);
            tvAmount = v.findViewById(R.id.etAmount);
            unitsSlider = v.findViewById(R.id.unitSlider);
            tvProductName.setText(p.getName());
            tvUnits.setText(p.getUnit().getName());
            tvUnits.setTag(position);
            tvUnits.setOnClickListener(v1 -> showPopup(v1, getItem((Integer) v1.getTag())));
            tvAmount.setText(p.getAmount());
            unitsSlider.setValue(Float.parseFloat(p.getAmount()));
            unitsSlider.setTag(position);
            unitsSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
                @Override
                public void onStartTrackingTouch(@NonNull @NotNull Slider slider) {

                }
                @Override
                public void onStopTrackingTouch(@NonNull @NotNull Slider slider) {
                    getItem((Integer) slider.getTag()).setAmount(String.format("%.0f",slider.getValue()));

                    tvAmount.setText(getItem((Integer) slider.getTag()).getAmount());
                    notifyDataSetChanged();
                }
            });
            setSliderSettings(p.getUnit(), Float.parseFloat(p.getAmount()));
            FloatingActionButton fabRemoveProduct = v.findViewById(R.id.fabRemoveProduct);
            fabRemoveProduct.setTag(position);
            fabRemoveProduct.setOnClickListener(v1 -> {
                remove(getItem((Integer) v1.getTag()));
                notifyDataSetChanged();
            });
        }
        return v;
    }


    public void showPopup(View v, Product p) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        for(UnitsENUM enumVal : UnitsENUM.values()){
            popup.getMenu().add(enumVal.name());
        }
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            Unit unit = UnitsENUM.valueOf(item.getTitle().toString()).getUnit();
            p.setAmount(String.valueOf(unit.getDefaultVal()));
            p.setUnit(unit);
            notifyDataSetChanged();
            return true;
        });
    }


    @SuppressLint("DefaultLocale")
    private void setSliderSettings(Unit unit, float amount) {
        unitsSlider.setValueFrom(unit.getValFrom());
        unitsSlider.setValue(amount);
        unitsSlider.setValueTo(unit.getValTo());
        unitsSlider.setStepSize(unit.getStep());
    }

}


