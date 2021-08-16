package com.alon.moveoapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alon.moveoapp.Adapters.AppointmentsListAdapter;
import com.alon.moveoapp.Models.Appointment;
import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.Models.Patient;
import com.alon.moveoapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    private Doctor doctor;
    private Patient patient;
    private RecyclerView appointment_RCV;
    private MaterialButton appointment_BTN_cancel;
    private ProgressBar appointment_PGB;
    private ArrayList<Appointment> appointmentsList = new ArrayList<>();
    private ArrayList<Appointment> tempAppointmentsList = new ArrayList<>();
    private AppointmentsListAdapter appointmentsListAdapter;
    private FirebaseFirestore db;
    private Handler handler = new Handler();
    private Query query;

    // Event listener for changes on waiting list sub collection.
    private EventListener eventListener = new EventListener() {
        @Override
        public void onEvent(@Nullable Object value, @Nullable FirebaseFirestoreException error) {
            if(error != null){
                Log.d("pttt", error.getLocalizedMessage());
                return;
            }
            for(DocumentChange dc : ((QuerySnapshot) value).getDocumentChanges()){
                Appointment appointment = dc.getDocument().toObject(Appointment.class);
                switch (dc.getType()){
                    case ADDED:
                        if(!appointment.isDone())
                            tempAppointmentsList.add(appointment);
                        break;
                    case MODIFIED:
                        if(tempAppointmentsList.size() > 0){
                            for(Appointment a : appointmentsList){
                                if(appointment.getPatient().getEmail().equals(a.getPatient().getEmail())){
                                    tempAppointmentsList.remove(a);
                                    tempAppointmentsList.add(appointment);
                                    break;
                                }
                            }
                        }
                        break;
                    case REMOVED:
                        tempAppointmentsList.remove(appointment);
                        break;
                }
            }
            appointmentsList = filterListByIsDone(tempAppointmentsList);
            initRecyclerView();
            if(appointmentsList.get(0).getPatient().getEmail().equals(patient.getEmail())){
                appointment_BTN_cancel.setEnabled(false);
                appointment_PGB.setVisibility(View.VISIBLE);
                // Send real notification??
                Toast.makeText(getApplicationContext(), "Appointment is on", Toast.LENGTH_LONG).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 5000);
            }
        }
    };

    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        if(getIntent().getExtras() != null){
            patient = (Patient) getIntent().getSerializableExtra("patient");
            doctor = (Doctor) getIntent().getSerializableExtra("doctor");
        }
        db = FirebaseFirestore.getInstance();
        query = db.collection("Doctor").document(doctor.getEmail()).collection("waiting").orderBy("timestamp");
        findAll();
        setClickListeners();
    }

    // Function that sets click listeners.
    private void setClickListeners() {
        appointment_BTN_cancel.setOnClickListener(this);
    }

    // Function that finds all views by id.
    private void findAll() {
        appointment_RCV = findViewById(R.id.appointment_RCV);
        appointment_BTN_cancel = findViewById(R.id.appointment_BTN_cancel);
        appointment_PGB = findViewById(R.id.appointment_PGB);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.appointment_BTN_cancel:
                appointment_BTN_cancel.setEnabled(false);
                appointment_PGB.setVisibility(View.VISIBLE);
                cancelAppointment();
                break;
        }
    }

    // Function that cancel the appointment of the current user.
    private void cancelAppointment() {
        db.collection("Doctor")
                .document(doctor.getEmail())
                .collection("waiting")
                .document(patient.getEmail())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Appointment deleted successfully", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appointment_PGB.setVisibility(View.VISIBLE);
        appointment_RCV.removeAllViews();
        appointmentsList.clear();
        tempAppointmentsList.clear();
        registration = query.addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        registration.remove();
    }

    // Function that initializes the recycler view and update the ui.
    private void initRecyclerView() {
        appointment_RCV.setHasFixedSize(true);
        appointmentsListAdapter = new AppointmentsListAdapter(appointmentsList);
        appointment_RCV.setAdapter(appointmentsListAdapter);
        appointment_PGB.setVisibility(View.GONE);
    }

    // Function that filters the appointments array and keeps all appointments that are not done yet.
    private ArrayList<Appointment> filterListByIsDone(ArrayList<Appointment> arr){
        ArrayList<Appointment> filteredArray = new ArrayList<>();
        for(Appointment appointment : arr){
            if(!appointment.isDone()){
                filteredArray.add(appointment);
            }
        }
        return filteredArray;
    }
}