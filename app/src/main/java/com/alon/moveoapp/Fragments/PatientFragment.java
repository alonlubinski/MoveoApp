package com.alon.moveoapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alon.moveoapp.Activities.AppointmentActivity;
import com.alon.moveoapp.Adapters.DoctorsListAdapter;
import com.alon.moveoapp.Models.Appointment;
import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.Models.Patient;
import com.alon.moveoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class PatientFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private Patient patient;
    private Chip patient_CHP_availability;
    private RecyclerView patient_RCV;
    private ProgressBar patient_PGB;
    private boolean filteredByAvailability = false;
    private FirebaseFirestore db;
    private ArrayList<Doctor> doctorsList = new ArrayList<>();
    private DoctorsListAdapter doctorsListAdapter;
    private DoctorsListAdapter.OnItemClickListener clickListener = new DoctorsListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Doctor doctor) {
            makeAnAppointment(doctor);
        }
    };

    private Query query;

    // Event listener for changes on doctor collection.
    private EventListener eventListener = new EventListener() {
        @Override
        public void onEvent(@Nullable Object value, @Nullable FirebaseFirestoreException error) {
            if(error != null){
                Log.d("pttt", error.getLocalizedMessage());
                return;
            }
            for(DocumentChange dc : ((QuerySnapshot) value).getDocumentChanges()){
                Doctor doctor = dc.getDocument().toObject(Doctor.class);
                switch (dc.getType()){
                    case ADDED:
                        doctorsList.add(doctor);
                        break;
                    case MODIFIED:
                        if(doctorsList.size() > 0){
                            for(Doctor d : doctorsList){
                                if(doctor.getEmail().equals(d.getEmail())){
                                    doctorsList.remove(d);
                                    doctorsList.add(doctor);
                                    break;
                                }
                            }
                        }
                        break;
                    case REMOVED:
                        doctorsList.remove(doctor);
                        break;
                }
            }
            initRecyclerView();
        }
    };
    private ListenerRegistration registration;

    public PatientFragment() {
        // Required empty public constructor
    }

    public PatientFragment(Patient patient) {
        this.patient = patient;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient, container, false);
        db = FirebaseFirestore.getInstance();
        query = db.collection("Doctor");
        findAll(view);
        patient_CHP_availability.setOnCheckedChangeListener(this);
        return view;
    }

    // Function that finds all views by id.
    private void findAll(View view) {
        patient_CHP_availability = view.findViewById(R.id.patient_CHP_availability);
        patient_RCV = view.findViewById(R.id.patient_RCV);
        patient_PGB = view.findViewById(R.id.patient_PGB);
    }

    // Function that initializes the recycler view and update the ui.
    private void initRecyclerView() {
        patient_RCV.setHasFixedSize(true);
        if(filteredByAvailability){
            doctorsListAdapter = new DoctorsListAdapter(filterListByAvailability(doctorsList), clickListener);
        } else {
            doctorsListAdapter = new DoctorsListAdapter(doctorsList, clickListener);
        }
        patient_RCV.setAdapter(doctorsListAdapter);
        patient_PGB.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        patient_PGB.setVisibility(View.VISIBLE);
        patient_RCV.removeAllViews();
        doctorsList.clear();
        registration = query.addSnapshotListener(eventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        registration.remove();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        patient_PGB.setVisibility(View.VISIBLE);
        patient_RCV.removeAllViews();
        filteredByAvailability = isChecked;
        initRecyclerView();
    }

    // Function that filters the doctors array and keeps all doctors that are available.
    private ArrayList<Doctor> filterListByAvailability(ArrayList<Doctor> arr){
        ArrayList<Doctor> filteredArray = new ArrayList<>();
        for(Doctor doctor : arr){
            if(doctor.isAvailable()){
                filteredArray.add(doctor);
            }
        }
        return filteredArray;
    }

    // Function that starts the appointment activity.
    private void startAppointmentActivity(Patient patient, Doctor doctor){
        Intent intent = new Intent(getContext(), AppointmentActivity.class);
        intent.putExtra("patient", patient);
        intent.putExtra("doctor", doctor);
        startActivity(intent);
    }

    // Function that gets a doctor object and add an appointment of the current user to the
    // doctor and save it in firebase.
    private void makeAnAppointment(Doctor doctor) {
        Appointment appointment = new Appointment(patient, new Timestamp(new Date()), false);
        db.collection("Doctor")
                .document(doctor.getEmail())
                .collection("waiting")
                .document(patient.getEmail())
                .set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "You made an appointment to doctor " + doctor.getLastName(), Toast.LENGTH_SHORT).show();
                        startAppointmentActivity(patient, doctor);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}