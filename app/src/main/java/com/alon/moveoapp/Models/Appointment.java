package com.alon.moveoapp.Models;

import com.google.firebase.Timestamp;

public class Appointment {

    private Patient patient;
    private Timestamp timestamp;
    private boolean isDone;

    public Appointment() {
    }

    public Appointment(Patient patient, Timestamp timestamp, boolean isDone) {
        this.patient = patient;
        this.timestamp = timestamp;
        this.isDone = isDone;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
