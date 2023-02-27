package com.example.meetpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    TextInputEditText textInputEditTextFullname, textInputEditTextUsername, textInputEditTextPassword, textInputEditTextEmail, textInputEditTextPassword2, textInputEditTextMobileNumber;

    private TextView btnSignup, loginTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        textInputEditTextFullname = findViewById(R.id.fullNameReg);
        textInputEditTextUsername = findViewById(R.id.usernameReg);
        textInputEditTextPassword = findViewById(R.id.passwordReg);
        textInputEditTextPassword2 = findViewById(R.id.password2Reg);
        textInputEditTextEmail = findViewById(R.id.emailReg);
        textInputEditTextMobileNumber = findViewById(R.id.mobileNumberReg);
        btnSignup = (Button) findViewById(R.id.btnSignUp);
        btnSignup.setOnClickListener(this);
        loginTextView = findViewById(R.id.loginText);
        loginTextView.setOnClickListener(this);
        progressBar = findViewById(R.id.progressReg);

    }

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[!*()@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{6,}" +               //at least 6 characters
            "$"
    );

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

    private boolean validateUsername() {
        String usernameInput = String.valueOf(textInputEditTextUsername.getText());

        if (usernameInput.isEmpty()) {
            textInputEditTextUsername.setError("Field can't be empty");
            textInputEditTextUsername.requestFocus();
            return false;
        } else if (usernameInput.length() > 15) {
            textInputEditTextUsername.setError("Username too long.");
            textInputEditTextUsername.requestFocus();
            return false;
        } else {
            textInputEditTextUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = String.valueOf(textInputEditTextPassword.getText());

        if (passwordInput.isEmpty()) {
            textInputEditTextPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputEditTextPassword.setError("Password must be at least 6 characters long and have at least 1 number, 1 alphabet and 1 special character");
            textInputEditTextPassword.requestFocus();
            return false;
        } else {
            textInputEditTextPassword.setError(null);
            return true;
        }
    }

    private boolean matchPassword() {
        String passwordInput = String.valueOf(textInputEditTextPassword.getText());
        String passwordInput2 = String.valueOf(textInputEditTextPassword2.getText());

        if (passwordInput.equals(passwordInput2)) {
            textInputEditTextPassword2.setError(null);
            return true;
        } else {
            textInputEditTextPassword2.setError("Password does not match");
            textInputEditTextPassword2.requestFocus();
            return false;
        }
    }

    private boolean validateMobile() {
        String mobileNumber = String.valueOf(textInputEditTextMobileNumber.getText());

        String mobileRegex = "[6-9][0-9]{7}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(mobileNumber);

        if (mobileNumber.isEmpty()) {
            textInputEditTextMobileNumber.setError("Field can't be empty");
            textInputEditTextMobileNumber.requestFocus();
            return false;
        } else if (!mobileMatcher.find()) {
            textInputEditTextMobileNumber.setError("Please enter a valid mobile number");
            textInputEditTextMobileNumber.requestFocus();
            return false;
        } else {
            textInputEditTextMobileNumber.setError(null);
            return true;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                btnSignUp();
                break;
            case R.id.loginText:
                startActivity(new Intent(SignUp.this, Login.class));
                break;
        }
    }

    private void btnSignUp() {
        String fullname;
        String username;
        String password;
        String email;
        String mobileNumber;

        fullname = String.valueOf(textInputEditTextFullname.getText());
        username = String.valueOf(textInputEditTextUsername.getText());
        password = String.valueOf(textInputEditTextPassword.getText());
        email = String.valueOf(textInputEditTextEmail.getText());
        mobileNumber = String.valueOf(textInputEditTextMobileNumber.getText());


        if (!validateUsername())
            return;

        if (!validatePassword())
            return;

        if (!validateEmail())
            return;

        if (!matchPassword())
            return;

        if (!validateMobile())
            return;

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(fullname, username, email, mobileNumber, id, "null");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(id)
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                progressBar.setVisibility(View.GONE);
                                                user.sendEmailVerification();
                                                Toast.makeText(SignUp.this, "Sign Up Success! Check your email to verify your account!", Toast.LENGTH_LONG).show();

                                                startActivity(new Intent(SignUp.this, Login.class));
                                            } else {
                                                Toast.makeText(SignUp.this, "Sign Up Fail! Try again!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                textInputEditTextEmail.setError("Email already taken! Please enter another email");
                                textInputEditTextEmail.requestFocus();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });
    }
}