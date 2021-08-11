package com.example.barcodeshop.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.barcodeshop.R;

public class LoadingDialog {

    private final Activity activity;
    private AlertDialog alertDialog;


    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("InflateParams")
    public void startLoadingAnimation(boolean cancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading_layout, null));
        builder.setCancelable(cancelable);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

}
