package com.example.meetpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;


public class Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    private TextView btnLogin,signupTextView,forgotPasswordTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        textInputEditTextEmail = findViewById(R.id.emailLogin);
        textInputEditTextPassword = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        signupTextView = findViewById(R.id.signupText);
        signupTextView.setOnClickListener(this);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(this);
        progressBar = findViewById(R.id.progressLog);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                btnLogin();
                break;
            case R.id.signupText:
                startActivity( new Intent(Login.this, SignUp.class));
                break;
            case R.id.forgotPasswordTextView:
                startActivity(new Intent(Login.this, ForgotPassword.class));
                break;
        }
    }

    private void btnLogin() {
        String email, password;

        email = String.valueOf(textInputEditTextEmail.getText());
        password = String.valueOf(textInputEditTextPassword.getText());

        if(email.isEmpty()){
            textInputEditTextEmail.setError("Email is required!");
            textInputEditTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEditTextEmail.setError("Please enter a valid email");
            textInputEditTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            textInputEditTextPassword.setError("Password is required!");
            textInputEditTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if(user.isEmailVerified()) {

                                Toast.makeText(Login.this, "Login Success!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    System.out.println("Fetching FCM registration token failed");
                                                    return;
                                                }
                                                
                                                // Get new FCM registration token
                                                String token = task.getResult();

                                                // Log and toast
                                                System.out.println("PRINTING YOUR TOKEN:" + token);
                                                //Toast.makeText(Login.this, token, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                startActivity(new Intent(Login.this, HomePage.class));

                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                user.sendEmailVerification();
                                Toast.makeText(Login.this, "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Login.this,"Fail to login! Please check your credentials",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}