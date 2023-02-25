package com.example.meetpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    FirebaseAuth auth;
    TextInputEditText textInputEditTextEmail;
    private Button resetPasswordBtn;
    private TextView loginTextView, signupTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        textInputEditTextEmail = findViewById(R.id.EmailFP);
        signupTextView = findViewById(R.id.signupText);
        signupTextView.setOnClickListener(this);
        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        resetPasswordBtn.setOnClickListener(this);
        progressBar = findViewById(R.id.progressFP);

        auth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    private boolean validateEmail() {
        String emailInput = String.valueOf(textInputEditTextEmail.getText());

        if (emailInput.isEmpty()) {
            textInputEditTextEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEditTextEmail.setError("Please enter a valid email address");
            return false;
        } else {
            textInputEditTextEmail.setError(null);
            return true;
        }
    }

    private void resetPassword() {
        String email = String.valueOf(textInputEditTextEmail.getText());

        if (!validateEmail())
            return;

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPassword.this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.signupText:
                startActivity(new Intent(ForgotPassword.this, SignUp.class));
                break;
            case R.id.loginTextView:
                startActivity(new Intent(ForgotPassword.this, Login.class));
                break;
        }
    }
}