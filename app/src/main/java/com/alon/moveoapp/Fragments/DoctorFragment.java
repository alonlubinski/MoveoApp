package com.alon.moveoapp.Fragments;

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

import com.alon.moveoapp.Adapters.AppointmentsListAdapter;
import com.alon.moveoapp.Models.Appointment;
import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DoctorFragment extends Fragment {

    private Doctor doctor;
    private RecyclerView doctor_RCV;
    private SwitchMaterial doctor_SWT;
    private ProgressBar doctor_PGB;
    private ArrayList<Appointment> appointmentsList = new ArrayList<>();
    private ArrayList<Appointment> tempAppointmentsList = new ArrayList<>();
    private AppointmentsListAdapter appointmentsListAdapter;
    private FirebaseFirestore db;
    private Timer timer;

    private CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            doctor_PGB.setVisibility(View.VISIBLE);
            doctor.setAvailable(isChecked);
            Log.d("pttt", String.valueOf(doctor.isAvailable()));
            updateDoctorAvailability(doctor.isAvailable(), true);
            if(isChecked && appointmentsList.size() > 0){
                // If switch turned on and there are appointments - so start new timer and new timer task.
                timer = new Timer();
                timer.schedule(createNewTimerTask(), 0, 5000);
            } else {
                // If switch turned off and the timer is initialized - so cancel the timer.
                if(timer != null)
                    timer.cancel();
            }
        }
    };

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
        }
    };

    private ListenerRegistration registration;

    public DoctorFragment() {
        // Required empty public constructor
    }

    public DoctorFragment(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);
        db = FirebaseFirestore.getInstance();
        query = db.collection("Doctor").document(doctor.getEmail()).collection("waiting").orderBy("timestamp");
        findAll(view);
        doctor_PGB.setVisibility(View.GONE);
        setListeners();
        return view;
    }

    // Function that sets listeners.
    private void setListeners() {
        doctor_SWT.setOnCheckedChangeListener(switchListener);
    }

    // Function that gets information about if the doctor is available and if the activity is
    // shown on his device - and update the data on the firebase.
    private void updateDoctorAvailability(boolean isAvailable, boolean onScreen) {
        db.collection(doctor.getUserType()).document(doctor.getEmail())
                .update("available", isAvailable)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        doctor_PGB.setVisibility(View.GONE);
                        if(isAvailable && onScreen){
                            Toast.makeText(getContext(), "You are now available", Toast.LENGTH_SHORT).show();
                        } else if(onScreen){
                            Toast.makeText(getContext(), "You are now unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(onScreen) {
                            doctor_PGB.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                            doctor_SWT.setOnCheckedChangeListener(null);
                            doctor_SWT.setChecked(!doctor.isAvailable());
                            doctor.setAvailable(!doctor.isAvailable());
                            doctor_SWT.setOnCheckedChangeListener(switchListener);
                        }
                    }
                });
    }

    // Function that finds all views by id.
    private void findAll(View view) {
        doctor_RCV = view.findViewById(R.id.doctor_RCV);
        doctor_SWT = view.findViewById(R.id.doctor_SWT);
        doctor_PGB = view.findViewById(R.id.doctor_PGB);
    }

    @Override
    public void onResume() {
        super.onResume();
        doctor_PGB.setVisibility(View.VISIBLE);
        updateDoctorAvailability(doctor.isAvailable(), true);
        doctor_RCV.removeAllViews();
        appointmentsList.clear();
        tempAppointmentsList.clear();
        registration = query.addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
        if(doctor.isAvailable()){
            timer = new Timer();
            timer.schedule(createNewTimerTask(), 0,5000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateDoctorAvailability(false, false);
        registration.remove();
        if(timer != null)
            timer.cancel();
    }

    // Function that initializes the recycler view and update the ui.
    private void initRecyclerView() {
        doctor_RCV.setHasFixedSize(true);
        appointmentsListAdapter = new AppointmentsListAdapter(appointmentsList);
        doctor_RCV.setAdapter(appointmentsListAdapter);
        doctor_PGB.setVisibility(View.GONE);
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

    // Function that gets an appointment and updates that is done on firebase.
    private void updateAppointmentIsDone(Appointment appointment) {
        db.collection("Doctor")
                .document(doctor.getEmail())
                .collection("waiting")
                .document(appointment.getPatient().getEmail())
                .update("done", true);
    }

    // Function that create new timer task (every time we cancel timer task we can't use it again
    // so we need to create a new one every time).
    private TimerTask createNewTimerTask(){
        return new TimerTask() {
            @Override
            public void run() {
                if (appointmentsList.size() > 0) {
                    Appointment appointment = appointmentsList.get(0);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName() + " is here", Toast.LENGTH_SHORT).show();
                        }
                    });
                    updateAppointmentIsDone(appointment);
                }
            }
        };
    }
}