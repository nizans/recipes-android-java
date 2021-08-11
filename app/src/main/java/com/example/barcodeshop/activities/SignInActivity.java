package com.example.barcodeshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barcodeshop.dialogs.LoadingDialog;
import com.example.barcodeshop.MainActivity;
import com.example.barcodeshop.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etSignInEmail, etSignInPassword;
    Button btnSignIn;
    TextView tvNotAUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            //User Already signed in - redirect
            finish();
        }

        etSignInEmail = findViewById(R.id.etSignInEmail);
        etSignInPassword = findViewById(R.id.etSignInPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvNotAUser = findViewById(R.id.tvNotAUser);

        btnSignIn.setOnClickListener(this);
        tvNotAUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case (R.id.btnSignIn):
                logInUser();
                return;

            case (R.id.tvNotAUser):
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                return;

            default:
                break;
        }
    }

    private void logInUser() {
        String email = etSignInEmail.getText().toString().trim();
        String password = etSignInPassword.getText().toString().trim();

        if (email.isEmpty()){
            etSignInEmail.setError("Email required");
            etSignInEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            etSignInPassword.setError("Password required");
            etSignInPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etSignInEmail.setError("Email is invalid");
            etSignInEmail.requestFocus();
            return;
        }
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingAnimation(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(SignInActivity.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                loadingDialog.dismissDialog();
                finish();

            } else {
                Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
            loadingDialog.dismissDialog();
        });
    }

}