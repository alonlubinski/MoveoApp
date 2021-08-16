package com.alon.moveoapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alon.moveoapp.Models.Doctor;
import com.alon.moveoapp.R;
import com.google.android.material.button.MaterialButton;


import java.util.ArrayList;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.MyViewHolder> {

    private ArrayList<Doctor> dataSet;
    private OnItemClickListener onItemClickListener;

    public DoctorsListAdapter(ArrayList<Doctor> dataSet, OnItemClickListener onItemClickListener){
        this.dataSet = dataSet;
        this.onItemClickListener = onItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView doctors_list_LBL_name, doctors_list_LBL_availability;
        private MaterialButton doctors_list_BTN_appointment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            doctors_list_LBL_name = itemView.findViewById(R.id.doctors_list_LBL_name);
            doctors_list_LBL_availability = itemView.findViewById(R.id.doctors_list_LBL_availability);
            doctors_list_BTN_appointment = itemView.findViewById(R.id.doctors_list_BTN_appointment);
        }

        public void bind(final Doctor doctor, final OnItemClickListener onItemClickListener){
            doctors_list_LBL_name.setText("Name: " + doctor.getFirstName() + " " + doctor.getLastName());
            doctors_list_LBL_availability.setText("Available: " + (doctor.isAvailable() ? "Yes" : "No"));
            if(onItemClickListener != null){
                doctors_list_BTN_appointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(doctor);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_list_row, parent, false);
        DoctorsListAdapter.MyViewHolder vh = new DoctorsListAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorsListAdapter.MyViewHolder holder, int position) {
        holder.bind(dataSet.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Doctor doctor);
    }
}
