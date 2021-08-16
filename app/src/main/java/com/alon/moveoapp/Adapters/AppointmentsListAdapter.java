package com.alon.moveoapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alon.moveoapp.Models.Appointment;
import com.alon.moveoapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.MyViewHolder> {

    private ArrayList<Appointment> dataSet;

    public AppointmentsListAdapter(ArrayList<Appointment> dataSet){
        this.dataSet = dataSet;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView appointments_list_LBL_number, appointments_list_LBL_name, appointments_list_LBL_time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appointments_list_LBL_number = itemView.findViewById(R.id.appointments_list_LBL_number);
            appointments_list_LBL_name = itemView.findViewById(R.id.appointments_list_LBL_name);
            appointments_list_LBL_time = itemView.findViewById(R.id.appointments_list_LBL_time);
        }

        public void bind(final Appointment appointment, int number){
            appointments_list_LBL_number.setText(String.valueOf(number));
            appointments_list_LBL_name.setText("Name: " + appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
            Date date = (new Date(appointment.getTimestamp().toDate().getTime()));
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.getDefault());
            appointments_list_LBL_time.setText("Arrival Time: " + sfd.format(date));
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_list_row, parent, false);
        AppointmentsListAdapter.MyViewHolder vh = new AppointmentsListAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsListAdapter.MyViewHolder holder, int position) {
        holder.bind(dataSet.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
