package com.example.barcodeshop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcodeshop.MainActivity;
import com.example.barcodeshop.R;
import com.example.barcodeshop.dialogs.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etRegEmail, etRegPass, etRegName;
    TextView tvRegSignIn;
    Button btnRegister;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            //User Already signed in - redirect
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPass = findViewById(R.id.etRegPass);
        etRegName = findViewById(R.id.etRegName);
        tvRegSignIn = findViewById(R.id.tvRegSignIn);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);
        tvRegSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case (R.id.btnRegister):
                registerUser();
                return;

            case (R.id.tvRegSignIn):
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return;

            default:
                break;
        }
    }

    private void registerUser() {
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPass.getText().toString().trim();
        String name = etRegName.getText().toString().trim();

        if (email.isEmpty()){
            etRegEmail.setError("Email required");
            etRegEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            etRegPass.setError("Password required");
            etRegPass.requestFocus();
            return;
        }
        if (name.isEmpty()){
            etRegName.setError("Name required");
            etRegName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etRegEmail.setError("Email is invalid");
            etRegEmail.requestFocus();
            return;
        }
        if (password.length() < 6){
            etRegPass.setError("Min password length is 6 characters");
            etRegPass.requestFocus();
            return;
        }
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingAnimation(false);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Objects.requireNonNull(task.getResult().getUser()).updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "User Created\nLogged in", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(this, MainActivity.class));
                                loadingDialog.dismissDialog();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                loadingDialog.dismissDialog();

                            }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_LONG).show();
                loadingDialog.dismissDialog();
            }
        });
    }


}