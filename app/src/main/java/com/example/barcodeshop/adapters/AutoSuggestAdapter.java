package com.example.barcodeshop.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.barcodeshop.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AutoSuggestAdapter extends ArrayAdapter<Pair<String, String>> implements Filterable {

    private final List<Pair<String, String>> mListData;
    private final int resourceLayout;
    private final Context mContext;
    public AutoSuggestAdapter(@NonNull Context context, int resource){
        super(context, resource);
        mListData = new ArrayList<>();
        resourceLayout = resource;
        mContext = context;
    }

    public void setData(List<Pair<String, String>> list) {
        mListData.clear();
        mListData.addAll(list);
    }
    @Override
    public int getCount() {
        return mListData.size();
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
        TextView tv = v.findViewById(R.id.tvSuggestRowName);
        ImageView imageView = v.findViewById(R.id.imgvSugRow);

        Pair<String, String> p = getItem(position);
        if (p != null){
            tv.setText(p.first);
            Picasso.get().load(p.second)
                    .placeholder(R.drawable.ic_baseline_broken_image_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .fit()
                    .centerCrop()
                    .into(imageView);

        }



        return v;
    }

    @Nullable
    @Override
    public Pair<String, String> getItem(int position) {
        return mListData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mListData;
                    filterResults.count = mListData.size();
                }
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
