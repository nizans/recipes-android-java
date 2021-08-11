package com.example.barcodeshop.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.barcodeshop.R;
import com.example.barcodeshop.adapters.AutoSuggestAdapter;
import com.example.barcodeshop.helpers.ApiCalls;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NewProductDialog extends DialogFragment   {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private AutoSuggestAdapter autoSuggestAdapter;
    NewProductItemDialogInterface itemDialogInterface;
    Handler handler;
    Activity activity;

    public NewProductDialog(Activity activity, NewProductItemDialogInterface itemDialogInterface) {

        this.itemDialogInterface = itemDialogInterface;
        this.activity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog()).getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        setCancelable(true);
        View v = inflater.inflate(R.layout.new_product_layout, container, false);
        AutoCompleteTextView autoCompleteTextView = v.findViewById(R.id.acTvName);
        handler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                    makeApiCall(autoCompleteTextView.getText().toString());
                }
            }
            return false;
        });
        autoSuggestAdapter = new AutoSuggestAdapter(activity, R.layout.suggestion_item_row);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            TextView tv = view.findViewById(R.id.tvSuggestRowName);

            itemDialogInterface.onItemSelected(tv.getText().toString());
            dismiss();
        });
        v.findViewById(R.id.btnNewProdCancel).setOnClickListener(v1 -> dismiss());
        return v;
    }

    public interface NewProductItemDialogInterface {
        void onItemSelected(String value);

    }

    private void makeApiCall(String text) {
        ApiCalls.makeAutoCompleteRequest(activity, text,
                response -> {
                    List<Pair<String, String>> stringList = new ArrayList<>();
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        JSONArray array = responseObject.getJSONArray("common");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject row = array.getJSONObject(i);

                            String name = row.getString("food_name");
                            String imgUrl = row.getJSONObject("photo").getString("thumb");
                            Pair<String, String> p = new Pair<>(name, imgUrl);
                            stringList.add(p);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    autoSuggestAdapter.setData(stringList);
                    autoSuggestAdapter.notifyDataSetChanged();
                },
                error -> {
                    Log.d("ERROR WITH API CALL", "makeApiCall: ");
                    error.printStackTrace();
                });
    }

}
