package com.alon.moveoapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.alon.moveoapp.Fragments.DoctorFragment;
import com.alon.moveoapp.Fragments.PatientFragment;
import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.Models.Patient;
import com.alon.moveoapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private ProgressBar home_PGB;
    private Fragment fragment = null;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findAll();
        if(getIntent().getExtras() != null){
            if(fragment == null) {
                userType = getIntent().getExtras().getString("userType");
                switch (userType) {
                    case "Patient":
                        Patient patient = (Patient) getIntent().getSerializableExtra("user");
                        fragment = new PatientFragment(patient);
                        break;
                    case "Doctor":
                        Doctor doctor = (Doctor) getIntent().getSerializableExtra("user");
                        fragment = new DoctorFragment(doctor);
                        break;
                }
                replaceFragment(fragment);
                home_PGB.setVisibility(View.GONE);
            }
        }
    }

    // Function that finds all views by id.
    private void findAll() {
        home_PGB = findViewById(R.id.home_PGB);
    }

    // Function that gets fragment and update ui.
    private void replaceFragment(Fragment fragment){
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.home_FL, fragment).commit();
        }
    }


}