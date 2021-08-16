package com.alon.moveoapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.Models.Patient;
import com.alon.moveoapp.Models.User;
import com.alon.moveoapp.R;
import com.alon.moveoapp.Utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout login_EDT_email, login_EDT_password;
    private RadioGroup login_RDG;
    private Button login_BTN_login, login_BTN_register;
    private ProgressBar login_PGB;
    private String email, password, userType;
    private int rdgChoice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findAll();
        setClickListeners();
        login_PGB.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Function that finds all views by id.
    private void findAll() {
        login_EDT_email = findViewById(R.id.login_EDT_email);
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_RDG = findViewById(R.id.login_RDG);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_BTN_register = findViewById(R.id.login_BTN_register);
        login_PGB = findViewById(R.id.login_PGB);
    }

    // Function that sets click listeners.
    private void setClickListeners() {
        login_BTN_login.setOnClickListener(this);
        login_BTN_register.setOnClickListener(this);
    }

    // Function that starts the sign up activity.
    private void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_BTN_login:
                Log.d("pttt", "Login pressed");
                login_BTN_login.setEnabled(false);
                login_BTN_register.setEnabled(false);
                login_PGB.setVisibility(View.VISIBLE);
                email = login_EDT_email.getEditText().getText().toString().trim();
                password = login_EDT_password.getEditText().getText().toString().trim();
                rdgChoice = login_RDG.getCheckedRadioButtonId();
                switch (rdgChoice) {
                    case R.id.login_RDB_patient:
                        userType = getResources().getString(R.string.patient_str);
                        break;
                    case R.id.login_RDB_doctor:
                        userType = getResources().getString(R.string.doctor_str);
                        break;
                }
                if (validateForm(email, password)) {
                    // Form is valid.
                    // Check if there is a user from this type.
                    db.collection(userType).document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) { // There is user from this type.
                                    switch (userType){
                                        case "Patient":
                                            user = documentSnapshot.toObject(Patient.class);
                                            break;
                                        case "Doctor":
                                            user = documentSnapshot.toObject(Doctor.class);
                                            break;
                                    }
                                    signIn();
                                } else { // There is no user with this type.
                                    Toast.makeText(getApplicationContext(), "Email with user type doesn't exist", Toast.LENGTH_SHORT).show();
                                    login_BTN_login.setEnabled(true);
                                    login_BTN_register.setEnabled(true);
                                    login_PGB.setVisibility(View.GONE);
                                }
                            } else {
                                login_BTN_login.setEnabled(true);
                                login_BTN_register.setEnabled(true);
                                login_PGB.setVisibility(View.GONE);
                            }
                        }
                    });

                } else { // Form not valid.
                    login_BTN_login.setEnabled(true);
                    login_BTN_register.setEnabled(true);
                    login_PGB.setVisibility(View.GONE);
                }
                break;
            case R.id.login_BTN_register:
                Log.d("pttt", "Sign up pressed");
                startSignUpActivity();
                break;
        }
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startHomeActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            login_BTN_login.setEnabled(true);
                            login_BTN_register.setEnabled(true);
                            login_PGB.setVisibility(View.GONE);
                        }
                    }
                });
    }

    // Function that gets information from the login form and validate it.
    private boolean validateForm(String email, String password) {
        boolean isValid = true;

        if (!Validation.validateEmail(email)) {
            login_EDT_email.setError(getResources().getString(R.string.valid_email_str));
            isValid = false;
        } else {
            login_EDT_email.setError("");
        }

        if (!Validation.validatePasswordLength(password)) {
            login_EDT_password.setError(getResources().getString(R.string.valid_password_str));
            isValid = false;
        } else {
            login_EDT_password.setError("");
        }

        return isValid;
    }

    // Function that starts the home activity.
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        login_BTN_login.setEnabled(true);
        login_BTN_register.setEnabled(true);
        login_PGB.setVisibility(View.GONE);
    }
}