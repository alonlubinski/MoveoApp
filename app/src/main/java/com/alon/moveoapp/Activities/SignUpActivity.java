package com.alon.moveoapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.Models.Patient;
import com.alon.moveoapp.Models.User;
import com.alon.moveoapp.R;
import com.alon.moveoapp.Utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout register_EDT_first, register_EDT_last, register_EDT_email,
            register_EDT_password, register_EDT_password2;
    private RadioGroup register_RDG;
    private Button register_BTN_register;
    private ProgressBar register_PGB;
    private String firstName, lastName, email, password, password2, userType;
    private int rdgChoice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Handler handler = new Handler();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findAll();
        setClickListeners();
        register_PGB.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Function that finds all views by id.
    private void findAll() {
        register_EDT_first = findViewById(R.id.register_EDT_first);
        register_EDT_last = findViewById(R.id.register_EDT_last);
        register_EDT_email = findViewById(R.id.register_EDT_email);
        register_EDT_password = findViewById(R.id.register_EDT_password);
        register_EDT_password2 = findViewById(R.id.register_EDT_password2);
        register_RDG = findViewById(R.id.register_RDG);
        register_BTN_register = findViewById(R.id.register_BTN_register);
        register_PGB = findViewById(R.id.register_PGB);
    }

    // Function that sets click listeners.
    private void setClickListeners() {
        register_BTN_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register_BTN_register:
                Log.d("pttt", "Sign up pressed");
                register_BTN_register.setEnabled(false);
                register_PGB.setVisibility(View.VISIBLE);
                firstName = register_EDT_first.getEditText().getText().toString().trim();
                lastName = register_EDT_last.getEditText().getText().toString().trim();
                email = register_EDT_email.getEditText().getText().toString().trim();
                password = register_EDT_password.getEditText().getText().toString().trim();
                password2 = register_EDT_password2.getEditText().getText().toString().trim();
                rdgChoice = register_RDG.getCheckedRadioButtonId();
                switch (rdgChoice){
                    case R.id.register_RDB_patient:
                        userType = getResources().getString(R.string.patient_str);
                        user = new Patient(firstName, lastName, email, userType);
                        break;
                    case R.id.register_RDB_doctor:
                        userType = getResources().getString(R.string.doctor_str);
                        user = new Doctor(firstName, lastName, email, userType, false);
                        break;
                }
                if(validateForm(firstName, lastName, email, password, password2)){
                    // Form is valid.
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                db.collection(userType).document(email).set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // User registration success
                                        Toast.makeText(getApplicationContext(), "Registration success", Toast.LENGTH_SHORT).show();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 2000);
                                    }
                                });
                            } else {
                                register_BTN_register.setEnabled(true);
                                register_PGB.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    // Form is not valid.
                    register_BTN_register.setEnabled(true);
                    register_PGB.setVisibility(View.GONE);
                }
                break;
        }
    }

    // Function that gets information from the form and validate it.
    private boolean validateForm(String firstName, String lastName, String email,
                                 String password, String password2){
        boolean isValid = true;
        boolean passwordValid = true;

        if(!Validation.validateIsNotEmpty(firstName)){
            register_EDT_first.setError(getResources().getString(R.string.valid_first_str));
            isValid = false;
        } else {
            register_EDT_first.setError("");
        }

        if(!Validation.validateIsNotEmpty(lastName)){
            register_EDT_last.setError(getResources().getString(R.string.valid_last_str));
            isValid = false;
        } else {
            register_EDT_last.setError("");
        }

        if(!Validation.validateEmail(email)){
            register_EDT_email.setError(getResources().getString(R.string.valid_email_str));
            isValid = false;
        } else {
            register_EDT_email.setError("");
        }

        if(!Validation.validatePasswordLength(password)){
            register_EDT_password.setError(getResources().getString(R.string.valid_password_str));
            isValid = false;
            passwordValid = false;
        } else {
            register_EDT_password.setError("");
        }

        if(!Validation.validatePasswordLength(password2)){
            register_EDT_password2.setError(getResources().getString(R.string.valid_password_str));
            isValid = false;
            passwordValid = false;
        } else {
            register_EDT_password2.setError("");
        }

        if(!Validation.validatePasswordsMatch(password, password2) && passwordValid){
            register_EDT_password.setError(getResources().getString(R.string.valid_match_str));
            register_EDT_password2.setError(getResources().getString(R.string.valid_match_str));
            isValid = false;
        } else if(passwordValid){
            register_EDT_password.setError("");
            register_EDT_password2.setError("");
        }
        return isValid;
    }
}